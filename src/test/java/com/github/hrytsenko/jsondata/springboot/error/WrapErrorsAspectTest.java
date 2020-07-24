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

import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class WrapErrorsAspectTest {

    WrapErrorsAspect aspect;

    @BeforeEach
    void init() {
        aspect = Mockito.spy(new WrapErrorsAspect());
    }

    @Test
    void handle_noException() {
        ProceedingJoinPoint sourceJoinPoint = Mockito.mock(ProceedingJoinPoint.class);
        WrapErrors sourceConfig = Mockito.mock(WrapErrors.class);

        Assertions.assertDoesNotThrow(
                () -> aspect.handle(sourceJoinPoint, sourceConfig));
    }

    @Test
    void handle_customException() {
        Exception sourceException = Mockito.mock(Exception.class);

        ProceedingJoinPoint sourceJoinPoint = mockJoinPoint(sourceException);
        WrapErrors sourceConfig = Mockito.mock(WrapErrors.class);

        Assertions.assertThrows(ServiceException.InternalServer.class,
                () -> aspect.handle(sourceJoinPoint, sourceConfig));
    }

    @Test
    void handle_serviceException() {
        ServiceException.Unauthorized sourceException = new ServiceException.Unauthorized();

        ProceedingJoinPoint sourceJoinPoint = mockJoinPoint(sourceException);
        WrapErrors sourceConfig = Mockito.mock(WrapErrors.class);

        ServiceException.Unauthorized actualException = Assertions.assertThrows(ServiceException.Unauthorized.class,
                () -> aspect.handle(sourceJoinPoint, sourceConfig));
        Assertions.assertSame(sourceException, actualException);
    }

    @SneakyThrows
    private ProceedingJoinPoint mockJoinPoint(Exception exception) {
        ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
        Mockito.doThrow(exception)
                .when(joinPoint).proceed();
        return joinPoint;
    }

}
