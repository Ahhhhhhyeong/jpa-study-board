# 로컬 MySQL 사용

Docker Desktop을 실행한 뒤 프로젝트 폴더에서 실행합니다.

```sh
docker compose up -d --wait
```

MySQL 8.4가 실행되며 최초 실행 시 `board` 데이터베이스와 사용자를 생성합니다.
접속 정보: 호스트 `localhost`, 포트 `3306`, DB `board`, 사용자 `board`, 비밀번호 `board_local`.
이 비밀번호는 로컬 개발 전용입니다.

Spring Boot에서 연결하려면 `local` 프로필로 실행합니다.

```sh
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

IDE에서는 활성 프로필에 `local`을 지정하거나 환경 변수 `SPRING_PROFILES_ACTIVE=local`을 설정합니다.
VS Code의 `Spring Boot-BoardApplication<board>` 실행 구성에는 `local` 프로필이 지정되어 있습니다.

MySQL 콘솔 접속:

```sh
docker compose exec mysql mysql -u board -p board
```

비밀번호 입력 시 `board_local`을 입력합니다. 접속 후 `SHOW TABLES;`로 테이블을 확인하고 `exit`로 나옵니다.
`local` 프로필은 `spring.jpa.hibernate.ddl-auto=update`를 사용하므로 앱 실행 시 엔티티에 맞춰 테이블을 생성·갱신합니다.

상태 확인 및 종료:

```sh
docker compose ps
docker compose stop
```

다시 시작할 때는 `docker compose up -d --wait`를 사용합니다.
데이터는 `mysql_data` 볼륨에 남습니다. `docker compose down`으로 컨테이너를 제거해도 보존되지만,
`docker compose down -v`는 데이터를 삭제합니다.
Compose의 DB 이름·사용자·비밀번호 환경 변수는 빈 볼륨의 최초 초기화에만 적용됩니다.
