# Spring MVC Item Service 11

Spring MVC와 Thymeleaf를 사용해 상품 등록, 조회, 수정 흐름을 구현하고 예제 코드와 문서로 정리한 저장소입니다.

상품 도메인, 메모리 기반 상품 저장소, Controller 요청 처리, Model 데이터 전달, Thymeleaf 화면 렌더링, 상품 등록 후 Redirect 처리 흐름을 학습했습니다.

## 학습 목적

Spring MVC에서 상품 데이터를 등록하고 조회하고 수정하는 기본 웹 애플리케이션 흐름을 이해하기 위해 정리했습니다.

단순 HTML 화면에서 시작해 Controller, Model, Thymeleaf 템플릿을 연결하면서 서버에서 전달한 데이터가 화면에 렌더링되는 구조를 확인하는 데 중점을 두었습니다.

## 학습 내용

* 상품 도메인 `Item` 설계
* 메모리 기반 `ItemRepository` 구현
* 상품 저장, 단건 조회, 전체 조회, 수정 기능 구현
* `@Controller`와 `@RequestMapping`을 사용한 상품 화면 요청 처리
* `@GetMapping`, `@PostMapping`, `@PathVariable` 활용
* `Model`을 사용한 View 데이터 전달
* `@ModelAttribute` 기반 요청 파라미터 바인딩 흐름 확인
* 상품 목록, 상품 상세, 상품 등록, 상품 수정 화면 구성
* Thymeleaf `th:text`, `th:href`, `th:value`, `th:onclick` 사용
* 상품 등록 후 `RedirectAttributes`를 사용한 Redirect 처리
* 테스트용 초기 상품 데이터 등록
* Repository 단위 테스트 작성

## 학습 포인트

* `Item` 객체를 통해 상품 ID, 상품명, 가격, 수량 데이터를 관리하는 구조를 학습했습니다.
* `ItemRepository`에서 `Map`과 `sequence`를 사용해 메모리 기반 저장소를 구현했습니다.
* 상품 저장, 조회, 목록 조회, 수정 기능을 Repository 메서드로 분리했습니다.
* Controller에서 Repository를 주입받아 상품 목록, 상세, 등록, 수정 요청을 처리하는 흐름을 확인했습니다.
* `Model`에 데이터를 담아 Thymeleaf 템플릿으로 전달하는 방식을 학습했습니다.
* 정적 HTML 화면과 Thymeleaf 템플릿 화면을 함께 두고, 서버 데이터가 동적으로 렌더링되는 차이를 확인했습니다.
* `th:each`를 사용해 상품 목록을 반복 출력하는 방식을 학습했습니다.
* `th:value`를 사용해 상품 상세와 수정 화면에 서버 데이터를 출력하는 흐름을 확인했습니다.
* `th:href`와 `th:onclick`을 사용해 상품 상세, 수정, 목록 이동 URL을 동적으로 생성했습니다.
* 상품 등록 후 바로 View를 반환하는 방식과 Redirect를 사용하는 방식의 차이를 코드로 비교했습니다.
* `RedirectAttributes`를 사용해 등록된 상품 ID와 상태 값을 Redirect URL에 전달하는 흐름을 확인했습니다.
* `@PostConstruct`를 사용해 애플리케이션 실행 시 테스트용 상품 데이터를 등록했습니다.
* `ItemRepositoryTest`를 통해 상품 저장, 전체 조회, 수정 기능을 검증했습니다.

## 디렉터리 구조

```
spring-mvc-item-service-11
├── gradle
├── src
│   ├── main
│   │   ├── docs
│   │   │   └── Spring-MVC-Web-Page-Development.md
│   │   ├── java
│   │   │   └── hello
│   │   │       └── itemservice
│   │   │           ├── ItemServiceApplication.java
│   │   │           ├── domain
│   │   │           │   └── item
│   │   │           │       ├── Item.java
│   │   │           │       └── ItemRepository.java
│   │   │           └── web
│   │   │               └── item
│   │   │                   └── basic
│   │   │                       └── BasicItemController.java
│   │   └── resources
│   │       ├── static
│   │       │   ├── css
│   │       │   └── html
│   │       │       ├── addForm.html
│   │       │       ├── editForm.html
│   │       │       ├── item.html
│   │       │       └── items.html
│   │       └── templates
│   │           └── basic
│   │               ├── addForm.html
│   │               ├── editForm.html
│   │               ├── item.html
│   │               └── items.html
│   └── test
│       └── java
│           └── hello
│               └── itemservice
│                   ├── ItemServiceApplicationTests.java
│                   └── domain
│                       └── item
│                           └── ItemRepositoryTest.java
├── build.gradle
├── gradlew
├── gradlew.bat
└── settings.gradle
```

## 실행 환경

* Java 17
* Spring Boot 3.5.15
* Spring MVC
* Thymeleaf
* Gradle 8.14.5
* Lombok
* JUnit 5
* AssertJ
* IntelliJ IDEA
* HTML
* Bootstrap
