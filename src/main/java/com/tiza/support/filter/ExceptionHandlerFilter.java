package com.tiza.support.filter;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 处理全局异常
 *
 * Description: ExceptionHandlerFilter
 * Author: DIYILIU
 * Update: 2018-05-11 10:09
 */

@Controller
@ControllerAdvice
public class ExceptionHandlerFilter {

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String noHandlerFound() {

        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String defaultErrorHandler(Exception e) {
        e.printStackTrace();

        return "error/500";
    }
}
