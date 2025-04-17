package com.prakass.aps.utils;

import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class DateUtils {

    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.now(ZoneId.of("UTC"));
    }

    public Date convertZonedDateTimeToDate(ZonedDateTime zonedDateTime) {
        return Date.from(zonedDateTime.toInstant());
    }
}
