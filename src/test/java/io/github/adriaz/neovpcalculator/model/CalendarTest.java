package io.github.adriaz.neovpcalculator.model;

import io.github.adriaz.neovpcalculator.exception.NoHolidaysException;
import io.github.adriaz.neovpcalculator.exception.NoSuchYearException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
public class CalendarTest {
    @Value("${holidayCalendar.path}")
    private String calPath;
    @Test
    void whenDateIsHoliday_thenAssertionSucceeds() {
        Calendar calendar = new Calendar(calPath);
        LocalDate May9 = LocalDate.of(2023,5,9);
        boolean actual = false;
        try {
            actual = calendar.isHoliday(May9);
        } catch (NoSuchYearException | NoHolidaysException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(true, actual);
    }
}
