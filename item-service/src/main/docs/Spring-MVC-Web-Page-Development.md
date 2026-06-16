# 스프링 MVC - 웹 페이지 만들기

## 1. 상품 도메인 개발

**Item - 상품 객체**

```java
package hello.itemservice.domain.item;

import lombok.Data;

@Data
public class Item {
  
  private Long id;
  private String itemName;
  private Integer price;
  private Integer quantity;
  
  public Item() {
    
  }
  
  public Item(String itemName, Integer price, Integer quantity) {
    this.itemName = itemName;
    this.price = price;
    this.quantity = quantity; 
  }
  
}
```

**itemRepository - 상품 저장소**

```java
package hello.itemservice.domain.item;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepository {

  private static final Map<Long, Item> store = new HashMap<>(); // static 사용
  private static long sequence = 0L; // static 사용

  public Item save(Item item) {
    item.setId(++sequence);
    store.put(item.getId(), item);
    return item;
  }

  public Item findById(Long id) {
    return store.get(id);
  }

  public List<Item> findAll() {
    return store.get(id);
  }
  
  public void update(Long itemId, Item updateParam) {
    Item findItem = findById(itemId);
    findItem.setItemName(updateParam.getItemName());
    findItem.setPrice(updateParam.getPrice());
    findItem.setQuantity(updateParam.getQuantity());
  }
  
  public void clearStore() {
    store.clear(); 
  }

}
```

**ItemRepositoryTest - 상품 저장소 테스트**
```java
package hello.itemservice.domain.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRepositoryTest {
  
  ItemRepository itemRepository = new ItemRepository();
  
  @AfterEach
  void afterEach() {
    itemRepository.clearStore();
  }
  
  @Test
  void save() {
    //given
    Item item = new Item("itemA", 10000, 10);
    
    //when
    Item savedItem = itemRepository.save(item);
    
    //then
    Item findItem = itemRepository.findById(item.getId());
    assertThat(findItem).isEqualTo(savedItem);  
  }
  
  @Test
  void findAll() {
    //given
    Item item1 = new Item("item1", 10000, 10);
    Item item2 = new Item("item2", 20000, 20);
    
    itemRepository.save(item1);
    itemRepository.save(item2);
    
    //when
    List<Item> result = itemRepository.findAll();
    
    //then
    assertThat(result.size()).isEqualTo(2);
    assertThat(result).contains(item1, item2);
  }
  
  @Test
  void updateItem() {
    //given
    Item item = new Item("item1", 10000, 10);
    
    Item savedItem = itemRepository.save(item);
    Long itemId = savedItem.getId();
    
    //when
    Item updateParam = new Item("item2", 20000, 30); 
    itemRepository.update(itemId, updateParam);
    
    Item findItem = itemRepository.findById(itemId);
    
    //then
    assertThat(findItem.getItemName()).isEqualTo(updateParam.getItemName());
    assertThat(findItem.getPrice()).isEqualTo(updateParam.getPrice());
    assertThat(findItem.getQuantity()).isEqualTo(updateParam.getQuantity());
  }
}
```

위 코드들을 다 테스트 해 본 결과, 문제는 없다.

## 2. 상품 서비스 HTML
**상품 목록 HTML**
```text
resources/static/html/items.html
```
```html
<!DOCTYPE HTML>
<html>
<head>
    <meta charset="utf-8">
    <link href="../css/bootstrap.rtl.min.css" rel="stylesheet">
</head>
<body>
<div class="container" style="max-width: 600px">
    <div class="py-5 text-center">
        <h2>상품 목록</h2>
    </div>
    <div class="row">
        <div class="col">
            <button class="btn btn-primary float-end"
                    onclick="location.href='addForm.html'" type="button">상품 등록
            </button>
        </div>
    </div>
    <hr class="my-4">
    <div>
        <table class="table">
            <thead>
            <tr>
                <th>ID</th>
                <th>상품명</th>
                <th>가격</th>
                <th>수량</th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td><a href="item.html">1</a></td>
                <td><a href="item.html">테스트 상품1</a></td>
                <td>10000</td>
                <td>10</td>
            </tr>
            <tr>
                <td><a href="item.html">2</a></td>
                <td><a href="item.html">테스트 상품2</a></td>
                <td>20000</td>
                <td>20</td>
            </tr>
            </tbody>
        </table>
    </div>
</div> 
</body>
</html>
```

**상품 상세 HTML**
```text
resources/static/html/item.html
```
```html
<!DOCTYPE HTML>
<html>
<head>
    <meta charset="utf-8">
    <link href="../css/bootstrap.rtl.min.css" rel="stylesheet">
    <style>
        .container {
        max-width: 560px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="py-5 text-center">
        <h2>상품 상세</h2>
    </div>
    <div>
        <label for="itemId">상품 ID</label>
        <input type="text" id="itemId" name="itemId" class="form-control"
               value="1" readonly>
    </div>
    <div>
        <label for="itemName">상품명</label>
        <input type="text" id="itemName" name="itemName" class="form-control"
               value="상품A" readonly>
    </div>
    <div>
        <label for="price">가격</label>
        <input type="text" id="price" name="price" class="form-control"
               value="10000" readonly>
    </div>
    <div>
        <label for="quantity">수량</label>
        <input type="text" id="quantity" name="quantity" class="form-control"
               value="10" readonly>
    </div>
    <hr class="my-4">
    <div class="row">
        <div class="col">
            <button class="w-100 btn btn-primary btn-lg"
                    onclick="location.href='editForm.html" type="button">상품 수정</button>
        </div>
        <div class="col">
            <button class="w-100 btn btn-secondary btn-lg"
                    onclick="location.href='items.html'" type="button">목록으로</button>
        </div>
    </div>
</div> 
</body>
</html>
```

**상품 등록 폼 HTML**
```text
resources/static/html/addForm.html
```
```html
<!DOCTYPE HTML>
<html>
<head>
    <meta charset="utf-8">
    <link href="../css/bootstrap.rtl.min.css" rel="stylesheet">
    <style>
        .container {
        max-width: 560px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="py-5 text-center">
        <h2>상품 등록 폼</h2>
    </div>
    <h4 class="mb-3">상품 입력</h4>
    <form action="item.html" method="post">
        <div>
            <label for="itemName">상품명</label>
            <input type="text" id="itemName" name="itemName" class="form-control"
                   placeholder="이름을 입력하세요">
        </div>
        <div>
            <label for="price">가격</label>
            <input type="text" id="price" name="price" class="form-control"
                   placeholder="가격을 입력하세요">
        </div>
        <div>
            <label for="quantity">수량</label>
            <input type="text" id="quantity" name="quantity" class="form-control"
                   placeholder="수량을 입력하세요">
        </div>
        <hr class="my-4">
        <div class="row">
            <div class="col">
                <button class="w-100 btn btn-primary btn-lg" type="submit">상품 등록</button>
            </div>
            <div class="col">
                <button class="w-100 btn btn-secondary btn-lg"
                        onclick="location.href='items.html'" type="button">취소</button>
            </div>
        </div>
    </form>
</div> </body>
</html>
```

**상품 수정 폼 HTML**
```text
resources/static/html/editForm.html
```
```html
<!DOCTYPE HTML>
<html>
<head>
    <meta charset="utf-8">
    <link href="../css/bootstrap.rtl.min.css" rel="stylesheet">
    <style>
        .container {
        max-width: 560px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="py-5 text-center">
        <h2>상품 수정 폼</h2>
    </div>
    <form action="item.html" method="post">
        <div>
            <label for="id">상품
                ID</label>
            <input type="text"
                   id="id" name="id" class="form-control" value="1"
                   readonly>
        </div>
        <div>
            <label for="itemName">상품명</label>
            <input type="text" id="itemName" name="itemName" class="form-
control" value="상품A">
        </div>
        <div>
            <label for="price">가격</label>
            <input type="text" id="price" name="price" class="form-control"
                   value="10000">
        </div>
        <div>
            <label for="quantity">수량</label>
            <input type="text" id="quantity" name="quantity" class="form-control" value="10">
        </div>
        <hr class="my-4">
        <div class="row">
            <div class="col">
                <button class="w-100 btn btn-primary btn-lg" type="submit">저장</button>
            </div>
            <div class="col">
                <button class="w-100 btn btn-secondary btn-lg"
                        onclick="location.href='item.html'" type="button">취소</button>
            </div>
        </div>
    </form>
</div> </body>
</html>
```

## 3. 상품 목록 - 타임리프
본격적으로 컨트롤러와 뷰 템플릿을 개발해보자.

**BasicItemController**

```java
package hello.itemservice.web.item.basic;

import hello.itemservice.domain.item.Item;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {

  private final ItemRepository itemRepository;

  @GetMapping
  public String items(Model model) {
    List<Item> items = itemRepository.findAll();
    model.addAttribute("items", items);
    return "basic/items";
  }

  /**
   * 테스트용 데이터 추가
   */
  @PostConstruct
  public void init() {
    itemRepository.save(new Item("testA", 10000, 10));
    itemRepository.save(new Item("testB", 20000, 20));
  }
}
```

컨트롤러 로직은 itemRepository에서 모든 상품을 조회한 다음에 모델에 담는다. 그리고 뷰 템플릿을 호출한다.
- @RequiredArgsConstructor
-> final이 붙은 멤버변수만 사용해서 생성자를 자동으로 만들어준다.

```java
public BasicItemController(ItemRepository itemRepository) {
 this.itemRepository = itemRepository;
}
```
- 이렇게 생성자가 딱 1개만 있으면 스프링이 해당 생성자에 @Autowired로 의존관계를 주입해준다. 
- 따라서 final 키워드를 빼면 안된다! 그러면 ItemRepository 의존관계 주입이 안된다.

**테스트용 데이터 추가**
- 테스트용 데이터가 없으면 회원 목록 기능이 정상 동작하는지 확인하기 어렵다.
- @PostConstruct : 해당 빈의 의존관계가 모두 주입되고 나면 초기화 용도로 호출된다.
- 여기서는 간단히 테스트용 데이터를 넣기 위해서 사용했다.

**개념 정리**
- Model : 컨트롤러에서 생성한 데이터를 View로 전달하기 위해 담아두는 바구니 역할을 한다.
- @ModelAttribute : 클라이언트가 보낸 HTTP 요청 파라미터(Form 데이터나 쿼리 스트링)를 자바 객체에 자동으로 바인딩(매핑)하고,
동시에 Model에 알아서 담아주는 역할을 한다.
- ModelAndView : Model(데이터 객체)과 View(논리적인 뷰 경로)를 모두 한 번에 담아서 반환할 수 있는 객체이다. 
- ViewResolver : 논리적인 이름을 받아 물리적인 경로와 객체로 완성해 주는 스프링 MVC의 내부 컴포넌트.
- ViewTemplate : 뷰 리졸버가 찾아낸 실제 HTML 파일 그 자체이자, 그 안에 적힌 문법을 해석하는 도구.

items.html 정적 HTML을 뷰 템플릿 영역으로 복사하고 수정해보자.
```html
<!DOCTYPE HTML>
<html xmlns:th="http:www.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <link href="../css/bootstrap.rtl.min.css"
          th:href="@{/css/bootstrap.rtl.min.css}" rel="stylesheet">
</head>
<body>
    <div class="container" style="max-width: 600px">
        <div class="py-5 text-center">
            <h2>상품 목록</h2>
        </div>
        
        <div class="row">
            <div class="col">
                <button class="btn btn-primary float-end"
                        onclick="location.href='addForm.html'"
                        th:onclick="|location.href='@{/basic/items/add}'|"
                        type="button">
                    상품 등록
                </button>
            </div>
        </div>
        
        <hr class="my-4">
        <div>
            <table class="table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>상품명</th>
                    <th>가격</th>
                    <th>수량</th>
                </tr>
                </thead>
                <tbody>
                    <tr th:each="item : ${items}">
                        <td><a href="item.html" th:href="@{/basic/items/{itemId}(itemId=${item.id})}"
                        th:text="${item.id}">회원id</a></td>
                        <td><a href="item.html" th:href="@{|/basic/items/${item.id}|}" th:text="${item.itemName}">상품명</a></td>
                        <td th:text="${item.price}">10000</td>
                        <td th:text="${item.quantity}">10</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
```

**타임리프 간단히 알아보기**

**타임리프 사용 선언**
```text
<html xmlns:th="http:/www.thymeleaf.org">
```

**속성 변경 - th:href**
```text
th:href="@{/css/bootstrap.rtl.min.css}"
```
- `href="value1"`을 `th:href="value2"`의 값으로 변경한다.
- 타임리프 뷰 템플릿을 거치게 되면 원래 값을 `th:xxx`값으로 변경한다. 만약 값이 없다면 새로 생성한다.
- HTML을 그대로 볼 때는 href 속성이 사용되고, 뷰 템플릿을 거치면 `th:href`의 값이 `href`로 대체되면서 동적으로 변경할 수 있다.
- 대부분의 HTML 속성을 `th:xxx`로 변경할 수 있다.

**타임리프 핵심**
- 핵심은 `th:xxx`가 붙은 부분은 서버사이드에서 렌더링 되고 기존 것을 대체한다. `th:xxx`이 없으면 기존 html의
속성이 그대로 사용된다.
- HTML을 파일로 직접 열었을 때, `th:xxx`가 있어도 웹 브라우저는 `th:` 속성을 알지 못하므로 무시한다. 
- 따라서 HTML을 파일 보기를 유지하면서 템플릿 기능도 할 수 있다.

**URL 링크 표현식 - @{...}**
`th:href="@{/css/bootstrap.min.css}`
- `@{...}` : 타임리프는 URL 링크를 사용하는 경우 `@{...}`를 사용한다. 이것을 URL 링크 표현식이라 한다.
- URL 링크 표현식을 사용하면 서블릿 컨텍스트를 자동으로 포함한다.

**상품 등록 폼으로 이동, 속성 변경 - th:onclick**
**속성 변경 - th:onclick**
- `onclick="location.href='addForm.html'"`
- `th:onclick="|location.href='@{/basic/items/add}'|"`
여기에는 리터럴 대체 문법이 사용되었다. 자세히 알아보자.

**리터럴 대체 - |...|**
`|...|` 
- 타임리프에서 문자와 표현식 등은 분리되어 있기 때문에 더해서 사용해야 한다.
-> `<span th:text="'Welcome to our application, ' + ${user.name}' + '!'">`
- 다음과 같이 리터럴 대체 문법을 사용하면, 더하기 없이 편리하게 사용할 수 있다.
-> `<span th:tex="|Welcome to our application, ${user.name}!|">`

**반복 출력 - th:each**
- `<tr th:each="item : ${items}">`
- 반복은 `th:each`를 사용한다. 이렇게 하면 모델에 포함된 items 컬렉션 데이터가 item 변수에 하나씩 포함되고,
반복문 안에서 item 변수를 사용할 수 있다.
- 컬렉션의 수 만큼 `<tr>..</tr>`이 하위 태그를 포함해서 생성된다. 

**변수 표현식 - ${...}**
- `<td th:text="${item.price}">10000</td>`
- 모델에 포함된 값이나, 타임리프 변수로 선언한 값을 조회할 수 있다.
- 프로퍼티 접근법을 사용한다. (item.getPrice())

**내용 변경 - th:text**
- `<td th:text="${item.price}>10000</td>`
- 내용의 값을 `th:text`의 값으로 변경한다.
- 여기서는 10000을 `${item.price}`의 값으로 변경한다.

**URL 링크 표현식2 - @{...}**
- `th:href="@{/basic/items/{itemId}(itemId=${item.id})}"`
- 상품 ID를 선택하는 링크를 확인해보자.
- URL 링크 표현식을 사용하면 경로를 템플릿처럼 편리하게 사용할 수 있다.
- 경로 변수 `({itemId})`뿐만 아니라 쿼리 파라미터도 생성한다.
- ex) `th:href="@{/basic/items/{itemId}(itemId=${item.id}, query='test')}"`

**URL 링크 간단히**
- `th:href="@{|/basic/items/${item.id}|}"`
- 리터럴 대체 문법을 활용해서 간단히 사용할 수도 있다. 
