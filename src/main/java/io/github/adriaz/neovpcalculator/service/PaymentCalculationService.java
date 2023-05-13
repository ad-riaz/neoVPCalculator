package io.github.adriaz.neovpcalculator.service;

import io.github.adriaz.neovpcalculator.exception.*;
import io.github.adriaz.neovpcalculator.model.Calendar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class PaymentCalculationService {
    // Если месяц отработан полностью, то количество дней считается равным 29.3
    @Value("${paymentCalculationService.days_in_month}")
    private double DAYS_IN_MONTH = 29.3;
    @Value("${holidayCalendar.path}")
    private String calPath;

    public int calcVacationDuration(String startVacationDate, String endVacationDate) throws VacationPaymentBaseException {
        int holidays = 0;
        int vacationDuration = 0;
        LocalDate start = null;
        LocalDate end = null;
        DateTimeFormatter inputDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        Calendar calendar = new Calendar(calPath);

        if (startVacationDate.equals("") || startVacationDate == null || endVacationDate.equals("") || endVacationDate == null) {
            throw new IncorrectVacationPeriodException("Даты не могут быть пустыми");
        }

        try {
            start = LocalDate.parse(startVacationDate, inputDateFormatter);
            end = LocalDate.parse(endVacationDate, inputDateFormatter);
        } catch (DateTimeParseException e) {
            throw new IncorrectDateTimeFormatException("Дата(ы) введены в неправильном формате");
        }

        if (start.isAfter(end)) {
            throw new IncorrectDateOrderException("Даты введены в неправильном порядке");
        }

        if (start.getYear() != end.getYear()) {
            throw new IncorrectDateOrderException("Годы во введенных датах начала и конца отпуска не совпадают");
        }

        // Посчитать количество календарных дней отпуска
        vacationDuration = Period.between(start, end).getDays() + 1;

        // Посчитать количество праздничных дней, которые выпадают на время отпуска
        for (int i = 0; i < vacationDuration; i++){
            LocalDate day = start.plusDays(i);

            if (calendar.isHoliday(day)) {
                holidays++;
            }
        }
        // Вычесть из дней отпуска неоплачиваемые праздничные дни
        vacationDuration -= holidays;
        return vacationDuration;
    }

    public double calcPayment(double avgSalary, int vacationDays) throws IncorrectSalaryException, IncorrectVacationPeriodException {
        if (avgSalary <= 0) {
            throw new IncorrectSalaryException("Заработная плата не может быть меньше или равна 0");
        }

        if (vacationDays < 1) {
            throw new IncorrectVacationPeriodException("Отпуск не может быть короче одного дня");
        }

        // Средний дневной заработок = Выплаты в расчетном периоде / Количество отработанных дней
        double avgDaySalary = (double) Math.round(avgSalary / DAYS_IN_MONTH * 100) / 100;
        // Отпускные = Средний дневной заработок x Кол-во календарных дней отпуска
        double vacationPayment = (double) Math.round(avgDaySalary * vacationDays * 100) / 100;
        // Отпускные за вычетом 13% НДФЛ
        vacationPayment = (double) Math.round(vacationPayment * 0.87 * 100) / 100;

        return vacationPayment;
    }
}
