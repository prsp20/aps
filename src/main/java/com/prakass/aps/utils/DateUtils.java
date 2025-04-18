package com.prakass.aps.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateUtils {

  public static ZonedDateTime getZonedDateTime() {
    return ZonedDateTime.now(ZoneId.of("UTC"));
  }

  public static Date convertZonedDateTimeToDate(ZonedDateTime zonedDateTime) {
    return Date.from(zonedDateTime.toInstant());
  }
}
