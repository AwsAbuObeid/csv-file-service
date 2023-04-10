package com.erabia.service.exception;


import com.erabia.service.enums.CSVExceptionType;

public class CSVException extends Exception{

    private final CSVExceptionType errorType;

    public CSVException(String message,CSVExceptionType errorType) {
        super(message);
        this.errorType=errorType;

    }

    public CSVException(String message, Throwable cause ,CSVExceptionType errorType) {
        super(message, cause);
        this.errorType=errorType;

    }

    public CSVException() {
        super();
        errorType=CSVExceptionType.GENERAL_ERROR;
    }
    public CSVExceptionType getErrorType() {
        return errorType;
    }

}
