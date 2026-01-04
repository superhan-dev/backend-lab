# Spring boot 4.0.1 + grafana + Opentelemetry를 활용한 observability

spring boot 4.0.1에서 opentelemetry를 사용하여 메트릭과 로그를 관리하는 방법을 실습 하였다.

## docker compose in spring boot

docker compose를 외부에서 실행하는 것이 아닌 스프링부트가 로드될 때 함께 관리해줌으로써 컨텍스트를 보다 좁히도록 설계한 것을 체험할 수 있었다.

## OpenTelemetryAppender

logback 설정을 활용하여 api 호출시 traceId를 부여하면 모든 trace를 일관적으로 추적할 수 있게된다.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/base.xml"/>

    <appender name="OTEL" class="io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender">
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="OTEL"/>
    </root>
</configuration>
```
