package com.chatApp.webSocketAPI.Exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;

@RestController
public class GlobalException {
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorDetail> UserExceptionHandler(UserException e, WebRequest req){
        ErrorDetail errorDetail = new ErrorDetail(e.getMessage(), req.getDescription(false), LocalDateTime.now());

        return new ResponseEntity<ErrorDetail>(errorDetail, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetail> otherExceptionHandler(UserException e, WebRequest req){
        ErrorDetail errorDetail = new ErrorDetail(e.getMessage(), req.getDescription(false), LocalDateTime.now());

        return new ResponseEntity<ErrorDetail>(errorDetail, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MessageException.class)
    public ResponseEntity<ErrorDetail> MessageExceptionHandler(MessageException e, WebRequest req){
        ErrorDetail errorDetail = new ErrorDetail(e.getMessage(), req.getDescription(false), LocalDateTime.now());

        return new ResponseEntity<ErrorDetail>(errorDetail, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetail> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e, WebRequest req){

        String error = e.getBindingResult().getFieldError().getDefaultMessage();

        ErrorDetail errorDetail = new ErrorDetail("Validation Error", error, LocalDateTime.now());

        return new ResponseEntity<ErrorDetail>(errorDetail, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDetail> NoHandlerFoundExceptionHandler(NoHandlerFoundException e, WebRequest request){

        ErrorDetail errorDetail = new ErrorDetail("Endpoint Not Found", e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorDetail, HttpStatus.BAD_REQUEST);
    }
}
