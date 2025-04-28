package store.mtvs.academyconnect.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.datetime.DateFormatterRegistrar;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 날짜/시간 관련 전역 설정 및 유틸리티 클래스
 */
@Configuration
public class ClockConfiguration {

    private static final String DATE_PATTERN = "yyyy/MM/dd";
    private static final String TIME_PATTERN = "HH:mm";
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
    private static final String FULL_DATE_TIME_PATTERN = "yyyy년 MM월 dd일 HH:mm";

    /**
     * 애플리케이션에서 사용할 Clock 빈 생성
     * @return 서울 시간 고정
     */
    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("Asia/Seoul"));
    }

    /**
     * 날짜/시간 포맷팅을 위한 ConversionService 빈 생성
     * @return 날짜/시간 포맷팅이 설정된 ConversionService
     */
    @Bean
    public FormattingConversionService conversionService() {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService(false);

        // 날짜 포맷 등록
        DateFormatterRegistrar dateRegistrar = new DateFormatterRegistrar();
        dateRegistrar.setFormatter(new DateFormatter(DATE_PATTERN));
        dateRegistrar.registerFormatters(conversionService);

        // LocalDateTime 등의 Java 8 날짜/시간 포맷 등록
        DateTimeFormatterRegistrar dateTimeRegistrar = new DateTimeFormatterRegistrar();
        dateTimeRegistrar.setDateFormatter(DateTimeFormatter.ofPattern(DATE_PATTERN));
        dateTimeRegistrar.setTimeFormatter(DateTimeFormatter.ofPattern(TIME_PATTERN));
        dateTimeRegistrar.setDateTimeFormatter(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
        dateTimeRegistrar.registerFormatters(conversionService);

        return conversionService;
    }

    /**
     * 현재 날짜/시간 반환
     * @param clock 시스템 시계
     * @return 현재 날짜/시간
     */
    public static LocalDateTime getCurrentDateTime(Clock clock) {
        return LocalDateTime.now(clock);
    }

    /**
     * 날짜 포맷팅 (yyyy/MM/dd)
     * @param date 포맷팅할 날짜
     * @return 포맷팅된 문자열
     */
    public static String formatDate(LocalDateTime date) {
        if (date == null) {
            return "";
        }
        return date.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
    }

    /**
     * 시간 포맷팅 (HH:mm)
     * @param time 포맷팅할 시간
     * @return 포맷팅된 문자열
     */
    public static String formatTime(LocalDateTime time) {
        if (time == null) {
            return "";
        }
        return time.format(DateTimeFormatter.ofPattern(TIME_PATTERN));
    }

    /**
     * 날짜/시간 포맷팅 (yyyy-MM-dd HH:mm)
     * @param dateTime 포맷팅할 날짜/시간
     * @return 포맷팅된 문자열
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
    }

    /**
     * 상세 날짜/시간 포맷팅 (yyyy년 MM월 dd일 HH:mm)
     * @param dateTime 포맷팅할 날짜/시간
     * @return 포맷팅된 문자열
     */
    public static String formatFullDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DateTimeFormatter.ofPattern(FULL_DATE_TIME_PATTERN));
    }
}