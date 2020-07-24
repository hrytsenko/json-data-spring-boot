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

import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true)
public class ServiceException extends RuntimeException {

    @Getter
    String code;

    private ServiceException(String code) {
        this(code, null);
    }

    private ServiceException(String code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public static class BadRequest extends ServiceException {

        public BadRequest(String code) {
            super(code);
        }

        public BadRequest(String code, Throwable cause) {
            super(code, cause);
        }

    }

    public static class Unauthorized extends BadRequest {

        public Unauthorized() {
            this(null);
        }

        public Unauthorized(Throwable cause) {
            super("UNAUTHORIZED", cause);
        }

    }

    public static class Forbidden extends BadRequest {

        public Forbidden() {
            this(null);
        }

        public Forbidden(Throwable cause) {
            super("FORBIDDEN", cause);
        }

    }

    public static class NotFound extends BadRequest {

        public NotFound() {
            this(null);
        }

        public NotFound(Throwable cause) {
            super("NOT_FOUND", cause);
        }

    }

    public static class InternalServer extends ServiceException {

        public InternalServer(String code) {
            super(code);
        }

        public InternalServer(String code, Throwable cause) {
            super(code, cause);
        }

    }

}
