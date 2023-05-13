package io.github.adriaz.neovpcalculator.controller;

import io.github.adriaz.neovpcalculator.exception.*;
import io.github.adriaz.neovpcalculator.service.PaymentCalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(
        name = "Калькулятор отпускных",
        description = "Содержит метод(ы) для работы с отпускными отчислениями"
)
public class PaymentController {
    @Autowired
    private PaymentCalculationService paymentService;

    @GetMapping("calculate")
    @Operation(summary = "Позволяет вычислить сумму отпускных отчислений")
    public ResponseEntity calcVacationPayment(@RequestParam @Parameter(description = "Средняя месячная заработная плата") double avgSalary,
                                              @RequestParam @Parameter(description = "Дата начала отпуска в формате dd-MM-yyyy") String startDate,
                                              @RequestParam @Parameter(description = "Дата конца отпуска в формате dd-MM-yyyy") String endDate) {
        try {
            int vacationDuration = paymentService.calcVacationDuration(startDate, endDate);
            double payment = paymentService.calcPayment(avgSalary, vacationDuration);
            return new ResponseEntity(payment, HttpStatus.OK);
        } catch (NoSuchYearException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (VacationPaymentBaseException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
