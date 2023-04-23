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

import com.github.hrytsenko.jsondata.JsonBean;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order
@Slf4j
@AllArgsConstructor
class ExceptionAdvice {

    CorrelationSource correlationSource;

    @ExceptionHandler(Exception.class)
    ResponseEntity<JsonBean> onUnexpectedError(Exception exception) {
        log.error("Unexpected error", exception);
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
    }

    @ExceptionHandler(ServiceException.BadRequest.class)
    ResponseEntity<JsonBean> onBadRequest(ServiceException.BadRequest exception) {
        log.error("Bad request", exception);
        return errorResponse(HttpStatus.BAD_REQUEST, exception.getCode());
    }

    @ExceptionHandler(ServiceException.Unauthorized.class)
    ResponseEntity<JsonBean> onUnauthorized(ServiceException.Unauthorized exception) {
        log.error("Unauthorized", exception);
        return errorResponse(HttpStatus.UNAUTHORIZED, exception.getCode());
    }

    @ExceptionHandler(ServiceException.Forbidden.class)
    ResponseEntity<JsonBean> onForbidden(ServiceException.Forbidden exception) {
        log.error("Forbidden", exception);
        return errorResponse(HttpStatus.FORBIDDEN, exception.getCode());
    }

    @ExceptionHandler(ServiceException.NotFound.class)
    ResponseEntity<JsonBean> onNotFound(ServiceException.NotFound exception) {
        log.error("Not found", exception);
        return errorResponse(HttpStatus.NOT_FOUND, exception.getCode());
    }

    @ExceptionHandler(ServiceException.InternalError.class)
    ResponseEntity<JsonBean> onInternalError(ServiceException.InternalError exception) {
        log.error("Internal error", exception);
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getCode());
    }

    @ExceptionHandler(ServiceException.ServiceUnavailable.class)
    ResponseEntity<JsonBean> onServiceUnavailable(ServiceException.ServiceUnavailable exception) {
        log.error("Service unavailable", exception);
        return errorResponse(HttpStatus.SERVICE_UNAVAILABLE, exception.getCode());
    }

    private ResponseEntity<JsonBean> errorResponse(HttpStatus status, String code) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new JsonBean()
                        .putString("error.correlation", correlationSource.getCorrelation())
                        .putString("error.code", code));
    }

}
