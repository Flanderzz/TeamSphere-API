package com.YipYapTimeAPI.YipYapTimeAPI.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@RestControllerAdvice
public class GlobalException {
    // TODO: think out and add more errors (too lazy atm)

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorDetail> UserExceptionHandler(UserException userException, WebRequest req){
        ErrorDetail error = new ErrorDetail(userException.getMessage(), req.getDescription(false), LocalDateTime.now().atOffset(ZoneOffset.UTC));

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MessageException.class)
    public ResponseEntity<ErrorDetail> MessageExceptionHandler(MessageException messageException,WebRequest req){

        ErrorDetail error = new ErrorDetail(messageException.getMessage(), req.getDescription(false), LocalDateTime.now().atOffset(ZoneOffset.UTC));

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetail> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException methodArgumentNotValidException){
        String error = Objects.requireNonNull(methodArgumentNotValidException.getBindingResult().getFieldError()).getDefaultMessage();

        ErrorDetail err =new ErrorDetail("Validation Error", error ,LocalDateTime.now().atOffset(ZoneOffset.UTC));

        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDetail> handleNoHandlerFoundException(NoHandlerFoundException noHandlerFoundException) {
        ErrorDetail error = new ErrorDetail("Endpoint not found", noHandlerFoundException.getMessage(), LocalDateTime.now().atOffset(ZoneOffset.UTC));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetail> otherErrorHandler(Exception e, WebRequest req){

        ErrorDetail error = new ErrorDetail(e.getMessage(), req.getDescription(false), LocalDateTime.now().atOffset(ZoneOffset.UTC));

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
