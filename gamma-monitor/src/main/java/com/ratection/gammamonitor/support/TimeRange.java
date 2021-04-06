package com.ratection.gammamonitor.support;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimeRange {
    private LocalDateTime start;
    private LocalDateTime end;

    private TimeRange(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("start should be earlier than end");
        }
        this.start = start;
        this.end = end;
    }

    public static TimeRange of(LocalDateTime start, LocalDateTime end) {
        return new TimeRange(start, end);
    }
}
