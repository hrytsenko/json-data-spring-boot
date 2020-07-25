[![Build Status](https://travis-ci.org/hrytsenko/json-data-spring-boot.svg?branch=master)](https://travis-ci.org/hrytsenko/json-data-spring-boot)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=hrytsenko_json-data-spring-boot&metric=alert_status)](https://sonarcloud.io/dashboard?id=hrytsenko_json-data-spring-boot)

# Summary

This library enables [json-data] for [Spring Boot] including serialization, validation and error handling for HTTP requests and responses that use JSON entities.

# Example

The following example illustrates:
* Use of JSON entities with [Spring Boot].
* Use of JSON entities with [Spring Cloud OpenFeign].
* Use of JSON errors with [Spring Boot] and [Spring Cloud Sleuth].

```java
@EnableFeignClients
@SpringBootApplication
class Application {

    static class Request extends JsonEntity<Request> {
        String getOwner() {
            return getString("owner");
        }
    }

    static class Response extends JsonEntity<Response> {
    }

    @RestController
    @AllArgsConstructor
    static class GithubController {

        static JsonMapper<Response> TO_RESPONSE = JsonMapper.create(
                JsonResources.readResource("/response-projection.json"), Response::new);

        GithubClient githubClient;

        @PostMapping(
                value = "/list-repositories",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
        @ValidateRequest("/request-schema.json")
        @ValidateResponse("/response-schema.json")
        @WrapErrors("CANNOT_LIST_REPOSITORIES")
        public Response listRepositories(@RequestBody Request request) {
            val repositories = githubClient.listRepositories(request.getOwner());
            return TO_RESPONSE.map(repositories);
        }

        @FeignClient(
                name = "github-client",
                url = "${github.url}")
        interface GithubClient {
            @GetMapping(
                    value = "/users/{owner}/repos",
                    produces = MediaType.APPLICATION_JSON_VALUE)
            List<JsonBean> listRepositories(@PathVariable("owner") String owner);
        }

    }

    @Bean
    public CorrelationSource sleuthSource(Tracer tracer) {
        return () -> tracer.currentSpan().context().traceIdString();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

# Usage

Use JSON entities with Spring Web and Spring Feign.

Use `ValidateRequest` to validate a JSON entity (the first argument) via JSON Schema. Use `ValidateResponse` to validate a JSON entity (the return value) via JSON Schema.

Use `WrapErrors` to wrap all exceptions in `ServiceException.InternalError` with a given error code, except those that are `ServiceException`.

Provide `CorrelationSource` to enable correlations for error responses.

[json-data]: https://github.com/hrytsenko/json-data 
[Spring Boot]: https://spring.io/projects/spring-boot
[Spring Cloud OpenFeign]: https://spring.io/projects/spring-cloud-openfeign
[Spring Cloud Sleuth]: https://spring.io/projects/spring-cloud-sleuth
