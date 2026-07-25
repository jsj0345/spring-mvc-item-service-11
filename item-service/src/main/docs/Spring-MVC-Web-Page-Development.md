## 1. 예제에서 확인한 전체 흐름

이 예제의 핵심은 상품 데이터를 저장하는 기능보다 **웹 요청이 화면과 서버 코드를 오가는 과정**을 이해하는 데 있다.

```text
상품 목록 조회
→ 상품 상세 조회
→ 등록 폼 출력
→ 등록 요청 처리
→ 수정 폼 출력
→ 수정 요청 처리
```

화면을 보여 주는 요청은 주로 `GET`, 데이터를 변경하는 요청은 `POST`로 나누었다.

```text
GET  /basic/items
GET  /basic/items/{itemId}
GET  /basic/items/add
POST /basic/items/add
GET  /basic/items/{itemId}/edit
POST /basic/items/{itemId}/edit
```

같은 기능이라도 폼을 여는 단계와 실제 저장을 수행하는 단계는 역할이 다르므로 HTTP 메서드로 구분한다.

---

## 2. 상품 객체와 메모리 저장소

상품 객체는 화면과 저장소에서 함께 사용할 기본 데이터를 가진다.

```java
public class Item {

    private Long id;
    private String itemName;
    private Integer price;
    private Integer quantity;

    public Item() {
    }

    public Item(
            String itemName,
            Integer price,
            Integer quantity
    ) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
```

저장소는 상품 등록, 단건 조회, 전체 조회, 수정 기능을 제공한다.

```java
@Repository
public class ItemRepository {

    private static final Map<Long, Item> store =
            new HashMap<>();

    private static long sequence;

    public Item save(Item item) {
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id) {
        return store.get(id);
    }

    public List<Item> findAll() {
        return new ArrayList<>(store.values());
    }

    public void update(Long id, Item source) {
        Item target = findById(id);
        target.setItemName(source.getItemName());
        target.setPrice(source.getPrice());
        target.setQuantity(source.getQuantity());
    }

    public void clearStore() {
        store.clear();
    }
}
```

### 저장소 테스트에서 확인할 부분

- 저장한 객체를 식별자로 다시 찾을 수 있는가
- 여러 상품을 저장했을 때 전체 목록에 모두 포함되는가
- 수정한 값이 기존 상품에 반영되는가
- 테스트가 끝날 때 저장 데이터를 비워 서로 영향을 주지 않는가

### 이 구현의 한계

이 저장소는 데이터베이스 대신 메모리의 `Map`을 사용한다.

- 애플리케이션을 다시 시작하면 데이터가 사라진다.
- `static` 저장소는 테스트 간 상태를 공유하기 쉽다.
- `HashMap`과 단순 증가식 ID는 동시 요청을 고려한 구현이 아니다.
- 수정 대상이 없을 때의 처리도 따로 정의되어 있지 않다.

따라서 실제 저장 기술을 배우기 전, MVC 화면 흐름을 확인하기 위한 임시 구현으로 보는 것이 맞다.

---

## 3. 정적 HTML만으로는 실제 상품을 표시할 수 없다

처음 만든 상품 목록, 상세, 등록, 수정 HTML에는 예시 값이 직접 들어 있다.

```html
<td>10000</td>
<input value="상품A">
```

이 상태에서는 서버 저장소의 데이터가 바뀌어도 화면은 그대로다.

정적 HTML의 역할은 다음과 같다.

- 화면 배치 확인
- 입력 항목과 버튼 구성
- 페이지 간 이동 구조 확인
- CSS 적용 결과 확인

실제 상품 데이터를 보여 주려면 컨트롤러가 데이터를 조회하고, 뷰 템플릿이 그 값을 HTML에 반영해야 한다.

```text
정적 HTML
→ 화면 모양을 먼저 확인

Thymeleaf 템플릿
→ 서버에서 받은 데이터로 HTML 완성
```

---

## 4. 컨트롤러와 모델의 역할

상품 목록 요청이 들어오면 컨트롤러가 저장소에서 데이터를 조회해 모델에 넣는다.

```java
@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        model.addAttribute(
                "items",
                itemRepository.findAll()
        );

        return "basic/items";
    }
}
```

내가 이해한 역할은 다음과 같다.

```text
Controller
→ 요청을 받고 필요한 데이터 조회

Model
→ View에 전달할 값을 이름과 함께 보관

View
→ Model 값을 이용해 최종 HTML 생성
```

`"basic/items"`는 실제 파일 전체 경로가 아니라 논리적인 뷰 이름이다. 뷰 리졸버가 설정된 위치와 확장자를 조합해 템플릿을 찾는다.

### 생성자 주입

`@RequiredArgsConstructor`는 `final` 필드에 필요한 생성자를 만들어 준다.

```java
private final ItemRepository itemRepository;
```

컨트롤러가 사용할 저장소를 생성 시점에 전달받으므로 의존관계가 명확해진다.

### 초기 테스트 데이터

`@PostConstruct`를 이용하면 빈 생성과 의존관계 주입이 끝난 뒤 예제 상품을 추가할 수 있다.

```java
@PostConstruct
void init() {
    itemRepository.save(
            new Item("sampleA", 10000, 10)
    );
}
```

이 방식은 화면 확인에는 편하지만 애플리케이션이 시작될 때마다 데이터가 추가될 수 있으므로 학습용 초기화 코드로만 사용한다.

---

## 5. Thymeleaf로 목록 화면 만들기

Thymeleaf는 기존 HTML 속성을 `th:*` 속성으로 바꾸어 서버 데이터를 반영한다.

```html
<tr th:each="item : ${items}">
    <td th:text="${item.id}">1</td>
    <td th:text="${item.itemName}">sample</td>
    <td th:text="${item.price}">10000</td>
    <td th:text="${item.quantity}">10</td>
</tr>
```

### 자주 사용한 문법

| 문법 | 역할 |
|---|---|
| `${item.price}` | 모델 또는 반복 변수의 값 조회 |
| `th:text` | 태그 내부 문자열 교체 |
| `th:value` | 입력 요소의 `value` 교체 |
| `th:each` | 컬렉션 반복 |
| `th:href` | 이동 URL 생성 |
| `th:onclick` | 클릭 동작의 URL 생성 |
| `th:action` | 폼 전송 주소 설정 |
| `th:if` | 조건이 맞을 때만 요소 출력 |

### 반복 출력

```html
<tr th:each="item : ${items}">
```

모델의 `items`를 하나씩 꺼내 `item`이라는 이름으로 사용한다. 상품 수만큼 행이 만들어진다.

### URL 만들기

```html
<a th:href="@{/basic/items/{id}(id=${item.id})}">
    상세 보기
</a>
```

경로 변수 위치와 실제 값을 함께 작성할 수 있다.

리터럴 대체를 사용하면 문자열과 표현식을 한 번에 조합할 수 있다.

```html
<a th:href="@{|/basic/items/${item.id}|}">
```

### 자연스러운 HTML 유지

```html
<td th:text="${item.price}">10000</td>
```

파일을 직접 열면 기본값 `10000`이 보이고, 서버를 거치면 실제 상품 가격으로 교체된다.

즉, 정적 화면 시안과 서버 템플릿 역할을 한 HTML 안에서 함께 유지할 수 있다.

---

## 6. 상품 상세 화면

상세 요청에서는 경로의 상품 번호를 받아 저장소에서 상품 하나를 찾는다.

```java
@GetMapping("/{itemId}")
public String item(
        @PathVariable Long itemId,
        Model model
) {
    Item item = itemRepository.findById(itemId);
    model.addAttribute("item", item);
    return "basic/item";
}
```

뷰에서는 모델의 `item`을 입력 요소에 출력한다.

```html
<input
    type="text"
    th:value="${item.itemName}"
    readonly
>
```

상세 화면은 조회 목적이므로 값을 보여 주기만 하고 입력 요소를 수정하지 못하도록 `readonly`를 사용한다.

수정 화면으로 이동할 때는 현재 상품 번호를 URL에 포함한다.

```html
<button
    type="button"
    th:onclick="|location.href='@{/basic/items/{id}/edit(id=${item.id})}'|">
    상품 수정
</button>
```

---

## 7. 등록 폼과 등록 처리 분리

등록 화면과 저장 처리는 같은 경로를 사용하면서 메서드로 구분할 수 있다.

```java
@GetMapping("/add")
public String addForm() {
    return "basic/addForm";
}
```

```html
<form th:action method="post">
```

`th:action`에 별도 주소를 적지 않으면 현재 URL로 폼을 전송한다.

```text
GET  /basic/items/add
→ 등록 폼 표시

POST /basic/items/add
→ 입력한 상품 저장
```

URL은 같지만 HTTP 메서드가 다르므로 서로 다른 컨트롤러 메서드가 실행된다.

---

## 8. `@RequestParam`과 `@ModelAttribute`

HTML Form은 다음과 비슷한 형태의 데이터를 보낸다.

```text
itemName=keyboard&price=30000&quantity=5
```

각 값을 따로 받으면 직접 객체를 조립해야 한다.

```java
@PostMapping("/add")
public String add(
        @RequestParam String itemName,
        @RequestParam Integer price,
        @RequestParam Integer quantity,
        Model model
) {
    Item item =
            new Item(itemName, price, quantity);

    itemRepository.save(item);
    model.addAttribute("item", item);

    return "basic/item";
}
```

입력 항목이 늘어날수록 파라미터와 setter 코드도 함께 늘어난다.

`@ModelAttribute`를 사용하면 요청 파라미터를 객체 프로퍼티에 묶을 수 있다.

```java
@PostMapping("/add")
public String add(
        @ModelAttribute Item item
) {
    itemRepository.save(item);
    return "basic/item";
}
```

이때 Spring MVC는 다음 작업을 수행한다.

```text
Item 객체 생성
→ 같은 이름의 요청 파라미터를 프로퍼티에 바인딩
→ item이라는 이름으로 Model에도 추가
```

### 생략 가능한 부분

```java
@ModelAttribute("item") Item item
```

이름을 생략하면 일반적으로 클래스 이름을 기준으로 모델 이름이 정해진다.

```java
@ModelAttribute Item item
```

복합 객체에서는 애노테이션 자체를 생략할 수도 있다.

```java
Item item
```

하지만 학습 중에는 요청 파라미터를 객체로 묶는다는 의도를 명확히 드러내기 위해 `@ModelAttribute`를 적어 두는 편이 이해하기 쉽다.

### 이 단계에서의 한계

`Item` 도메인 객체를 폼 입력 객체로 바로 사용하고 있어 사용자가 보낼 수 있는 값과 저장 대상 필드가 강하게 연결된다.

현재 예제는 MVC 바인딩 흐름에 집중하므로 별도의 입력 검증이나 오류 화면 처리는 포함하지 않는다.

---

## 9. 상품 수정 흐름

수정도 폼 조회와 저장 처리를 나눈다.

```java
@GetMapping("/{itemId}/edit")
public String editForm(
        @PathVariable Long itemId,
        Model model
) {
    model.addAttribute(
            "item",
            itemRepository.findById(itemId)
    );

    return "basic/editForm";
}
```

수정 폼은 기존 값을 `th:value`로 채운다.

```html
<input
    name="price"
    th:value="${item.price}"
>
```

폼 제출 뒤에는 경로의 상품 ID와 입력 객체를 함께 사용한다.

```java
@PostMapping("/{itemId}/edit")
public String edit(
        @PathVariable Long itemId,
        @ModelAttribute Item item
) {
    itemRepository.update(itemId, item);

    return "redirect:/basic/items/{itemId}";
}
```

수정 처리 후 상세 페이지로 이동하므로 사용자는 변경 결과를 바로 확인할 수 있다.

---

## 10. 등록 직후 화면을 직접 반환할 때의 문제

상품을 저장한 POST 요청에서 곧바로 상세 템플릿을 반환하면 브라우저의 마지막 요청은 여전히 POST다.

```text
POST /basic/items/add
→ 상품 저장
→ 상세 HTML 렌더링
```

이 상태에서 새로고침하면 브라우저가 같은 POST 요청을 다시 전송할 수 있다. 그러면 동일한 상품이 또 저장될 가능성이 있다.

문제는 화면 자체가 아니라 브라우저에 남아 있는 마지막 요청 방식이다.

---

## 11. PRG 적용

POST 처리가 끝난 뒤 상세 화면으로 리다이렉트하면 브라우저가 새 GET 요청을 보낸다.

```java
@PostMapping("/add")
public String add(
        @ModelAttribute Item item
) {
    Item savedItem = itemRepository.save(item);

    return "redirect:/basic/items/"
            + savedItem.getId();
}
```

흐름은 다음처럼 바뀐다.

```text
POST /basic/items/add
→ 상품 저장
→ 리다이렉트 응답
→ GET /basic/items/{itemId}
→ 상세 화면 표시
```

이제 새로고침해도 마지막 요청인 GET이 반복되므로 등록 동작이 다시 실행되지 않는다.

이 흐름을 PRG(Post/Redirect/Get)라고 정리했다.

---

## 12. `RedirectAttributes`로 URL 값 전달

문자열 결합으로 리다이렉트 URL을 만들면 경로 값의 인코딩과 쿼리 파라미터 구성이 코드에 섞인다.

`RedirectAttributes`를 이용하면 경로 변수와 나머지 값을 구분해 전달할 수 있다.

```java
@PostMapping("/add")
public String add(
        @ModelAttribute Item item,
        RedirectAttributes attributes
) {
    Item savedItem = itemRepository.save(item);

    attributes.addAttribute(
            "itemId",
            savedItem.getId()
    );
    attributes.addAttribute(
            "status",
            true
    );

    return "redirect:/basic/items/{itemId}";
}
```

결과 URL은 다음 형태가 된다.

```text
/basic/items/3?status=true
```

- `itemId`는 경로 변수에 사용된다.
- 남은 `status`는 쿼리 파라미터가 된다.

상세 화면에서는 등록 직후에만 메시지를 보여 줄 수 있다.

```html
<h2
    th:if="${param.status}"
    th:text="'저장되었습니다.'">
</h2>
```

`status` 파라미터가 있을 때만 요소가 출력된다.

---

## 13. 내가 정리한 핵심 연결 관계

```text
Item
→ 화면과 저장소에서 사용할 상품 데이터

ItemRepository
→ 메모리에서 상품을 저장하고 조회

Controller
→ 요청에 맞는 저장소 기능 호출

Model
→ 컨트롤러 결과를 View에 전달

Thymeleaf
→ Model 데이터를 HTML에 반영

@ModelAttribute
→ Form 파라미터를 객체에 바인딩

RedirectAttributes
→ 리다이렉트 경로와 쿼리 값 구성

PRG
→ POST 중복 실행을 피하도록 GET 화면으로 전환
```

## 핵심 정리

- 정적 HTML은 화면 모양을 확인하는 데 적합하지만 실제 데이터를 반영하지 못한다.
- 컨트롤러는 데이터를 조회해 모델에 넣고 논리적인 뷰 이름을 반환한다.
- Thymeleaf는 `th:*` 속성으로 기존 HTML을 서버 데이터에 맞게 바꾼다.
- `@ModelAttribute`는 Form 파라미터를 객체에 바인딩하고 모델에도 넣는다.
- 등록과 수정은 폼을 여는 GET과 처리하는 POST로 나눌 수 있다.
- POST에서 화면을 직접 반환하면 새로고침으로 요청이 반복될 수 있다.
- PRG를 적용하면 저장 뒤 상세 페이지를 GET으로 다시 조회한다.
- `RedirectAttributes`는 경로 변수와 쿼리 파라미터를 안전하게 구성하는 데 사용한다.
