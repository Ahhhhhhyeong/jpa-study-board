# JPA 학습 노트: Transaction (트랜잭션)

- 학습 날짜: 2026-09-30
- 관련 코드: `src/main/java/com/example/board/service/BoardService.java`

## 오늘의 질문

- 서비스 메서드에 `@Transactional`을 붙이는 이유는 무엇일까?
- 게시글을 저장하다가 예외가 발생하면 DB에는 어떤 결과가 남을까?
- 트랜잭션은 영속성 컨텍스트, 변경 감지와 어떻게 연결될까?

## 내가 이해한 개념

### 한 문장으로 설명

> 트랜잭션은 여러 DB 작업을 하나의 작업 단위로 묶어, 함께 확정하거나 함께 취소하는 경계다.

게시글 저장과 다른 DB 작업을 하나의 서비스 메서드에서 수행한다면,
중간에 실패했을 때 일부만 저장되는 상황을 막기 위해 트랜잭션을 사용한다.
DB 변경 확정은 커밋(commit), 변경 취소는 롤백(rollback)이라고 한다.

### 동작 흐름

다른 트랜잭션이 없는 상태에서 Spring이 관리하는 서비스를 외부에서 호출하는 경우:

```text
서비스의 @Transactional 메서드 호출
    → 트랜잭션 시작
    → 조회 / 저장 / 수정 / 삭제
    → 정상 종료: flush 후 커밋
    → 롤백 대상 예외 발생: 롤백
```

`flush`는 엔티티의 변경을 SQL로 DB에 반영하는 과정이다.
커밋과는 다르므로 SQL이 실행됐더라도 커밋 전에 롤백할 수 있다.
SQL은 ID 생성이나 쿼리 실행 등으로 메서드 종료 전에 실행될 수도 있다.

### 적용 조건과 주의점

- import는 `org.springframework.transaction.annotation.Transactional`을 사용한다.
- 기본 전파 방식은 `REQUIRED`다. 기존 트랜잭션이 있으면 참여하고, 없으면 시작한다.
- 일반적인 Spring 기본 설정에서는 `RuntimeException`과 `Error`가 밖으로 전달되면 롤백한다.
  체크 예외도 롤백하려면 `rollbackFor = Exception.class` 등을 지정한다.
- 예외를 메서드 안에서 잡고 정상 종료하면 자동 롤백을 기대할 수 없다.
- 기본 프록시 방식에서는 외부에서 Spring 빈을 통해 호출해야 적용된다.
  같은 클래스 안에서 `this.save()`처럼 호출하면 해당 메서드의 어노테이션이 새로 적용되지 않는다.
- `new BoardService(...)`로 직접 만든 객체에는 Spring의 트랜잭션 프록시가 적용되지 않는다.
- `readOnly = true`는 읽기 전용 처리에 대한 힌트다. 쓰기 작업을 무조건 차단하는 장치는 아니다.
- 트랜잭션 롤백은 DB 작업에 적용된다. 이메일 전송이나 파일 쓰기까지 자동으로 취소하지는 않는다.

위 동작과 설정은 [Spring @Transactional 공식 문서](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html)와
[롤백 규칙 공식 문서](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/rolling-back.html)를 참고했다.

## Board 프로젝트에 적용

- 적용 위치: `BoardService.save()`와 `BoardService.findById()`
- 사용 이유: 저장 작업의 트랜잭션 경계를 서비스에서 정하고, 조회에는 읽기 전용 설정을 사용한다.

현재 작성된 저장 메서드:

```java
@Transactional
public void save(String title, String content) {
    Board board = new Board(title, content);
    boardRepository.save(board);
}
```

현재 Repository는 `JpaRepository` 상속 방식이 아니라 `EntityManager`를 직접 사용하는 클래스다.
`boardRepository.save()`는 내부에서 `em.persist(board)`를 호출한다.
이 저장 작업에는 활성 트랜잭션이 필요하므로 서비스의 `@Transactional`이 중요하다.

조회 메서드:

```java
@Transactional(readOnly = true)
public Board findById(Long id) {
    return boardRepository.findById(id);
}
```

조회만 하는 메서드는 읽기 전용으로 표시한다. 수정 작업은 별도의 일반 트랜잭션에서 처리한다.

### 영속성 컨텍스트와 변경 감지 연결

다음은 앞으로 작성할 수 있는 수정 메서드 예시이며, 현재 서비스에 추가한 코드는 아니다.

```java
@Transactional
public void update(Long id, String title, String content) {
    Board board = boardRepository.findById(id);
    if (board == null) {
        throw new IllegalArgumentException("게시글이 없습니다.");
    }
    board.update(title, content);
}
```

이 트랜잭션 안에서 조회한 `board`는 영속성 컨텍스트가 관리한다.
필드를 바꾸면 JPA가 flush 시 변경을 감지해 UPDATE SQL을 실행한다.
따라서 별도로 `save()`를 다시 호출할 필요가 없다.
객체 필드 변경 자체와 DB 변경 확정은 서로 다른 단계다.

## 직접 확인한 실험

**아래는 실험 계획이다. 아직 실행하지 않았으며 실제 결과는 확인 후 기록한다.**

- 실행 환경 / 설정: Docker MySQL, `local` 프로필, `board` 테이블 준비
- 실행 방법: 정상 저장과 저장 직후 런타임 예외가 발생하는 경우를 비교한다.
- 예상 결과: 정상 저장은 행이 남고, 예외가 발생한 저장은 롤백되어 행이 남지 않는다.
- 실제 결과: 미실행

실험용 서비스 메서드 예시:

```java
@Transactional
public void saveAndFail() {
    boardRepository.save(new Board("tx-rollback-sample", "롤백 실험"));
    throw new IllegalStateException("저장 후 실패");
}
```

1. 준비된 앱에서 정상 저장 메서드를 호출해 `tx-commit-sample` 제목의 게시글을 저장한다.
2. 별도 호출로 Spring 빈의 `saveAndFail()`을 실행한다.
3. 각 호출이 종료된 뒤 MySQL 콘솔에서 아래 쿼리로 결과를 비교한다.
4. 테스트로 실험한다면 테스트 자체의 `@Transactional`이 서비스 트랜잭션을 감싸지 않도록 한다.

```sql
SELECT id, title, content
FROM board
WHERE title IN ('tx-commit-sample', 'tx-rollback-sample');
```

`id`가 자동 증가하므로 롤백 후 번호가 건너뛰어도 이상이 아니다.
롤백 여부는 ID의 연속성이 아니라 실제 행이 남았는지로 확인한다.

### 결과를 통해 알게 된 것

- 실행 후 기록할 항목: 정상 저장 행과 롤백된 행의 차이
- 확인할 항목: SQL 로그에 INSERT가 보이는 것과 커밋되어 행이 남는 것은 같은 의미인가?

## 헷갈렸던 점과 해결

- 처음 생각: `save()`를 호출하면 그 순간 저장이 확정된다고 생각했다.
- 확인 후 이해: SQL 실행과 커밋은 다르다. INSERT가 실행된 뒤에도 롤백될 수 있다.
- 확인한 근거: 현재 Repository의 `em.persist()` 사용 코드와 Spring 공식 롤백 문서.
  실제 DB 실험은 아직 하지 않았다.

- 처음 생각: 예외가 발생하면 종류와 상관없이 항상 롤백된다고 생각했다.
- 확인 후 이해: 기본 롤백 규칙은 런타임 예외와 Error이며, 체크 예외는 설정이 필요하다.
- 확인한 근거: Spring 공식 롤백 문서의 기본 규칙.

## 참고 자료

- [Spring: @Transactional 사용법](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html)
- [Spring: 트랜잭션 롤백 규칙](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/rolling-back.html)
- 프로젝트의 `BoardService.java`, `BoardRepository.java`, `Board.java`

## 다음에 확인할 질문

- [ ] 같은 클래스 안의 메서드 호출에서는 왜 트랜잭션 어노테이션이 적용되지 않을까?
- [ ] `flush()`를 직접 호출한 후 예외를 발생시켜도 롤백될까?
- [ ] 영속성 컨텍스트가 관리하지 않는 객체를 수정하면 어떻게 될까?
- [ ] 기존 트랜잭션에 참여하는 것과 새 트랜잭션을 만드는 것은 어떤 차이가 있을까?

## 복습

- [ ] 트랜잭션, 커밋, 롤백을 내 말로 설명할 수 있다.
- [ ] 현재 Board 코드에서 트랜잭션 경계를 찾을 수 있다.
- [ ] flush와 커밋의 차이를 설명할 수 있다.
- [ ] 정상 저장과 롤백 실험을 실행하고 실제 결과를 기록했다.
