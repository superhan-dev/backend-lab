# kafka connect 실습

Datagen을 사용해서 가짜 카프카 데이터를 보내도록해서 파이프라인으로 데이터를 받을 수 있는지 모니터링 하는 튜토리얼이다.

`/docs` 경로에 있는 튜토리얼 문서를 통해 자세한 튜토리얼 내용을 확인할 수 있다.

# 파일 설명

## ./connect

커넥트를 실행시 `kafka-connect-datagen`이라는 기능을 사용하기 위해 Dockerfile을 정의하여 이미지를 빌드 후 진행할 수 았도록 미리 정의해둔다.

## 실행 방법

```
docker compose up -d
```
