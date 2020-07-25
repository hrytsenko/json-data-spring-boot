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
package com.github.hrytsenko.jsondata.springboot;

import com.github.hrytsenko.jsondata.springboot.error.CorrelationSource;
import com.github.hrytsenko.jsondata.springboot.web.ValidatorSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AutoConfigurationTest {

    @Test
    void correlationSource_undefined() {
        CorrelationSource correlationSource = new AutoConfiguration().undefinedCorrelationSource();

        String actualCorrelation = correlationSource.getCorrelation();

        Assertions.assertEquals("UNDEFINED", actualCorrelation);
    }

    @Test
    void validatorSource_default() {
        ValidatorSource validatorSource = new AutoConfiguration().defaultValidatorSource();

        Assertions.assertDoesNotThrow(
                () -> validatorSource.getValidator("empty-schema.json"));
    }

}
