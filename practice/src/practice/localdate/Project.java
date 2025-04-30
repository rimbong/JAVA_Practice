package practice.localdate;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/* 
    LocalDate와 LocalDateTime은 Java 8 이후 시간과 날짜를 다루는 데 매우 자주 사용되는 클래스입니다. 
    특히, 기존의 Date나 Calendar 클래스에 비해 직관적이고 스레드 안전하며, 다양한 날짜/시간 연산을 쉽게 처리할 수 있어 실무에서 표준으로 자리 잡았습니다. 
    질문에서 제공된 코드처럼 예약 시스템에서 날짜 필터링, 시간 범위 검증 등에 자주 활용됩니다.
    

1. **날짜/시간 파싱 및 포맷팅**:
   - 데이터베이스, API, 사용자 입력에서 받은 문자열을 `LocalDate` 또는 `LocalDateTime`으로 변환.
   - 다양한 포맷(예: `yyyy-MM-dd`, `yyyyMMdd HHmmss`) 지원.

2. **날짜/시간 비교**:
   - 예약 기간, 이벤트 기간, 유효성 체크 등에서 `isBefore`, `isAfter`, `isEqual` 사용.

3. **날짜/시간 연산**:
   - 날짜 더하기/빼기(`plusDays`, `minusMonths`), 특정 요일로 조정(`TemporalAdjusters`).

4. **기간 계산**:
   - 두 날짜 간 차이 계산(`Period`, `Duration`, `ChronoUnit`).

5. **특정 날짜 기준 필터링**:
   - 주간, 월간, 연간 데이터 필터링(예: 주간 보고서, 월별 예약).

6. **현재 날짜/시간 처리**:
   - `LocalDate.now()`, `LocalDateTime.now()`로 현재 시점 기준 로직 구현.

7. **타임존 처리**:
   - `ZonedDateTime`과 연계해 글로벌 애플리케이션에서 타임존 고려.

l
 */
public class Project {

	public static void main(String[] args) {
		test7();
	}

/*  #### 1. 날짜/시간 파싱 및 포맷팅
    시나리오**: API에서 받은 날짜 문자열을 파싱하고, 다른 포맷으로 출력.
 */
    public static void test1(){
        // API에서 받은 문자열
        String dateStr = "20250425";
        String dateTimeStr = "20250425 143000";

        // 포맷터 정의
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 파싱
        LocalDate date = LocalDate.parse(dateStr, dateFormatter);
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, dateTimeFormatter);

        // 출력 포맷팅
        //  DateTimeFormatter.ISO_LOCAL_DATE => yyyy-MM-dd
        System.out.println("Parsed Date: " + date.format(DateTimeFormatter.ISO_LOCAL_DATE)); // 2025-04-25
        System.out.println("Parsed DateTime: " + dateTime.format(outputFormatter)); // 2025-04-25 14:30:00

        // 연습: 다른 포맷으로 파싱 시도
        String anotherDateStr = "2025-04-25";
        DateTimeFormatter anotherFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate anotherDate = LocalDate.parse(anotherDateStr, anotherFormatter);
        System.out.println("Another Date: " + anotherDate); // 2025-04-25

        // 연습2
        String dateTimeStr2 = "2025-04-25T14:30:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        try {
            LocalDateTime dateTime2 = LocalDateTime.parse(dateTimeStr2, formatter);
            System.out.println("Parsed DateTime: " + dateTime2); // 2025-04-25T14:30
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format: " + dateTimeStr2);
            throw new IllegalArgumentException("Date format must be yyyy-MM-dd'T'HH:mm:ss", e);
        }

        // 잘못된 입력 테스트
        String invalidDateTimeStr = "2025-04-25 14:30:00";
        try {
            LocalDateTime invalidDateTime = LocalDateTime.parse(invalidDateTimeStr, formatter);
            System.out.println("This won't print: " + invalidDateTime);
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format: " + invalidDateTimeStr); // 에러 출력
        }
    }
/* 
 * #### 2. 날짜/시간 비교
    시나리오**: 예약 시간이 특정 범위 내에 있는지 확인 
 */
    public static void test2(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
        LocalDateTime startTime = LocalDateTime.parse("20250425 090000", formatter);
        LocalDateTime endTime = LocalDateTime.parse("20250425 183000", formatter);

        LocalDateTime rangeStart = LocalDateTime.parse("20250425 090000", formatter);
        LocalDateTime rangeEnd = LocalDateTime.parse("20250425 180000", formatter);

        // 시간 범위 체크
        System.out.println(startTime.isBefore(rangeStart)); // false
        System.out.println(endTime.isAfter(rangeEnd)); // true
        boolean isValid = !startTime.isBefore(rangeStart) && !endTime.isAfter(rangeEnd);
        System.out.println("Is reservation valid? " + isValid); // false (18:30:00 > 18:00:00)

        // 분 단위 비교
        boolean isEndTimeValid = endTime.isBefore(rangeEnd) || 
                                 (endTime.getHour() == rangeEnd.getHour() && endTime.getMinute() == 0);
        System.out.println("Is end time valid? " + isEndTimeValid); // false

        // 연습: 시작 시간과 종료 시간이 같은 날인지 확인
        boolean isSameDay = startTime.toLocalDate().equals(endTime.toLocalDate());
        System.out.println("Is same day? " + isSameDay); // true

        // 연습2
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
        LocalDateTime startTime2 = LocalDateTime.parse("20250425 090000", formatter2);
        LocalDateTime endTime2 = LocalDateTime.parse("20250425 183000", formatter2);

        // 시작 시간 > 종료 시간 체크
        try {
            validateTimeOrder(startTime2, endTime2);
            System.out.println("Time order is valid");
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }

        // 9:00~17:00 범위 체크
        boolean isInBusinessHours = isWithinBusinessHours(startTime2, endTime2);
        System.out.println("Within 9:00-17:00? " + isInBusinessHours); // false (18:30 > 17:00)

    }
    
    private static void validateTimeOrder(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must not be after end time");
        }
    }

    private static boolean isWithinBusinessHours(LocalDateTime startTime, LocalDateTime endTime) {
        LocalTime businessStart = LocalTime.of(9, 0);
        LocalTime businessEnd = LocalTime.of(17, 0);

        LocalTime start = startTime.toLocalTime();
        LocalTime end = endTime.toLocalTime();

        return !start.isBefore(businessStart) && !end.isAfter(businessEnd);
    }

/* 
 * #### 3. 날짜/시간 연산
 * 시나리오**: 예약 날짜를 기준으로 7일 후, 다음 월요일, 특정 월의 마지막 날 계산.
 */
    public static void test3(){
        LocalDate today = LocalDate.of(2025, 4, 25);

        // 7일 후
        LocalDate after7Days = today.plusDays(7);
        System.out.println("After 7 days: " + after7Days); // 2025-05-02

        // 다음 월요일
        LocalDate nextMonday = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        System.out.println("Next Monday: " + nextMonday); // 2025-04-28

        // 이번 달 마지막 날
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        System.out.println("Last day of month: " + lastDayOfMonth); // 2025-04-30

        // 3개월 전
        LocalDate threeMonthsAgo = today.minusMonths(3);
        System.out.println("Three months ago: " + threeMonthsAgo); // 2025-01-25

        // 연습: 다음 달의 첫 번째 금요일 계산
        LocalDate nextMonthFirstFriday = today.plusMonths(1)
                .with(TemporalAdjusters.firstInMonth(DayOfWeek.FRIDAY));
        System.out.println("Next month's first Friday: " + nextMonthFirstFriday); // 2025-05-02

        // 연습2
        LocalDate date = LocalDate.of(2025, 4, 25); // 금요일
        LocalDateTime dateTime = LocalDateTime.of(2025, 4, 25, 14, 30);

        // 2주 후 같은 요일
        LocalDate twoWeeksLaterSameDay = date.plusWeeks(2);
        System.out.println("Two weeks later (same day): " + twoWeeksLaterSameDay); // 2025-05-09 (금요일)

        // 30분 후
        LocalDateTime thirtyMinutesLater = dateTime.plusMinutes(30);
        System.out.println("30 minutes later: " + thirtyMinutesLater); // 2025-04-25T15:00

        // 요일 확인
        System.out.println("Original day: " + date.getDayOfWeek()); // FRIDAY
        System.out.println("Two weeks later day: " + twoWeeksLaterSameDay.getDayOfWeek()); // FRIDAY
    }
/* 
#### 4. 기간 계산
**시나리오**: 두 예약 날짜 간의 일수, 시간 차이 계산.
 * 
 */
    public static void test4(){
        LocalDate startDate = LocalDate.of(2025, 4, 25);
        LocalDate endDate = LocalDate.of(2025, 5, 10);

        // 일수 차이 (Period)
        Period period = Period.between(startDate, endDate);
        System.out.println("Days between: " + period.getDays()); // 15
        System.out.println("Months: " + period.getMonths() + ", Days: " + period.getDays()); // Months: 0, Days: 15

        // 총 일수 (ChronoUnit)
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        System.out.println("Total days: " + daysBetween); // 15

        // 시간 차이 (LocalDateTime)
        LocalDateTime startTime = LocalDateTime.of(2025, 4, 25, 9, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 4, 25, 17, 30);
        Duration duration = Duration.between(startTime, endTime);
        System.out.println("Hours between: " + duration.toHours()); // 8
        System.out.println("Minutes: " + duration.toMinutes()); // 510
        
        // 연습: 두 날짜가 같은 주인지 확인
        LocalDate weekStart = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        System.out.println("weekStart : " + weekStart); // 2025-04-21
        // WEEK_OF_WEEK_BASED_YEAR => 주 기반 연도(week-based year)"에서의 주 번호
        boolean isSameWeek = endDate.getYear() == weekStart.getYear() &&
                             endDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) == weekStart.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        System.out.println(weekStart.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
        System.out.println("Is same week? " + isSameWeek); // false
    }

/* 
*    #### 5. 특정 날짜 기준 필터링
    **시나리오**: 질문 코드처럼 예약 목록을 일별, 주별, 월별로 필터링.
*/
    public static void test5(){
        List<Reservation> reservations = Arrays.asList(
                new Reservation("20250425 090000", "20250425 180000"),
                new Reservation("20250420 100000", "20250422 170000"),
                new Reservation("20250501 140000", "20250501 160000")
        );

        String year = "2025", month = "04", day = "25", filter = "w";
        List<Reservation> filtered = filterReservations(reservations, year, month, day, filter);
        filtered.forEach(r -> System.out.println(r.startTime + " ~ " + r.endTime));
    }

    static class Reservation {
        String startTime;
        String endTime;
    
        Reservation(String startTime, String endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    private static List<Reservation> filterReservations(List<Reservation> list, String year, String month, String day, String filter) {
        if (!Arrays.asList("d", "w", "m", "y", "D", "W", "M", "Y").contains(filter)) {
            filter = "m"; // 기본값: 월별
        }
        if ("m".equalsIgnoreCase(filter)) {
            return list;
        }

        LocalDate targetDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
        String finalFilter = filter;
        LocalTime businessStart = LocalTime.of(9, 0);
        LocalTime businessEnd = LocalTime.of(17, 0);

        return list.stream()
                .filter(res -> {
                    LocalDateTime startDateTime = LocalDateTime.parse(res.startTime, formatter);
                    LocalDateTime endDateTime = LocalDateTime.parse(res.endTime, formatter);
                    LocalDate startDate = startDateTime.toLocalDate();
                    LocalDate endDate = endDateTime.toLocalDate();

                    // 9:00~17:00 시간대 체크 특정 - 시간대(예: 9:00~17:00) 내 예약만 필터링
                    LocalTime startTime = startDateTime.toLocalTime();
                    LocalTime endTime = endDateTime.toLocalTime();                    
                    boolean isWithinBusinessHours = !startTime.isBefore(businessStart) && !endTime.isAfter(businessEnd);

                    if ("d".equalsIgnoreCase(finalFilter)) {
                        return isWithinBusinessHours && !startDate.isAfter(targetDate) && !endDate.isBefore(targetDate);
                    } else if ("w".equalsIgnoreCase(finalFilter)) {
                        LocalDate weekStart = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                        LocalDate weekEnd = targetDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                        return isWithinBusinessHours && !startDate.isAfter(weekEnd) && !endDate.isBefore(weekStart);
                    } else if ("y".equalsIgnoreCase(finalFilter)) {
                        return isWithinBusinessHours && startDate.getYear() == targetDate.getYear() &&
                               endDate.getYear() == targetDate.getYear();
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

/* 
 *  #### 6. 타임존 처리
    **시나리오**: 글로벌 예약 시스템에서 타임존 변환.
 */
    public static void test6(){
         // 서울 시간으로 예약 생성
         ZonedDateTime seoulTime = ZonedDateTime.of(2025, 4, 25, 14, 30, 0, 0, ZoneId.of("Asia/Seoul"));
         System.out.println("Seoul time: " + seoulTime);
 
         // 뉴욕 시간으로 변환
         ZonedDateTime newYorkTime = seoulTime.withZoneSameInstant(ZoneId.of("America/New_York"));
         System.out.println("New York time: " + newYorkTime);
 
         // ISO 포맷으로 출력
         DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
         System.out.println("ISO format: " + seoulTime.format(formatter));
 
         // 연습: UTC로 변환
         ZonedDateTime utcTime = seoulTime.withZoneSameInstant(ZoneId.of("UTC"));
         System.out.println("UTC time: " + utcTime);

        //  연습2
        // 특정 타임존 현재 시간
        ZonedDateTime seoulTime2 = getCurrentTimeInZone("Asia/Seoul");
        System.out.println("Seoul time: " + seoulTime2);

        // 두 타임존 간 시간 차이
        long hoursDiff = getTimeZoneHourDifference("Asia/Seoul", "America/New_York");
        System.out.println("Hours difference (Seoul-NY): " + hoursDiff); // 13
    }

    private static ZonedDateTime getCurrentTimeInZone(String zoneId) {
        return ZonedDateTime.now(ZoneId.of(zoneId));
    }

    private static long getTimeZoneHourDifference(String zoneId1, String zoneId2) {
        ZonedDateTime time1 = ZonedDateTime.now(ZoneId.of(zoneId1));
        ZonedDateTime time2 = ZonedDateTime.now(ZoneId.of(zoneId2));
        return ChronoUnit.HOURS.between(time2, time1);
    }

    // Calendar UI - 첫주 일요일  부터 마지막주 토욜까지 나온다.
    public static void test7(){
        List<LocalDate> days = getCalendarDays(2025, 4);
        days.forEach(date -> {
            int weekday = date.getDayOfWeek().getValue() % 7 + 1; // 1=Sunday, ..., 7=Saturday
            System.out.printf("%s (Weekday: %d)%n", date, weekday);
        });
    }

    public static List<LocalDate> getCalendarDays(int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }

        List<LocalDate> days = new ArrayList<>();
        LocalDate startDate = LocalDate.of(year, month, 1)
                .with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY));
        LocalDate endDate = LocalDate.of(year, month, 1)
                .plusMonths(1)
                .with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SATURDAY));

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            days.add(current);
            current = current.plusDays(1);
        }

        return days;
    }

}
