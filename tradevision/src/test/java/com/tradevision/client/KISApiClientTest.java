package com.tradevision.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * KISApiClient 기본 테스트
 * Note: WebClient를 사용한 실제 API 호출 테스트는 통합 테스트에서 진행
 * 여기서는 클래스의 기본 구조만 검증
 */
@DisplayName("KISApiClient 기본 테스트")
class KISApiClientTest {

    @Test
    @DisplayName("KISApiClient 클래스 존재 확인")
    void kisApiClientExists() {
        // when
        Class<?> clazz = KISApiClient.class;

        // then
        assertThat(clazz).isNotNull();
        assertThat(clazz.getName()).isEqualTo("com.tradevision.client.KISApiClient");
    }

    @Test
    @DisplayName("KISApiClient에 필수 메서드 존재 확인")
    void requiredMethodsExist() throws NoSuchMethodException {
        // when
        Class<?> clazz = KISApiClient.class;

        // then
        assertThat(clazz.getMethod("getAccessToken")).isNotNull();
        assertThat(clazz.getMethod("getStockPrice", String.class, com.tradevision.constant.CandleType.class)).isNotNull();
    }
}
