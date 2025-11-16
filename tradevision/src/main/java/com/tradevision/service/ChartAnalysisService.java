package com.tradevision.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradevision.client.OpenAIClient;
import com.tradevision.constant.CandleType;
import com.tradevision.dto.request.ChartAnalysisRequest;
import com.tradevision.dto.response.ChartAnalysisResponse;
import com.tradevision.entity.ChartAnalysis;
import com.tradevision.entity.User;
import com.tradevision.exception.BusinessException;
import com.tradevision.exception.ErrorCode;
import com.tradevision.exception.ResourceNotFoundException;
import com.tradevision.repository.ChartAnalysisRepository;
import com.tradevision.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 차트 분석 서비스
 * 이미지 업로드, AI 분석, 히스토리 관리
 */
@Service
@RequiredArgsConstructor
public class ChartAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(ChartAnalysisService.class);
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png"};

    private final ChartAnalysisRepository chartAnalysisRepository;
    private final UserRepository userRepository;
    private final OpenAIClient openAIClient;
    private final ObjectMapper objectMapper;

    @Value("${app.upload.dir:uploads/charts}")
    private String uploadDir;

    @Value("${app.daily-analysis-limit:10}")
    private int dailyAnalysisLimit;

    /**
     * 차트 이미지 분석 요청
     *
     * @param request 분석 요청 정보
     * @param userId  사용자 ID
     * @return 분석 결과
     */
    @Transactional
    public ChartAnalysisResponse analyzeChart(ChartAnalysisRequest request, Long userId) {
        log.info("차트 분석 요청: userId={}, stockCode={}", userId, request.getStockCode());

        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));

        // 2. 일일 분석 횟수 확인
        checkDailyLimit(userId);

        // 3. 이미지 유효성 검증
        MultipartFile chartImage = request.getChartImage();
        validateImage(chartImage);

        // 4. 이미지 저장
        String imagePath = saveImage(chartImage, userId);

        try {
            // 5. 이미지를 Base64로 인코딩
            String base64Image = encodeImageToBase64(chartImage);

            // 6. AI 분석 프롬프트 생성
            String prompt = openAIClient.buildChartAnalysisPrompt(
                    request.getStockCode(),
                    request.getStockName(),
                    request.getCandleType().getDisplayName()
            );

            // 7. OpenAI API 호출하여 분석
            String analysisResultJson = openAIClient.analyzeChart(base64Image, prompt);

            // 8. 분석 결과 저장
            ChartAnalysis analysis = ChartAnalysis.builder()
                    .user(user)
                    .stockCode(request.getStockCode())
                    .stockName(request.getStockName())
                    .candleType(request.getCandleType())
                    .imagePath(imagePath)
                    .analysisResult(analysisResultJson)
                    .build();

            ChartAnalysis savedAnalysis = chartAnalysisRepository.save(analysis);

            log.info("차트 분석 완료: analysisId={}", savedAnalysis.getId());

            // 9. 응답 DTO 변환
            return convertToResponse(savedAnalysis);

        } catch (Exception e) {
            // 분석 실패 시 업로드된 이미지 삭제
            deleteImageFile(imagePath);
            log.error("차트 분석 실패: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.AI_ANALYSIS_FAILED);
        }
    }

    /**
     * 사용자의 분석 히스토리 조회
     *
     * @param userId   사용자 ID
     * @param pageable 페이징 정보
     * @return 분석 히스토리 페이지
     */
    @Transactional(readOnly = true)
    public Page<ChartAnalysisResponse> getAnalysisHistory(Long userId, Pageable pageable) {
        log.info("분석 히스토리 조회: userId={}", userId);

        Page<ChartAnalysis> analyses = chartAnalysisRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable);

        return analyses.map(this::convertToResponse);
    }

    /**
     * 특정 분석 결과 상세 조회
     *
     * @param analysisId 분석 ID
     * @param userId     사용자 ID
     * @return 분석 결과
     */
    @Transactional(readOnly = true)
    public ChartAnalysisResponse getAnalysisById(Long analysisId, Long userId) {
        log.info("분석 상세 조회: analysisId={}, userId={}", analysisId, userId);

        ChartAnalysis analysis = chartAnalysisRepository.findByIdAndUserId(analysisId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ANALYSIS_NOT_FOUND));

        return convertToResponse(analysis);
    }

    /**
     * 일일 분석 횟수 제한 확인
     *
     * @param userId 사용자 ID
     */
    private void checkDailyLimit(Long userId) {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        long todayCount = chartAnalysisRepository.countTodayAnalyses(userId, todayStart);

        if (todayCount >= dailyAnalysisLimit) {
            throw new BusinessException(ErrorCode.DAILY_LIMIT_EXCEEDED);
        }

        log.debug("오늘 분석 횟수: {}/{}", todayCount, dailyAnalysisLimit);
    }

    /**
     * 이미지 유효성 검증
     *
     * @param image 업로드된 이미지
     */
    private void validateImage(MultipartFile image) {
        // 파일 존재 확인
        if (image == null || image.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }

        // 파일 크기 확인
        if (image.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.IMAGE_SIZE_EXCEEDED);
        }

        // 파일 확장자 확인
        String originalFilename = image.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException(ErrorCode.INVALID_IMAGE_FORMAT);
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        boolean isValidExtension = false;
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equals(extension)) {
                isValidExtension = true;
                break;
            }
        }

        if (!isValidExtension) {
            throw new BusinessException(ErrorCode.INVALID_IMAGE_FORMAT);
        }

        log.debug("이미지 유효성 검증 통과: size={}KB, extension={}",
                image.getSize() / 1024, extension);
    }

    /**
     * 이미지 파일 저장
     *
     * @param image  업로드된 이미지
     * @param userId 사용자 ID
     * @return 저장된 이미지 경로
     */
    private String saveImage(MultipartFile image, Long userId) {
        try {
            // 업로드 디렉토리 생성
            Path uploadPath = Paths.get(uploadDir, String.valueOf(userId));
            Files.createDirectories(uploadPath);

            // 고유한 파일명 생성
            String originalFilename = image.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String newFilename = UUID.randomUUID().toString() + "." + extension;

            // 파일 저장
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String savedPath = uploadDir + "/" + userId + "/" + newFilename;
            log.info("이미지 저장 완료: {}", savedPath);

            return savedPath;

        } catch (IOException e) {
            log.error("이미지 저장 실패: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 이미지를 Base64로 인코딩
     *
     * @param image 업로드된 이미지
     * @return Base64 인코딩된 문자열
     */
    private String encodeImageToBase64(MultipartFile image) {
        try {
            byte[] imageBytes = image.getBytes();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            log.error("이미지 Base64 인코딩 실패: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 이미지 파일 삭제
     *
     * @param imagePath 이미지 경로
     */
    private void deleteImageFile(String imagePath) {
        try {
            Path path = Paths.get(imagePath);
            Files.deleteIfExists(path);
            log.info("이미지 삭제 완료: {}", imagePath);
        } catch (IOException e) {
            log.warn("이미지 삭제 실패: {}", imagePath, e);
        }
    }

    /**
     * 파일 확장자 추출
     *
     * @param filename 파일명
     * @return 확장자
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    /**
     * ChartAnalysis 엔티티를 Response DTO로 변환
     *
     * @param analysis ChartAnalysis 엔티티
     * @return ChartAnalysisResponse
     */
    private ChartAnalysisResponse convertToResponse(ChartAnalysis analysis) {
        try {
            // JSON 분석 결과를 AnalysisResult 객체로 파싱
            ChartAnalysisResponse.AnalysisResult analysisResult = objectMapper.readValue(
                    analysis.getAnalysisResult(),
                    ChartAnalysisResponse.AnalysisResult.class
            );

            return ChartAnalysisResponse.builder()
                    .analysisId(analysis.getId())
                    .stockCode(analysis.getStockCode())
                    .stockName(analysis.getStockName())
                    .candleType(analysis.getCandleType())
                    .imagePath(analysis.getImagePath())
                    .analysisResult(analysisResult)
                    .feedback(analysis.getFeedback())
                    .createdAt(analysis.getCreatedAt())
                    .build();

        } catch (Exception e) {
            log.error("분석 결과 JSON 파싱 실패: {}", e.getMessage(), e);
            // 파싱 실패 시 기본 응답 반환
            return ChartAnalysisResponse.builder()
                    .analysisId(analysis.getId())
                    .stockCode(analysis.getStockCode())
                    .stockName(analysis.getStockName())
                    .candleType(analysis.getCandleType())
                    .imagePath(analysis.getImagePath())
                    .feedback(analysis.getFeedback())
                    .createdAt(analysis.getCreatedAt())
                    .build();
        }
    }
}
