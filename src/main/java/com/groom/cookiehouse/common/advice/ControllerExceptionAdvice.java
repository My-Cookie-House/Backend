package com.groom.cookiehouse.common.advice;

import com.groom.cookiehouse.common.dto.BaseResponse;
import com.groom.cookiehouse.exception.ErrorCode;
import com.groom.cookiehouse.exception.model.CustomException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Objects;

@RestControllerAdvice
@Component
@RequiredArgsConstructor
public class ControllerExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionAdvice.class);

    /**
     * 400 BAD_REQUEST
     */

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected BaseResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        FieldError fieldError = Objects.requireNonNull(e.getFieldError());
        logger.error("Validation error for field {}: {}", fieldError.getField(), fieldError.getDefaultMessage());
        return BaseResponse.error(ErrorCode.VALIDATION_REQUEST_MISSING_EXCEPTION, String.format("%s. (%s)", fieldError.getDefaultMessage(), fieldError.getField()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestHeaderException.class)
    protected BaseResponse handleMissingRequestHeaderException(final MissingRequestHeaderException e) {
        logger.error("Missing Request Header: {}", e.getMessage(), e);
        return BaseResponse.error(ErrorCode.VALIDATION_REQUEST_HEADER_MISSING_EXCEPTION, String.format("%s. (%s)", ErrorCode.VALIDATION_REQUEST_HEADER_MISSING_EXCEPTION.getMessage(), e.getHeaderName()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected BaseResponse handleMissingRequestParameterException(final MissingServletRequestParameterException e) {
        logger.error("Missing Request Parameter: {}", e.getMessage(), e);
        return BaseResponse.error(ErrorCode.VALIDATION_REQUEST_PARAMETER_MISSING_EXCEPTION, String.format("%s. (%s)", ErrorCode.VALIDATION_REQUEST_PARAMETER_MISSING_EXCEPTION.getMessage(), e.getParameterName()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected BaseResponse handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        logger.error("Http Request Method Not Supported: {}", e.getMessage(), e);
        return BaseResponse.error(ErrorCode.REQUEST_METHOD_VALIDATION_EXCEPTION, e.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public BaseResponse fileSizeLimitExceeded(final MaxUploadSizeExceededException e) {
        logger.error("File Size Limit Exceeded: {}", e.getMessage(), e);
        return BaseResponse.error(ErrorCode.MAX_UPLOAD_SIZE_EXCEED_EXCEPTION, e.getMessage());
    }

    /**
     * 500 Internal Server Error
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    protected BaseResponse handleException(final Exception e, final HttpServletRequest request) throws IOException {
        logger.error("Internal Server Error: {}", e.getMessage(), e);
        return BaseResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);
    }
    /**
     * custom error
     */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<BaseResponse> handleGroomException(CustomException e) {
        logger.error("CustomException: {}", e.getMessage(), e);
        return ResponseEntity.status(e.getHttpStatus())
                .body(BaseResponse.error(e.getErrorCode(), e.getMessage()));
    }
}

