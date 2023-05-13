package io.github.adriaz.neovpcalculator.exception;

public class VacationPaymentBaseException extends Exception {
    private String message;

    public VacationPaymentBaseException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
