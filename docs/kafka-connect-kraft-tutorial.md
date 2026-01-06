# Kafka Connect 튜토리얼 (KRaft / Docker / 2026)

이 문서는 **Kafka Connect를 올바르게 이해하고 실습하기 위한 튜토리얼**입니다.  
Zookeeper 없이 **KRaft 모드**, Docker 기반, 실무 기준으로 작성되었습니다.

---

## 목표

- Kafka Connect의 역할과 내부 구조 이해
- REST API 기반 Connector 관리
- Source → Kafka → Consumer 데이터 흐름 직접 확인
- 왜 FileStreamSourceConnector 튜토리얼이 더 이상 적절하지 않은지 이해

---

## 아키텍처 개요

```
[ Kafka Connect ]
        |
        | (REST API)
        v
[ Herder ]
        |
        | (task assign)
        v
[ Source Task ]
        |
        v
[ Kafka Topic ]
```

---

## Docker Compose 구성

cp-kafka 를 사용하지 않으면 에러가 발생했고

```yaml
version: "3.8"

services:
  kafka:
    image: confluentinc/cp-kafka:7.6.0
    container_name: kafka
    ports:
      - "9092:9092"
    environment:
      KAFKA_PROCESS_ROLES: "broker,controller"
      KAFKA_NODE_ID: 1
      KAFKA_CONTROLLER_QUORUM_VOTERS: "1@kafka:9093"

      KAFKA_LISTENERS: "PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093"
      KAFKA_ADVERTISED_LISTENERS: "PLAINTEXT://kafka:9092"
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: "PLAINTEXT:PLAINTEXT,CONTROLLER:PLAINTEXT"
      KAFKA_CONTROLLER_LISTENER_NAMES: "CONTROLLER"

      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0

      CLUSTER_ID: "MkU3OEVBNTcwNTJENDM2Qk"

  connect:
    image: confluentinc/cp-kafka-connect:7.6.0
    container_name: connect
    depends_on:
      - kafka
    ports:
      - "8083:8083"
    environment:
      CONNECT_BOOTSTRAP_SERVERS: "kafka:9092"
      CONNECT_REST_ADVERTISED_HOST_NAME: "localhost"
      CONNECT_REST_PORT: 8083

      CONNECT_GROUP_ID: "connect-cluster"
      CONNECT_CONFIG_STORAGE_TOPIC: "connect-configs"
      CONNECT_OFFSET_STORAGE_TOPIC: "connect-offsets"
      CONNECT_STATUS_STORAGE_TOPIC: "connect-status"

      CONNECT_CONFIG_STORAGE_REPLICATION_FACTOR: 1
      CONNECT_OFFSET_STORAGE_REPLICATION_FACTOR: 1
      CONNECT_STATUS_STORAGE_REPLICATION_FACTOR: 1

      CONNECT_KEY_CONVERTER: "org.apache.kafka.connect.json.JsonConverter"
      CONNECT_VALUE_CONVERTER: "org.apache.kafka.connect.json.JsonConverter"
      CONNECT_KEY_CONVERTER_SCHEMAS_ENABLE: "false"
      CONNECT_VALUE_CONVERTER_SCHEMAS_ENABLE: "false"

      CONNECT_PLUGIN_PATH: "/usr/share/java,/usr/share/confluent-hub-components"
```

---

## 실행

```bash
docker compose up -d
```

---

## Kafka Connect 상태 확인

```bash
curl http://localhost:8083/
```

정상 응답 예시:

```json
{ "version": "7.6.0", "kafka_cluster_id": "..." }
```

---

## Source Connector 실습 (Datagen)

### Connect 컨테이너에 Datagen 설치

직접 컨테이너에 접근해서 datagen을 직접 설치해야한다.

```bash
docker exec -it connect bash
```

```bash
confluent-hub install confluentinc/kafka-connect-datagen:0.6.8 --no-prompt
```

커넥트를 다시 시작한다.

```bash
docker restart connect

```

### 커넥트 요청

“Kafka Connect 클러스터에 ‘Datagen 소스 커넥터 인스턴스 하나’를 등록해서
주기적으로 가짜 user 데이터를 생성해 users 토픽으로 보내라”
라고 명령하는 것이다.

```bash
curl -X POST http://localhost:8083/connectors \
  -H "Content-Type: application/json" \
  -d '{
    "name": "users-datagen",
    "config": {
      "connector.class": "io.confluent.kafka.connect.datagen.DatagenConnector",
      "tasks.max": "1",
      "kafka.topic": "users",
      "quickstart": "users",
      "max.interval": "1000"
    }
  }'
```

---

## Kafka 데이터 확인

Confluent 이미지는 `.sh`를 붙이지 않고 사용한다.

```bash
docker exec -it kafka \
  kafka-console-consumer \
  --bootstrap-server kafka:9092 \
  --topic users \
  --from-beginning
```

명령어를 실행하면 실시간으로 카프카에 스트리밍되는 메시지를 구독할 수 있다.

```
{"registertime":1514986470717,"userid":"User_2","regionid":"Region_6","gender":"FEMALE"}
{"registertime":1489730776926,"userid":"User_5","regionid":"Region_1","gender":"OTHER"}
{"registertime":1495551954140,"userid":"User_7","regionid":"Region_5","gender":"OTHER"}
```

---

## Connector 상태 확인

```bash
curl http://localhost:8083/connectors/users-datagen/status
```

---

## Connector 삭제

```bash
curl -X DELETE http://localhost:8083/connectors/users-datagen
```

---

## 왜 FileStreamSourceConnector를 쓰지 않는가?

- 테스트용 샘플 커넥터
- 최신 Docker 이미지에 기본 포함 ❌
- 운영/학습 가치 낮음

👉 Datagen / JDBC / CDC 커넥터를 사용하는 것이 정석

---

## 핵심 요약

- Kafka Connect는 **데이터 파이프라인 런타임**
- Connector는 Plugin
- Task는 실제 실행 단위
- Offset은 Kafka에 저장됨
- KRaft 환경에서도 문제없이 동작

---

# 결론

카프카를 설치하고 커넥트를 설치하여 카프카 큐에 실시간으로 스트리밍 되는 데이터를 구독해보는 튜토리얼을 진행했다.
