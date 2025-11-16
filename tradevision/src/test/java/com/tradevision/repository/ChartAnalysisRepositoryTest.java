package com.tradevision.repository;

import com.tradevision.constant.CandleType;
import com.tradevision.constant.InvestmentLevel;
import com.tradevision.entity.ChartAnalysis;
import com.tradevision.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ChartAnalysisRepository 단위 테스트
 */
@DataJpaTest
@org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase(replace = org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.ANY)
@org.springframework.data.jpa.repository.config.EnableJpaAuditing
@DisplayName("ChartAnalysisRepository 테스트")
@org.springframework.test.context.TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class ChartAnalysisRepositoryTest {

    @Autowired
    private ChartAnalysisRepository chartAnalysisRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser1;
    private User testUser2;
    private ChartAnalysis analysis1;
    private ChartAnalysis analysis2;
    private ChartAnalysis analysis3;

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성
        testUser1 = User.builder()
                .email("user1@example.com")
                .password("hashedPassword1")
                .nickname("사용자1")
                .investmentLevel(InvestmentLevel.BEGINNER)
                .build();
        entityManager.persist(testUser1);

        testUser2 = User.builder()
                .email("user2@example.com")
                .password("hashedPassword2")
                .nickname("사용자2")
                .investmentLevel(InvestmentLevel.INTERMEDIATE)
                .build();
        entityManager.persist(testUser2);

        // 분석 결과 JSON (mock)
        String mockAnalysisJson = """
                {
                  "pattern": "상승 삼각형",
                  "trend": "상승",
                  "supportLevel": "68000",
                  "resistanceLevel": "72000",
                  "tradingOpinion": "매수",
                  "summary": "강한 상승 추세",
                  "riskLevel": "보통"
                }
                """;

        // 사용자1의 분석 데이터 (삼성전자)
        analysis1 = ChartAnalysis.builder()
                .user(testUser1)
                .stockCode("005930")
                .stockName("삼성전자")
                .candleType(CandleType.D)
                .imagePath("uploads/charts/1/test1.jpg")
                .analysisResult(mockAnalysisJson)
                .build();
        entityManager.persist(analysis1);

        // 사용자1의 분석 데이터 (SK하이닉스)
        analysis2 = ChartAnalysis.builder()
                .user(testUser1)
                .stockCode("000660")
                .stockName("SK하이닉스")
                .candleType(CandleType.W)
                .imagePath("uploads/charts/1/test2.jpg")
                .analysisResult(mockAnalysisJson)
                .build();
        entityManager.persist(analysis2);

        // 사용자2의 분석 데이터 (삼성전자)
        analysis3 = ChartAnalysis.builder()
                .user(testUser2)
                .stockCode("005930")
                .stockName("삼성전자")
                .candleType(CandleType.D)
                .imagePath("uploads/charts/2/test3.jpg")
                .analysisResult(mockAnalysisJson)
                .build();
        entityManager.persist(analysis3);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("사용자별 분석 히스토리 조회 - 성공")
    void findByUserIdOrderByCreatedAtDesc_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<ChartAnalysis> result = chartAnalysisRepository.findByUserIdOrderByCreatedAtDesc(
                testUser1.getId(), pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getUser().getId()).isEqualTo(testUser1.getId());
        assertThat(result.getContent().get(1).getUser().getId()).isEqualTo(testUser1.getId());
    }

    @Test
    @DisplayName("사용자별 분석 히스토리 조회 - 빈 결과")
    void findByUserIdOrderByCreatedAtDesc_EmptyResult() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Long nonExistentUserId = 999L;

        // when
        Page<ChartAnalysis> result = chartAnalysisRepository.findByUserIdOrderByCreatedAtDesc(
                nonExistentUserId, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("사용자별 특정 종목 분석 히스토리 조회 - 성공")
    void findByUserIdAndStockCodeOrderByCreatedAtDesc_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        String stockCode = "005930"; // 삼성전자

        // when
        Page<ChartAnalysis> result = chartAnalysisRepository.findByUserIdAndStockCodeOrderByCreatedAtDesc(
                testUser1.getId(), stockCode, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStockCode()).isEqualTo(stockCode);
        assertThat(result.getContent().get(0).getUser().getId()).isEqualTo(testUser1.getId());
    }

    @Test
    @DisplayName("사용자별 특정 종목 분석 히스토리 조회 - 다른 종목")
    void findByUserIdAndStockCodeOrderByCreatedAtDesc_DifferentStock() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        String stockCode = "000660"; // SK하이닉스

        // when
        Page<ChartAnalysis> result = chartAnalysisRepository.findByUserIdAndStockCodeOrderByCreatedAtDesc(
                testUser1.getId(), stockCode, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStockCode()).isEqualTo(stockCode);
        assertThat(result.getContent().get(0).getStockName()).isEqualTo("SK하이닉스");
    }

    @Test
    @DisplayName("사용자 ID와 분석 ID로 조회 - 성공")
    void findByIdAndUserId_Success() {
        // when
        Optional<ChartAnalysis> result = chartAnalysisRepository.findByIdAndUserId(
                analysis1.getId(), testUser1.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(analysis1.getId());
        assertThat(result.get().getStockCode()).isEqualTo("005930");
    }

    @Test
    @DisplayName("사용자 ID와 분석 ID로 조회 - 다른 사용자의 분석")
    void findByIdAndUserId_DifferentUser() {
        // when
        Optional<ChartAnalysis> result = chartAnalysisRepository.findByIdAndUserId(
                analysis1.getId(), testUser2.getId());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("사용자 ID와 분석 ID로 조회 - 존재하지 않는 분석")
    void findByIdAndUserId_NotFound() {
        // when
        Optional<ChartAnalysis> result = chartAnalysisRepository.findByIdAndUserId(
                999L, testUser1.getId());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("일일 분석 횟수 조회 - 오늘 분석")
    void countTodayAnalyses_TodayAnalyses() {
        // given
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);

        // when
        long count = chartAnalysisRepository.countTodayAnalyses(testUser1.getId(), todayStart);

        // then
        assertThat(count).isEqualTo(2); // analysis1, analysis2
    }

    @Test
    @DisplayName("일일 분석 횟수 조회 - 다른 사용자")
    void countTodayAnalyses_DifferentUser() {
        // given
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);

        // when
        long count = chartAnalysisRepository.countTodayAnalyses(testUser2.getId(), todayStart);

        // then
        assertThat(count).isEqualTo(1); // analysis3
    }

    @Test
    @DisplayName("일일 분석 횟수 조회 - 분석 없는 사용자")
    void countTodayAnalyses_NoAnalyses() {
        // given
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        Long nonExistentUserId = 999L;

        // when
        long count = chartAnalysisRepository.countTodayAnalyses(nonExistentUserId, todayStart);

        // then
        assertThat(count).isEqualTo(0);
    }

    @Test
    @DisplayName("기간별 분석 횟수 조회 - 성공")
    void countByUserIdAndCreatedAtBetween_Success() {
        // given
        LocalDateTime startDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endDate = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        // when
        long count = chartAnalysisRepository.countByUserIdAndCreatedAtBetween(
                testUser1.getId(), startDate, endDate);

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("기간별 분석 횟수 조회 - 과거 기간")
    void countByUserIdAndCreatedAtBetween_PastPeriod() {
        // given
        LocalDateTime startDate = LocalDateTime.of(LocalDate.now().minusDays(10), LocalTime.MIN);
        LocalDateTime endDate = LocalDateTime.of(LocalDate.now().minusDays(5), LocalTime.MAX);

        // when
        long count = chartAnalysisRepository.countByUserIdAndCreatedAtBetween(
                testUser1.getId(), startDate, endDate);

        // then
        assertThat(count).isEqualTo(0); // 오늘 생성된 데이터는 포함 안됨
    }

    @Test
    @DisplayName("페이징 - 첫 번째 페이지")
    void findByUserIdOrderByCreatedAtDesc_FirstPage() {
        // given
        Pageable pageable = PageRequest.of(0, 1);

        // when
        Page<ChartAnalysis> result = chartAnalysisRepository.findByUserIdOrderByCreatedAtDesc(
                testUser1.getId(), pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.hasNext()).isTrue();
    }

    @Test
    @DisplayName("페이징 - 두 번째 페이지")
    void findByUserIdOrderByCreatedAtDesc_SecondPage() {
        // given
        Pageable pageable = PageRequest.of(1, 1);

        // when
        Page<ChartAnalysis> result = chartAnalysisRepository.findByUserIdOrderByCreatedAtDesc(
                testUser1.getId(), pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.isLast()).isTrue();
        assertThat(result.hasPrevious()).isTrue();
    }
}
