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

## Kafka 모니터링 (JMX Exporter + Prometheus + Grafana)

Kafka 브로커의 메트릭을 수집하고 시각화하기 위해 JMX Exporter, Prometheus, Grafana를 통합하였다.

### 아키텍처

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  Kafka Broker   │────▶│   Prometheus    │────▶│  Grafana-LGTM   │
│  (JMX Exporter) │     │   (수집/저장)    │     │   (시각화)       │
│    :7071        │     │    :9090        │     │    :3000        │
└─────────────────┘     └─────────────────┘     └─────────────────┘
        │                                               │
        └───────────── JMX 메트릭 노출 ──────────────────┘
```

### 구성 원리

#### 1. JMX Exporter (Kafka 메트릭 노출)

Kafka는 JMX(Java Management Extensions)를 통해 내부 메트릭을 노출한다. JMX Exporter는 이 메트릭을 Prometheus가 이해할 수 있는 형식으로 변환하는 Java Agent이다.

```dockerfile
# Dockerfile.kafka
FROM apache/kafka:latest
ADD https://repo1.maven.org/.../jmx_prometheus_javaagent-1.0.1.jar /opt/jmx-exporter/
```

```yaml
# compose.yml - Kafka 환경변수
KAFKA_OPTS: "-javaagent:/opt/jmx-exporter/jmx_prometheus_javaagent.jar=7071:/opt/jmx-exporter/kafka-jmx-config.yml"
```

- Kafka JVM 시작 시 JMX Exporter agent가 함께 로드됨
- 포트 7071에서 `/metrics` 엔드포인트로 Prometheus 형식의 메트릭 제공

#### 2. Prometheus (메트릭 수집/저장)

Prometheus는 설정된 타겟에서 주기적으로 메트릭을 스크랩(pull)하여 시계열 데이터베이스에 저장한다.

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'kafka'
    static_configs:
      - targets: ['kafka:7071']  # JMX Exporter 엔드포인트
```

#### 3. Grafana Init Container (대시보드 자동 프로비저닝)

`grafana-lgtm`은 기본적으로 OpenTelemetry용 데이터소스만 포함하므로, 별도의 init 컨테이너를 통해 Prometheus 데이터소스와 Kafka 대시보드를 동적으로 추가한다.

```yaml
grafana-init:
  image: curlimages/curl:latest
  depends_on:
    grafana-lgtm:
      condition: service_healthy  # Grafana가 준비될 때까지 대기
  command:
    - |
      # 1. Prometheus 데이터소스 추가
      curl -X POST http://grafana-lgtm:3000/api/datasources \
        -d '{"name":"Prometheus-Kafka","type":"prometheus","url":"http://prometheus:9090"}'

      # 2. Kafka 대시보드 임포트
      curl -X POST http://grafana-lgtm:3000/api/dashboards/db \
        -d '{"dashboard": $(cat kafka-dashboard.json)}'
```

**핵심 포인트:**
- `depends_on` + `condition: service_healthy`: Grafana healthcheck가 통과한 후에만 실행
- Grafana REST API를 사용해 런타임에 데이터소스/대시보드 등록
- 컨테이너 실행 후 종료되는 일회성 작업 (init pattern)

### 서비스 포트

| 서비스 | 포트 | 용도 |
|--------|------|------|
| Grafana-LGTM | 3000 | OpenTelemetry + Kafka 대시보드 |
| Prometheus | 9090 | 메트릭 수집/쿼리 |
| Kafka | 9092 | 브로커 |
| JMX Exporter | 7071 | Kafka JMX 메트릭 |

### 실행 방법

```bash
# otlp-demo 실행 (Docker Compose 자동 시작)
cd otlp-demo && ./gradlew bootRun

# kafka-consumer-demo 실행 (별도 터미널)
cd kafka-consumer-demo && ./gradlew bootRun

# 대시보드 접속
# - Grafana: http://localhost:3000 (admin/admin)
# - Prometheus: http://localhost:9090
```

### 주요 Kafka 메트릭

| 메트릭 | 설명 |
|--------|------|
| `kafka_server_brokertopicmetrics_messagesinpersec` | 초당 수신 메시지 수 |
| `kafka_server_brokertopicmetrics_bytesinpersec` | 초당 수신 바이트 |
| `kafka_server_brokertopicmetrics_bytesoutpersec` | 초당 송신 바이트 |
| `kafka_controller_kafkacontroller_activecontrollercount` | 활성 컨트롤러 수 |
| `kafka_log_log_size` | 토픽/파티션별 로그 크기 |
