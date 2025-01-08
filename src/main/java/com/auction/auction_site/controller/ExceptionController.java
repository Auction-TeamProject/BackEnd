package com.auction.auction_site.controller;

import com.auction.auction_site.exception.EntityNotFound;
import com.auction.auction_site.dto.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@Slf4j
@RestControllerAdvice
public class ExceptionController { // 예외 처리용 컨트롤러
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse illegalExHandle(IllegalStateException e) {
        return new ErrorResponse("BAD_REQUEST", e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ErrorResponse handleAuthenticationException(AuthenticationException e) {
        return new ErrorResponse("UNAUTHORIZED", e.getMessage());
    }

    // AccessDeniedException 예외 처리 (권한 부족)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.error("AccessDeniedException 발생: {}", e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse("FORBIDDEN", "권한이 부족합니다.");
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);  // 403 응답
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException e) {
        log.error("EntityNotFoundException 발생: {}", e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse("NOT_FOUND", "해당 상품을 찾을 수 없습니다.");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);  // 404 응답
    }

//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    @ExceptionHandler(EntityNotFound.class)
//    public ErrorResponse handleEntityNotFound(EntityNotFound e) {
//        return new ErrorResponse("NOT_FOUND", e.getMessage());
//    }



}