[![Build Status](https://travis-ci.org/hrytsenko/json-data-spring-boot.svg?branch=master)](https://travis-ci.org/hrytsenko/json-data-spring-boot)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=hrytsenko_json-data-spring-boot&metric=alert_status)](https://sonarcloud.io/dashboard?id=hrytsenko_json-data-spring-boot)
[![](https://jitpack.io/v/hrytsenko/json-data-spring-boot.svg)](https://jitpack.io/#hrytsenko/json-data-spring-boot)

# JSON data for Spring Boot

This library enables [json-data] for [Spring Boot] including serialization, validation and error handling.
The following example illustrates integration with [Spring Boot], [Spring Feign] and [Spring Sleuth]:

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
      var repositories = githubClient.listRepositories(request.getOwner());
      return TO_RESPONSE.map(repositories);
    }

    @FeignClient(name = "github-client", url = "${github.url}")
    interface GithubClient {
      @GetMapping(
          value = "/users/{owner}/repos",
          produces = MediaType.APPLICATION_JSON_VALUE)
      List<JsonBean> listRepositories(@PathVariable("owner") String owner);
    }

  }

  @Bean
  CorrelationSource sleuthSource(Tracer tracer) {
    return () -> tracer.currentSpan().context().traceId();
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
```

Use `ValidateRequest` to validate an input JSON entity (the first argument).
Use `ValidateResponse` to validate an output JSON entity (the return value).
Provide `ValidatorSource` to configure a resource manager for validators.

Use `WrapErrors` to wrap all unhandled exceptions into `ServiceException.InternalError`.
Provide `CorrelationSource` to enable correlations for error responses.

[json-data]: https://github.com/hrytsenko/json-data
[Spring Boot]: https://spring.io/projects/spring-boot
[Spring Feign]: https://spring.io/projects/spring-cloud-openfeign
[Spring Sleuth]: https://spring.io/projects/spring-cloud-sleuth
