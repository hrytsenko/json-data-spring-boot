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
package com.github.hrytsenko.jsondata.springboot.web;

import com.github.hrytsenko.jsondata.JsonBean;
import com.github.hrytsenko.jsondata.JsonParser;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ValidateResponseAspectTest {

    ValidateResponseAspect aspect;

    @BeforeEach
    void init() {
        aspect = Mockito.spy(new ValidateResponseAspect());
    }

    @Test
    void validate_success() {
        String sourceSchema = "{\"properties\":{\"foo\":{\"enum\":[\"FOO\"]}},\"required\":[\"foo\"]}";
        JsonBean sourceResponse = JsonParser.stringToEntity("{'foo':'FOO'}", JsonBean::create);

        ProceedingJoinPoint sourceJoinPoint = mockJoinPoint(sourceResponse);
        ValidateResponse sourceConfig = Mockito.mock(ValidateResponse.class);

        Mockito.doReturn(sourceSchema)
                .when(aspect).loadSchema(Mockito.any());

        aspect.handle(sourceJoinPoint, sourceConfig);
    }

    @Test
    void validate_failure() {
        String sourceSchema = "{\"properties\":{\"foo\":{\"enum\":[\"FOO\"]}},\"required\":[\"foo\"]}";
        JsonBean sourceResponse = JsonParser.stringToEntity("{'foo':'BAR'}", JsonBean::create);

        ProceedingJoinPoint sourcePoint = mockJoinPoint(sourceResponse);
        ValidateResponse sourceConfig = Mockito.mock(ValidateResponse.class);

        Mockito.doReturn(sourceSchema)
                .when(aspect).loadSchema(Mockito.any());

        Assertions.assertThrows(ValidateResponseException.class,
                () -> aspect.handle(sourcePoint, sourceConfig));
    }

    @SneakyThrows
    private ProceedingJoinPoint mockJoinPoint(Object result) {
        ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
        Mockito.doReturn(result)
                .when(joinPoint).proceed();
        return joinPoint;
    }

}
