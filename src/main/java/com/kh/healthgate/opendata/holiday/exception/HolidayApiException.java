package com.kh.healthgate.opendata.holiday.exception;

import com.kh.healthgate.common.exception.ProblemException;
import com.kh.healthgate.common.exception.ProblemType;

public class HolidayApiException extends ProblemException {
    public HolidayApiException(String message) {
        super(ProblemType.HOLIDAY_INFO_UNAVAILABLE, message);
    }
}