package io.github.adriaz.neovpcalculator.service;

import io.github.adriaz.neovpcalculator.exception.*;
import io.github.adriaz.neovpcalculator.model.Calendar;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Service
public class PaymentCalculationService {
    // Если месяц отработан полностью, то количество дней считается равным 29.3
//    @Value("${DAYS_IN_MONTH}")
    private static double DAYS_IN_MONTH = 29.3;
    public int calcVacationDuration(String startVacationDate, String endVacationDate) throws IncorrectDateOrderException, NoSuchYearException, NoHolidaysException {
        int holidays = 0;
        int vacationDuration = 0;
        DateTimeFormatter inputDateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        Calendar calendar = new Calendar("src/main/resources/calendar.json");

        // TODO: добавить тесты
        LocalDate start = LocalDate.parse(startVacationDate, inputDateFormatter);
        LocalDate end = LocalDate.parse(endVacationDate, inputDateFormatter);

        if (start.isAfter(end)) {
            throw new IncorrectDateOrderException("Использован некорректный порядок дат начала и конца отпуска");
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

        System.out.println("Начало отпуска: " + start);
        System.out.println("Конец отпуска: " + end);
        System.out.println("Отпуск длится " + vacationDuration + " дней");
        System.out.println("На весь отпуск выпадает " + holidays + " неоплачиваемых праздников");
        // Вычесть из дней отпуска неоплачиваемые праздничные дни
        vacationDuration -= holidays;
        System.out.println("Количество дней, за которые положены отпускные: " + vacationDuration + "\n\n");

        return vacationDuration;
    }

    public double calcPayment(double avgSalary, int vacationDays) throws IncorrectSalaryException, IncorrectVacationPeriodException {
        // TODO: добавить тесты
        if (avgSalary <= 0) {
            throw new IncorrectSalaryException("Заработная плата не может быть меньше и равна 0");
        }

        if (vacationDays < 1) {
            throw new IncorrectVacationPeriodException("Отпуск не может быть короче одного дня");
        }

        // Средний дневной заработок = Выплаты в расчетном периоде / Количество отработанных дней
        double avgDaySalary = (double) Math.round(avgSalary / DAYS_IN_MONTH * 100) / 100;
        System.out.println("Средний дневной заработок составляет " + avgDaySalary);

        // Отпускные = Средний дневной заработок x Кол-во календарных дней отпуска
        double vacationPayment = (double) Math.round(avgDaySalary * vacationDays * 100) / 100;
        System.out.println("Размер отпускных до вычета НДФЛ составляет " + vacationPayment);

        // Отпускные за вычетом 13% НДФЛ
        vacationPayment = (double) Math.round(vacationPayment * 0.87 * 100) / 100;
        System.out.println("Размер отпускных после вычета НДФЛ составляет " + vacationPayment);

        return vacationPayment;
    }
}
