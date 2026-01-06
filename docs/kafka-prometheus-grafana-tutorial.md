
# Kafka + Prometheus + Grafana Monitoring Tutorial (Docker Compose)

이 튜토리얼은 **KRaft 기반 Kafka** 환경을 대상으로  
Prometheus + Grafana를 이용해 **운영 수준의 Kafka 모니터링**을 구성하는 방법을 설명합니다.

---

## 1. 아키텍처 개요

Kafka는 JVM 기반이므로 **JMX 메트릭**을 노출합니다.  
JMX Exporter가 이를 HTTP 메트릭으로 변환하고 Prometheus가 수집합니다.

```
Kafka (JMX)
 └─ JMX Exporter :9404
      └─ Prometheus
           └─ Grafana
```

---

## 2. 디렉토리 구조

```
backend-lab/
├─ docker-compose.yml
├─ monitoring/
│  ├─ prometheus/
│  │  ├─ prometheus.yml
│  │  └─ kafka-jmx.yml
│  └─ grafana/
│     └─ provisioning/
│        └─ datasources/
│           └─ prometheus.yml
```

---

## 3. Kafka + JMX Exporter 설정

### 3.1 Kafka JMX Exporter 설정

📄 `monitoring/prometheus/kafka-jmx.yml`

```yaml
startDelaySeconds: 0
lowercaseOutputName: true
rules:
  - pattern: 'kafka.server<type=(.+), name=(.+)><>Value'
    name: kafka_server_$1_$2
```

---

### 3.2 docker-compose Kafka 설정

```yaml
services:
  kafka:
    image: apache/kafka:3.7.0
    container_name: kafka
    ports:
      - "9092:9092"
      - "9404:9404"
    environment:
      KAFKA_NODE_ID: 1
      KAFKA_PROCESS_ROLES: broker,controller
      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@kafka:9093
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,CONTROLLER:PLAINTEXT
      KAFKA_JMX_PORT: 9999
      KAFKA_OPTS: >
        -javaagent:/opt/jmx_exporter/jmx_prometheus_javaagent.jar=9404:/opt/jmx_exporter/kafka-jmx.yml
    volumes:
      - ./monitoring/prometheus/kafka-jmx.yml:/opt/jmx_exporter/kafka-jmx.yml
      - ./monitoring/prometheus/jmx_prometheus_javaagent.jar:/opt/jmx_exporter/jmx_prometheus_javaagent.jar
```

---

## 4. Prometheus 설정

📄 `monitoring/prometheus/prometheus.yml`

```yaml
global:
  scrape_interval: 5s

scrape_configs:
  - job_name: kafka
    static_configs:
      - targets: ['kafka:9404']
```

---

```yaml
  prometheus:
    image: prom/prometheus
    container_name: prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./monitoring/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml
```

---

## 5. Grafana 설정

📄 `monitoring/grafana/provisioning/datasources/prometheus.yml`

```yaml
apiVersion: 1
datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090
    isDefault: true
```

---

```yaml
  grafana:
    image: grafana/grafana
    container_name: grafana
    ports:
      - "3000:3000"
    volumes:
      - ./monitoring/grafana/provisioning:/etc/grafana/provisioning
```

---

## 6. 실행

```bash
docker-compose up -d
```

---

## 7. 접속 확인

| 서비스 | 주소 |
|---|---|
| Kafka Metrics | http://localhost:9404/metrics |
| Prometheus | http://localhost:9090 |
| Grafana | http://localhost:3000 |

---

## 8. Grafana Kafka 대시보드

- 추천 Dashboard ID: **7589**
- 이름: Kafka Exporter Overview

---

## 9. 운영에서 보는 핵심 메트릭

- Broker Under Replicated Partitions
- Request Handler Idle %
- ISR Count
- Produce / Fetch Request Rate
- Consumer Lag (확장)

---

## 10. 다음 단계

- Kafka Connect 메트릭 추가
- Alertmanager 연동
- OpenTelemetry + Kafka

---

✅ 이 구성은 **실제 운영 Kafka 모니터링의 표준 패턴**입니다.
