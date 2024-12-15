package com.example.demo.security;

import com.example.demo.security.Exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice //глобальная обработка ошибок в приложении Spring
@Slf4j //логгирование
public class HandleTheException {
    @ExceptionHandler
    public ResponseEntity<AppError> catchDataNotFound(DataNotFound e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), e.getMessage()), HttpStatus.NOT_FOUND);}
    @ExceptionHandler
    public ResponseEntity<AppError> catchSaveDataException(SaveDataException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);}
    @ExceptionHandler
    public ResponseEntity<AppError> catchDeleteDataException(DeleteDataException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);}
    @ExceptionHandler
    public ResponseEntity<AppError> catchUpdateDataException(UpdateDataException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);}
}



