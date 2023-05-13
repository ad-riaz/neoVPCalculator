package io.github.adriaz.neovpcalculator.service;

import io.github.adriaz.neovpcalculator.exception.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@SpringBootTest
public class PaymentCalculationServiceTest {

    @Value("${paymentCalculationService.days_in_month}")
    double DAYS_IN_MONTH;

    @Autowired
    private PaymentCalculationService paymentService;

    @Test
    void WhenDatesAreCorrect_thenAssertionSucceeds() {
        String startVacationDate = "01-01-2022";
        String endVacationDate = "30-01-2022";
        int expected = 22;
        int actual = 0;

        try {
            actual = paymentService.calcVacationDuration(startVacationDate, endVacationDate);
        } catch (VacationPaymentBaseException e) {
            throw new RuntimeException(e);
        };

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void whenYearsAreDifferent_thenAssertionSucceeds() {
        String startVacationDate = "01-01-2022";
        String endVacationDate = "30-01-2023";

        Exception exception = assertThrows(Exception.class, () -> paymentService.calcVacationDuration(startVacationDate, endVacationDate));
        assertEquals("Годы во введенных датах начала и конца отпуска не совпадают", exception.getMessage());
    }

    @Test
    void whenFirstDateIsAfterAnother_thenAssertionSucceeds() {
        String startVacationDate = "01-01-2023";
        String endVacationDate = "30-01-2022";

        Exception exception = assertThrows(Exception.class, () -> paymentService.calcVacationDuration(startVacationDate, endVacationDate));
        assertEquals("Использован некорректный порядок дат начала и конца отпуска", exception.getMessage());
    }

    @Test
    void whenDateIsEmpty_thenAssertionSucceeds() {
        String startVacationDate = "";
        String endVacationDate = "30-01-2022";

        Exception exception = assertThrows(Exception.class, () -> paymentService.calcVacationDuration(startVacationDate, endVacationDate));
        assertEquals("Даты не могут быть пустыми", exception.getMessage());
    }

    @Test
    void whenSalaryAndVacationsDaysAreOk_thenAssertionSucceeds() {
        double avgSalary = 55123.45;
        int daysOfVacation = 14;
        double actual = 0;
        double expected = 0;

        try {
            actual = paymentService.calcPayment(avgSalary, daysOfVacation);
        } catch (IncorrectSalaryException | IncorrectVacationPeriodException e) {
            throw new RuntimeException(e);
        }

        // Средний дневной заработок = Выплаты в расчетном периоде / Количество отработанных дней
        double avgDaySalary = (double) Math.round(avgSalary / DAYS_IN_MONTH * 100) / 100;
        // Отпускные = Средний дневной заработок x Кол-во календарных дней отпуска
        expected = (double) Math.round(avgDaySalary * daysOfVacation * 100) / 100;
        // Отпускные за вычетом 13% НДФЛ
        expected = (double) Math.round(expected * 0.87 * 100) / 100;

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void whenSalaryIsNegative_thenAssertionSucceeds() {
        double avgSalary = -1000.0;
        int daysOfVacation = 14;

        Exception exception = assertThrows(Exception.class, () -> paymentService.calcPayment(avgSalary, daysOfVacation));
        assertEquals("Заработная плата не может быть меньше и равна 0", exception.getMessage());

    }

    @Test
    void whenVacationPeriodIsLessThenOneDay_thenAssertionSucceeds() {
        double avgSalary = 32569.50;
        int daysOfVacation = -14;

        Exception exception = assertThrows(Exception.class, () -> paymentService.calcPayment(avgSalary, daysOfVacation));
        assertEquals("Отпуск не может быть короче одного дня", exception.getMessage());

    }
}

