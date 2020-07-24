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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.github.hrytsenko.jsondata.JsonEntity;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
class JacksonConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonEntityJacksonCustomizer() {
        SimpleModule module = new SimpleModule()
                .setDeserializerModifier(new JsonEntityDeserializerModifier())
                .setSerializerModifier(new JsonEntitySerializerModifier());
        return builder -> builder.modulesToInstall(module)
                .featuresToEnable(DeserializationFeature.USE_LONG_FOR_INTS);
    }

    @AllArgsConstructor
    static class JsonEntityDeserializer extends JsonDeserializer<JsonEntity<?>> {

        private Class<?> entityClass;

        @SneakyThrows
        @Override
        public JsonEntity<?> deserialize(JsonParser parser, DeserializationContext context) {
            Map<String, ?> json = parser.readValueAs(new TypeReference<Map<String, ?>>() {
            });
            return new JsonEntity.Factory(entityClass).createFromMap(json);
        }

    }

    static class JsonEntityDeserializerModifier extends BeanDeserializerModifier {

        @Override
        public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription bean, JsonDeserializer<?> deserializer) {
            return bean.getType().isTypeOrSubTypeOf(JsonEntity.class)
                    ? new JsonEntityDeserializer(bean.getType().getRawClass())
                    : super.modifyDeserializer(config, bean, deserializer);
        }

    }

    static class JsonEntitySerializer extends JsonSerializer<JsonEntity<?>> {

        @SneakyThrows
        @Override
        public void serialize(JsonEntity<?> value, JsonGenerator generator, SerializerProvider provider) {
            generator.writeObject(value.asMap());
        }

    }

    static class JsonEntitySerializerModifier extends BeanSerializerModifier {

        @Override
        public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription bean, JsonSerializer<?> serializer) {
            return bean.getType().isTypeOrSubTypeOf(JsonEntity.class)
                    ? new JsonEntitySerializer()
                    : super.modifySerializer(config, bean, serializer);
        }

    }

}
