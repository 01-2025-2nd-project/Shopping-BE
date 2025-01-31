package com.github.farmplus.web.advice;

import com.github.farmplus.service.exceptions.*;
import com.github.farmplus.web.dto.base.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;



@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ResponseDto handleNotFoundException(NotFoundException nfe){
        log.error("클라이언트 요청 이후 DB검색 중 발생한 에러입니다. " + nfe.getMessage());
        return new ResponseDto(HttpStatus.NOT_FOUND.value(),nfe.getMessage());
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StockShortageException.class)
    public ResponseDto handleStockShortageException(StockShortageException sse){
        log.error("클라이언의 요청이 잘못되어 발생한 에러입니다. " + sse.getMessage());
        return new ResponseDto(HttpStatus.BAD_REQUEST.value(), sse.getMessage());
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidDateException.class)
    public ResponseDto handleInvalidDateException(InvalidDateException ide){
        log.error("클라이언의 요청이 잘못되어 발생한 에러입니다. " + ide.getMessage());
        return new ResponseDto(HttpStatus.BAD_REQUEST.value(), ide.getMessage());
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ResponseDto handleBadRequestException(BadRequestException bre){
        log.error("클라이언의 요청이 잘못되어 발생한 에러입니다. " + bre.getMessage());
        return new ResponseDto(HttpStatus.BAD_REQUEST.value(), bre.getMessage());
    }
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedDeleteException.class)
    public ResponseDto handleUnauthorizedDeleteException(UnauthorizedDeleteException ude){
        log.error("클라이언트의 것이 아니어서 발생한 에러입니다." + ude.getMessage());
        return new ResponseDto(HttpStatus.UNAUTHORIZED.value(), ude.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(NotAcceptException.class)
    public ResponseDto handleNotAcceptException(NotAcceptException nae){
        log.error("클라이언트 요청 이후 DB검색 중 발생한 에러입니다. " + nae.getMessage());
        return new ResponseDto(HttpStatus.NOT_ACCEPTABLE.value(),nae.getMessage());
    }
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseDto handleAccessDeniedException(AccessDeniedException ade){
        log.error("Client 요청에 문제가 있어 다음처럼 출력합니다. " + ade.getMessage());
        return new ResponseDto(HttpStatus.FORBIDDEN.value(),ade.getMessage());
    }
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(CAuthenticationEntryPointException.class)
    public ResponseDto handleAuthenticationException(CAuthenticationEntryPointException ae){
        log.error("Client 요청에 문제가 있어 다음처럼 출력합니다. " + ae.getMessage());
        return new ResponseDto(HttpStatus.UNAUTHORIZED.value(),ae.getMessage());
    }






}
