/*
 * Copyright (C) 2020 Anton Hrytsenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.hrytsenko.jsondata.springboot.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.hrytsenko.jsondata.JsonEntity;
import com.github.hrytsenko.jsondata.springboot.web.ValidateRequestException;
import com.github.hrytsenko.jsondata.springboot.web.ValidateResponseException;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@UtilityClass
class ExceptionAdvices {

    @RestControllerAdvice
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Slf4j
    @AllArgsConstructor
    static class InternalExceptionAdvice {

        CorrelationSource correlationSource;

        @ExceptionHandler(Exception.class)
        @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
        public ErrorResponse onInternalError(Exception exception) {
            log.error("Unexpected error", exception);
            return ErrorResponse.create("INTERNAL_ERROR", correlationSource.getCorrelation());
        }

    }

    @RestControllerAdvice
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Slf4j
    @AllArgsConstructor
    static class ServiceExceptionAdvice {

        CorrelationSource correlationSource;

        @ExceptionHandler(ServiceException.BadRequest.class)
        @ResponseStatus(code = HttpStatus.BAD_REQUEST)
        public ErrorResponse onBadRequest(ServiceException.BadRequest exception) {
            log.error("Bad request", exception);
            return ErrorResponse.create(exception.getCode(), correlationSource.getCorrelation());
        }

        @ExceptionHandler(ServiceException.Unauthorized.class)
        @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
        public ErrorResponse onUnauthorized(ServiceException.Unauthorized exception) {
            log.error("Unauthorized", exception);
            return ErrorResponse.create(exception.getCode(), correlationSource.getCorrelation());
        }

        @ExceptionHandler(ServiceException.Forbidden.class)
        @ResponseStatus(code = HttpStatus.FORBIDDEN)
        public ErrorResponse onForbidden(ServiceException.Forbidden exception) {
            log.error("Forbidden", exception);
            return ErrorResponse.create(exception.getCode(), correlationSource.getCorrelation());
        }

        @ExceptionHandler(ServiceException.NotFound.class)
        @ResponseStatus(code = HttpStatus.NOT_FOUND)
        public ErrorResponse onNotFound(ServiceException.NotFound exception) {
            log.error("Not found", exception);
            return ErrorResponse.create(exception.getCode(), correlationSource.getCorrelation());
        }

        @ExceptionHandler(ServiceException.InternalError.class)
        @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
        public ErrorResponse onInternalError(ServiceException.InternalError exception) {
            log.error("Internal error", exception);
            return ErrorResponse.create(exception.getCode(), correlationSource.getCorrelation());
        }

        @ExceptionHandler(ServiceException.ServiceUnavailable.class)
        @ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE)
        public ErrorResponse onServiceUnavailable(ServiceException.ServiceUnavailable exception) {
            log.error("Service unavailable", exception);
            return ErrorResponse.create(exception.getCode(), correlationSource.getCorrelation());
        }

    }

    @RestControllerAdvice
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Slf4j
    @AllArgsConstructor
    static class ValidateExceptionAdvice {

        CorrelationSource correlationSource;

        @ExceptionHandler(JsonProcessingException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse onProcessJson(JsonProcessingException exception) {
            log.error("JSON processing failed", exception);
            return ErrorResponse.create("BAD_CONTENT", correlationSource.getCorrelation());
        }

        @ExceptionHandler(ValidateRequestException.class)
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public ErrorResponse onValidateRequest(ValidateRequestException exception) {
            log.error("Request validation failed", exception);
            return ErrorResponse.create("INVALID_REQUEST", correlationSource.getCorrelation());
        }

        @ExceptionHandler(ValidateResponseException.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public ErrorResponse onValidateResponse(ValidateResponseException exception) {
            log.error("Response validation failed", exception);
            return ErrorResponse.create("INVALID_RESPONSE", correlationSource.getCorrelation());
        }

    }

    static class ErrorResponse extends JsonEntity<ErrorResponse> {

        static ErrorResponse create(String code, String correlation) {
            return new ErrorResponse()
                    .putString("error.code", code)
                    .putString("error.correlation", correlation);
        }

    }

}
