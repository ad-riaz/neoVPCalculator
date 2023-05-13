package io.github.adriaz.neovpcalculator.controller;

import io.github.adriaz.neovpcalculator.exception.*;
import io.github.adriaz.neovpcalculator.service.PaymentCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {
    @Autowired
    private PaymentCalculationService paymentService;

    @GetMapping("calculate")
    public ResponseEntity calcVacationPayment(@RequestParam double avgSalary, @RequestParam String startDate, @RequestParam String endDate) {
        try {
            int vacationDuration = paymentService.calcVacationDuration(startDate, endDate);
            double payment = paymentService.calcPayment(avgSalary, vacationDuration);
            return new ResponseEntity(payment, HttpStatus.OK);
        } catch (NoSuchYearException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (VacationPaymentBaseException e1) {
            return new ResponseEntity(e1.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
