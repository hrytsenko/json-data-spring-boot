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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

class ExceptionAdvicesTest {

    private static final String CORRELATION = "CORRELATION";

    ExceptionAdvice exceptionAdvice;

    @BeforeEach
    void init() {
        CorrelationSource correlationSource = Mockito.mock(CorrelationSource.class);
        Mockito.doReturn(CORRELATION)
                .when(correlationSource).getCorrelation();
        exceptionAdvice = new ExceptionAdvice(correlationSource);
    }

    @Test
    void onUnknownError() {
        ResponseEntity<?> actualResponse = exceptionAdvice.onUnexpectedError(
                Mockito.mock(Exception.class));

        assertResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", actualResponse);
    }

    @Test
    void onBadRequest() {
        ResponseEntity<?> actualResponse = exceptionAdvice.onBadRequest(
                new ServiceException.BadRequest("BAD_REQUEST"));

        assertResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", actualResponse);
    }

    @Test
    void onUnauthorized() {
        ResponseEntity<?> actualResponse = exceptionAdvice.onUnauthorized(
                new ServiceException.Unauthorized());

        assertResponse(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", actualResponse);
    }

    @Test
    void onForbidden() {
        ResponseEntity<?> actualResponse = exceptionAdvice.onForbidden(
                new ServiceException.Forbidden());

        assertResponse(HttpStatus.FORBIDDEN, "FORBIDDEN", actualResponse);
    }

    @Test
    void onNotFound() {
        ResponseEntity<?> actualResponse = exceptionAdvice.onNotFound(
                new ServiceException.NotFound());

        assertResponse(HttpStatus.NOT_FOUND, "NOT_FOUND", actualResponse);
    }

    @Test
    void onInternalError() {
        ResponseEntity<?> actualResponse = exceptionAdvice.onInternalError(
                new ServiceException.InternalError("INTERNAL_ERROR"));

        assertResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", actualResponse);
    }

    @Test
    void onServiceUnavailable() {
        ResponseEntity<?> actualResponse = exceptionAdvice.onServiceUnavailable(
                new ServiceException.ServiceUnavailable());

        assertResponse(HttpStatus.SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", actualResponse);
    }

    static void assertResponse(HttpStatus expectedStatus, String expectedCode, ResponseEntity<?> actualResponse) {
        HttpStatus actualStatus = actualResponse.getStatusCode();
        Assertions.assertEquals(expectedStatus, actualStatus);
        MediaType actualContentType = actualResponse.getHeaders().getContentType();
        Assertions.assertEquals(MediaType.APPLICATION_JSON, actualContentType);

        JsonBean actualBody = (JsonBean) actualResponse.getBody();
        Assertions.assertNotNull(actualBody);
        String actualCorrelation = actualBody.getString("error.correlation");
        Assertions.assertEquals(CORRELATION, actualCorrelation);
        String actualCode = actualBody.getString("error.code");
        Assertions.assertEquals(expectedCode, actualCode);
    }

}
