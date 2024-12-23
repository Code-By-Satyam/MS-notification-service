package com.microservice.notification.notification_service.advice;

import com.microservice.notification.notification_service.exception.InvalidRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class NotificationExceptionHandler {

//    @ExceptionHandler(InvalidRequestException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse ticketNotFoundException(InvalidRequestException e){
//        return new ErrorResponse() {
//            @Override
//            public HttpStatusCode getStatusCode() {
//                return HttpStatus.BAD_REQUEST;
//            }
//
//            @Override
//            public ProblemDetail getBody() {
//                return null;
//            }
//
//        };
//    }

//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorResponse handleGeneralException(Exception ex) {
//        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                "An error occurred: " + ex.getMessage());
//    }
}
