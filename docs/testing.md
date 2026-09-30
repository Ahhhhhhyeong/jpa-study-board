# 테스트 실행 및 작성 방법

## 현재 테스트의 범위

`src/test/java/com/example/board/controller/BoardControllerTest.java`는
`POST /board/write` 요청을 처리하는 컨트롤러 테스트다.

- 제목과 내용을 전달하면 `BoardService.save()`를 호출하는지 확인한다.
- 응답이 `302`이고 이동 주소가 `/board/list`인지 확인한다.
- 제목이나 내용이 누락되면 `400` 응답을 반환하고 서비스를 호출하지 않는지 확인한다.

실제 서버를 띄우거나 MySQL에 데이터를 저장하지 않는다.
서비스는 가짜 객체(mock)로 대체하므로 Docker 실행도 필요 없다.
리다이렉트 주소만 검증하며 이동한 페이지를 실제로 호출하거나 JSP를 렌더링하지 않는다.

## 테스트 실행

터미널에서 `pom.xml`과 `mvnw`가 있는 프로젝트 루트로 이동한다.

### 컨트롤러 테스트만 실행

```bash
./mvnw -Dtest=BoardControllerTest test
```

### 메서드 하나만 실행

```bash
./mvnw '-Dtest=BoardControllerTest#writeBoard' test
```

### 전체 테스트 실행

```bash
./mvnw test
```

전체 실행에는 `BoardApplicationTests.contextLoads()`도 포함된다.
이 테스트는 `@SpringBootTest`로 전체 앱을 시작하므로 DB 연결 설정과 실행 중인 DB가 필요하다.
현재 로컬 MySQL 설정을 사용하려면 다음처럼 실행한다.

```bash
docker compose up -d --wait
./mvnw -Dspring.profiles.active=local test
```

전체 테스트와 컨트롤러 테스트의 실행 조건은 다르다.
위 명령이 테이블을 자동 생성하는 것은 아니며, 향후 DB 저장 테스트에는 테이블 준비도 필요하다.

### VS Code에서 실행

1. 테스트 파일을 연다.
2. 클래스 또는 테스트 메서드 위의 **Run Test**를 누른다.
3. **Testing** 패널에서 실행 결과를 확인한다.

실행 버튼이 없다면 Java 프로젝트 로딩이 끝났는지 확인한다.
이 프로젝트에는 Java 테스트 확장이 설치되어 있다.

## 결과 확인

성공한 실행에서는 다음과 같은 메시지가 나온다.

```text
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

- `Failures`: 예상 결과와 실제 결과가 다른 검증 실패
- `Errors`: 예외나 설정 문제 등으로 테스트가 정상 완료되지 못함
- `Skipped`: 실행하지 않은 테스트

상세 결과는 `target/surefire-reports/`에 생성된다.
현재 `BoardControllerTest`의 3개 테스트는 실행하여 모두 통과한 상태다.
전체 테스트 실행 결과는 별도로 확인해야 한다.

## 테스트 코드 읽기

### 컨트롤러만 준비하기

```java
@WebMvcTest(BoardController.class)
class BoardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BoardService boardService;
}
```

- `@WebMvcTest`: 지정한 컨트롤러와 웹 요청 처리에 필요한 Spring 구성을 준비한다.
- `MockMvc`: 서버를 실제로 실행하지 않고 Spring MVC에 요청을 전달한다.
- `@MockitoBean`: Spring 컨텍스트에 가짜 `BoardService`를 등록한다.
  따라서 실제 서비스의 JPA 저장 로직은 실행되지 않는다.

이 프로젝트는 Spring Boot 4를 사용하므로 import는 다음과 같다.

```java
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
```

### 요청과 결과 검증하기

```java
@Test
@DisplayName("게시글 작성 요청은 저장 서비스를 호출하고 목록으로 이동한다")
void writeBoard() throws Exception {
    mockMvc.perform(post("/board/write")
                    .param("title", "첫 번째 게시글")
                    .param("content", "JPA 공부 중입니다."))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("/board/list"));

    verify(boardService).save("첫 번째 게시글", "JPA 공부 중입니다.");
}
```

- `@Test`: JUnit이 실행할 테스트 메서드다.
- `@DisplayName`: 실행 결과에 표시할 설명이다.
- `post()`: HTTP POST 요청을 만든다.
- `param()`: 컨트롤러의 `@RequestParam`에 전달할 값을 설정한다.
  현재 API는 JSON 본문 대신 요청 파라미터를 받는다.
- `status().isFound()`: HTTP 상태 코드 `302`를 확인한다.
- `redirectedUrl()`: 응답의 리다이렉트 주소를 확인한다.
- `verify()`: 지정한 인자로 서비스 메서드가 한 번 호출됐는지 확인한다.

`post`, `status`, `redirectedUrl`, `verify`는 기존 테스트 파일의 static import를 사용한다.

## 새 테스트 작성하기

테스트 파일은 실제 코드와 대응하는 패키지에 두고 이름을 `클래스명Test.java`로 작성한다.

```text
src/main/java/com/example/board/controller/BoardController.java
src/test/java/com/example/board/controller/BoardControllerTest.java
```

각 테스트는 다음 순서로 작성한다.

1. **준비(Given)**: 입력값과 필요한 가짜 객체의 동작을 정한다.
2. **실행(When)**: 테스트할 요청이나 메서드를 호출한다.
3. **검증(Then)**: 응답, 반환값, 호출 여부를 확인한다.

예를 들어 제목이 없는 요청을 확인하려면:

```java
@Test
@DisplayName("제목이 누락되면 400 응답을 반환하고 저장하지 않는다")
void writeWithoutTitle() throws Exception {
    mockMvc.perform(post("/board/write")
                    .param("content", "내용만 전달합니다."))
            .andExpect(status().isBadRequest());

    verifyNoInteractions(boardService);
}
```

`verifyNoInteractions()`는 서비스가 전혀 호출되지 않았는지 확인한다.
여기서 검증하는 것은 파라미터의 **누락**이다.
`.param("title", "")`처럼 빈 문자열을 전달하는 경우는 별도 검증이 필요하며,
현재 컨트롤러에는 빈 문자열을 거부하는 검증이 없다.

테스트마다 독립적으로 입력을 준비하고, 실행 순서에 의존하지 않는다.
새 기능을 추가하면 정상 요청뿐 아니라 해당 기능의 중요한 실패 조건도 확인한다.

## DB 저장까지 확인하고 싶을 때

현재 테스트의 `verify(boardService).save(...)`는 서비스 호출을 확인할 뿐,
DB에 게시글이 저장됐음을 증명하지 않는다.

실제 저장을 확인하려면 별도의 통합 테스트에서 실제 서비스를 사용하고,
저장 후 DB를 조회해 제목과 내용을 검증해야 한다.
이 경우 DB 연결과 테이블 준비, 테스트 데이터 정리 방법도 함께 정한다.

## 참고 자료

- [Spring 공식 가이드: 웹 계층 테스트](https://spring.io/guides/gs/testing-web/)
- [Spring Boot: WebMvcTest](https://docs.spring.io/spring-boot/api/java/org/springframework/boot/webmvc/test/autoconfigure/WebMvcTest.html)
