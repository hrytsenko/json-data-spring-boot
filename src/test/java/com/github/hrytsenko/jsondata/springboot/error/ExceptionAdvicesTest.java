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
import com.github.hrytsenko.jsondata.springboot.web.ValidateRequestException;
import com.github.hrytsenko.jsondata.springboot.web.ValidateResponseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ExceptionAdvicesTest {

    private static final String CORRELATION = "CORRELATION";

    CorrelationSource correlationSource;

    @BeforeEach
    void init() {
        correlationSource = Mockito.mock(CorrelationSource.class);
        Mockito.doReturn(CORRELATION)
                .when(correlationSource).getCorrelation();
    }

    @Test
    void internalExceptionAdvice_onInternalError() {
        ExceptionAdvices.InternalExceptionAdvice advice = new ExceptionAdvices.InternalExceptionAdvice(correlationSource);

        Exception sourceException = Mockito.mock(Exception.class);

        ExceptionAdvices.ErrorResponse actualResponse = advice.onInternalError(sourceException);

        ExceptionAdvices.ErrorResponse expectedResponse = ExceptionAdvices.ErrorResponse.create("INTERNAL_ERROR", CORRELATION);
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void serviceExceptionAdvice_onBadRequest() {
        ExceptionAdvices.ServiceExceptionAdvice advice = new ExceptionAdvices.ServiceExceptionAdvice(correlationSource);

        ServiceException.BadRequest sourceException = new ServiceException.BadRequest("BAD_REQUEST");

        ExceptionAdvices.ErrorResponse actualResponse = advice.onBadRequest(sourceException);

        ExceptionAdvices.ErrorResponse expectedResponse = ExceptionAdvices.ErrorResponse.create("BAD_REQUEST", CORRELATION);
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void serviceExceptionAdvice_onUnauthorized() {
        ExceptionAdvices.ServiceExceptionAdvice advice = new ExceptionAdvices.ServiceExceptionAdvice(correlationSource);

        ServiceException.Unauthorized sourceException = new ServiceException.Unauthorized();

        ExceptionAdvices.ErrorResponse actualResponse = advice.onUnauthorized(sourceException);

        ExceptionAdvices.ErrorResponse expectedResponse = ExceptionAdvices.ErrorResponse.create("UNAUTHORIZED", CORRELATION);
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void serviceExceptionAdvice_onForbidden() {
        ExceptionAdvices.ServiceExceptionAdvice advice = new ExceptionAdvices.ServiceExceptionAdvice(correlationSource);

        ServiceException.Forbidden sourceException = new ServiceException.Forbidden();

        ExceptionAdvices.ErrorResponse actualResponse = advice.onForbidden(sourceException);

        ExceptionAdvices.ErrorResponse expectedResponse = ExceptionAdvices.ErrorResponse.create("FORBIDDEN", CORRELATION);
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void serviceExceptionAdvice_onNotFound() {
        ExceptionAdvices.ServiceExceptionAdvice advice = new ExceptionAdvices.ServiceExceptionAdvice(correlationSource);

        ServiceException.NotFound sourceException = new ServiceException.NotFound();

        ExceptionAdvices.ErrorResponse actualResponse = advice.onNotFound(sourceException);

        ExceptionAdvices.ErrorResponse expectedResponse = ExceptionAdvices.ErrorResponse.create("NOT_FOUND", CORRELATION);
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void serviceExceptionAdvice_onInternalError() {
        ExceptionAdvices.ServiceExceptionAdvice advice = new ExceptionAdvices.ServiceExceptionAdvice(correlationSource);

        ServiceException.InternalServer sourceException = new ServiceException.InternalServer("INTERNAL_ERROR");

        ExceptionAdvices.ErrorResponse actualResponse = advice.onInternalError(sourceException);

        ExceptionAdvices.ErrorResponse expectedResponse = ExceptionAdvices.ErrorResponse.create("INTERNAL_ERROR", CORRELATION);
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void validateExceptionAdvice_onProcessJson() {
        ExceptionAdvices.ValidateExceptionAdvice advice = new ExceptionAdvices.ValidateExceptionAdvice(correlationSource);

        JsonProcessingException sourceException = Mockito.mock(JsonProcessingException.class);

        ExceptionAdvices.ErrorResponse actualResponse = advice.onProcessJson(sourceException);

        ExceptionAdvices.ErrorResponse expectedResponse = ExceptionAdvices.ErrorResponse.create("BAD_CONTENT", CORRELATION);
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void validateExceptionAdvice_onValidateRequest() {
        ExceptionAdvices.ValidateExceptionAdvice advice = new ExceptionAdvices.ValidateExceptionAdvice(correlationSource);

        ValidateRequestException sourceException = Mockito.mock(ValidateRequestException.class);

        ExceptionAdvices.ErrorResponse actualResponse = advice.onValidateRequest(sourceException);

        ExceptionAdvices.ErrorResponse expectedResponse = ExceptionAdvices.ErrorResponse.create("INVALID_REQUEST", CORRELATION);
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void validateExceptionAdvice_onValidateResponse() {
        ExceptionAdvices.ValidateExceptionAdvice advice = new ExceptionAdvices.ValidateExceptionAdvice(correlationSource);

        ValidateResponseException sourceException = Mockito.mock(ValidateResponseException.class);

        ExceptionAdvices.ErrorResponse actualResponse = advice.onValidateResponse(sourceException);

        ExceptionAdvices.ErrorResponse expectedResponse = ExceptionAdvices.ErrorResponse.create("INVALID_RESPONSE", CORRELATION);
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

}
