package com.BE.utils;


import com.BE.enums.TimeRangePreset;
import org.springframework.data.util.Pair;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;

public class TimeRangeUtil {
    public static Pair<LocalDate, LocalDate> resolve(TimeRangePreset preset) {
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        return switch (preset) {
            case TODAY -> Pair.of(now, now);
            case YESTERDAY -> Pair.of(now.minusDays(1), now.minusDays(1));
            case LAST_7_DAYS -> Pair.of(now.minusDays(6), now);
            case LAST_30_DAYS -> Pair.of(now.minusDays(29), now);
            case THIS_WEEK -> Pair.of(now.with(DayOfWeek.MONDAY), now.with(DayOfWeek.SUNDAY));
            case LAST_WEEK -> {
                LocalDate start = now.minusWeeks(1).with(DayOfWeek.MONDAY);
                yield Pair.of(start, start.plusDays(6));
            }
            case THIS_MONTH -> Pair.of(now.withDayOfMonth(1), now.withDayOfMonth(now.lengthOfMonth()));
            case LAST_MONTH -> {
                LocalDate lastMonth = now.minusMonths(1);
                yield Pair.of(lastMonth.withDayOfMonth(1), lastMonth.withDayOfMonth(lastMonth.lengthOfMonth()));
            }
            case THIS_YEAR -> Pair.of(now.withDayOfYear(1), now.withMonth(12).withDayOfMonth(31));
            case CUSTOM -> throw new IllegalArgumentException("CUSTOM is not handled here (use fromDate/toDate instead)");
        };
    }
}
