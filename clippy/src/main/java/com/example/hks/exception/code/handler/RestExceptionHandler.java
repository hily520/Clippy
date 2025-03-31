package com.example.hks.exception.code.handler;

import com.example.hks.exception.BusinessException;
import com.example.hks.exception.code.BaseResponseCode;
import com.example.hks.utils.DataResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 全局异常处理类
 */
@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(Exception.class)
    public DataResult handleException(Exception e) {
        log.error("handleException....{}", e);
        return DataResult.getResult(BaseResponseCode.SYSTEM_ERROR);
    }



    @ExceptionHandler(BusinessException.class)
    public DataResult handlerBusinessException(BusinessException e) {
        log.error("BusinessException ...{}", e);
        return DataResult.getResult(e.getCode(), e.getMsg());
    }

    /**
     * 校验的异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public DataResult handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        log.error("handlerMethodArgumentNotValidException  AllErrors:{} MethodArgumentNotValidException:{}",
                e.getBindingResult().getAllErrors(), e);
        String msg = null;
        for (ObjectError error : allErrors) {
            msg = error.getDefaultMessage();
            break;
        }
        return DataResult.getResult(BaseResponseCode.VALIDATOR_ERROR.getCode(), msg);
    }

}
