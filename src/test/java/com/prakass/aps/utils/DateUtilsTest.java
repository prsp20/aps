package com.prakass.aps.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import org.junit.jupiter.api.Test;

public class DateUtilsTest {

  @Test
  void testGetZonedDateTimeReturnsUTCZone() {
    ZonedDateTime zonedDateTime = DateUtils.getZonedDateTime();
    assertNotNull(zonedDateTime, "ZonedDateTime should not be null");
    assertEquals(ZoneId.of("UTC"), zonedDateTime.getZone(), "ZoneId should be UTC");
  }

  @Test
  void testConvertZonedDateTimeToDate() {
    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
    Date date = DateUtils.convertZonedDateTimeToDate(now);
    assertNotNull(date, "Converted Date should not be null");

    long difference = Math.abs(date.toInstant().toEpochMilli() - now.toInstant().toEpochMilli());
    assertTrue(
        difference < 1000, "The converted Date should closely match the original ZonedDateTime");
  }
}
