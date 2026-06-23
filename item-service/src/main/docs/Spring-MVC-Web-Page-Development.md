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

**참고**
타임리프는 순수 HTML 파일을 웹 브라우저에서 열어도 내용을 확인할 수 있고, 서버를 통해 뷰 템플릿을 거치면
동적으로 변경된 결과를 확인할 수 있다. JSP를 생각해보면, JSP 파일은 웹 브라우저에서 그냥 열면 JSP 소스코드와
HTML이 뒤죽박죽 되어서 정상적인 확인이 불가능하다. 오직 서버를 통해서 JSP를 열어야 한다.
이렇게 순수 HTML을 그대로 유지하면서 뷰 템플릿도 사용할 수 있는 타임리프의 특징을 네츄럴 템플릿이라 한다. 

**상품 상세**
상품 상세 컨트롤러와 뷰를 개발하자.

**BasicItemController에 추가**

```java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@GetMapping("/{itemId}")
public String item(@PathVariable("itemId") Long itemId, Model model) {
  Item item = itemRepository.findById(itemId);
  model.addAttribute("item", item);
  return "basic/item";
}
```

`PathVariable`로 넘어온 상품ID로 상품을 조회하고, 모델에 담아둔다. 그리고 뷰 템플릿을 호출한다.

**상품 상세 뷰**
정적 HTML을 뷰 템플릿(templates) 영역으로 복사하고 다음과 같이 수정하자.

`resources/templates/basic/item.html`
```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <link href="../css/bootstrap.rtl.min.css"
          th:href="@{/css/bootstrap.rtl.min.css}" rel="stylesheet">
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
            <input type="text" id="itemId" name="itemId" class="form-control" value="1"
                   th:value="${item.id}" readonly>
        </div>
        
        <div>
            <label for="price">가격</label>
            <input type="text" id="price" name="price" class="form-control" value="10000"
                   th:value="${item.price}" readonly>
        </div>
        
        <div>
            <label for="quantity">수량</label>
            <input type="text" id="quantity" name="quantity" class="form-control"
                   value="10" th:value="${item.quantity}" readonly> 
        </div>
        
        <hr class="my-4">
        
        <div class="row">
            <div class="col">
                <button class="w-100 btn btn-primary btn-lg"
                        onclick="location.href='editForm.html'"
                        th:onclick="|location.href='@{basic/items/{itemId}/edit(itemId=${item.id})}'|"
                        type="button">
                    상품 수정
                </button>
            </div>
            
            <div class="col">
                <button class="w-100 btn btn-secondary btn-lg"
                        onclick="location.href='items.html'"
                        th:onclick="|location.href='@{/basic/items}'|"
                        type="button">
                    목록으로 
                </button>
            </div>
        </div>
    </div>
</body>
</html>
```

**속성 변경 - th:value**
`th:value="${item.id}`
- 모델에 있는 item 정보를 획득하고 프로퍼티 접근법으로 출력한다. `(item.getId())`
- `value` 속성을 `th:value` 속성으로 변경한다.

**상품수정 링크**
`th:onclick="|location.href='@{/basic/items/{itemId}/edit(itemId=${item.id})}'|`

**목록으로 링크**
- `th:onclick="|location.href='@{/basic/items}'|"`

**상품 등록 폼**
BasicItemController에 추가

```java
import org.springframework.web.bind.annotation.GetMapping;

@GetMapping
public String addForm() {
  return "basic/addForm"; 
}
```

상품 등록 폼은 단순히 뷰 템플릿만 호출한다.

`/resources/templates/basic/addForm.html`
```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <link href="../css/bootstrap.rtl.min.css"
          th:href="@{/css/bootstrap.rtl.min.css}" rel="stylesheet">
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
        
        <form action="item.html" th:action method="post">
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
                            onclick="location.href='items.html'"
                            th:onclick="|location.href='@{/basic/items}'|"
                            type="button">취소</button>
                </div>
            </div>
        </form>
    </div>

</body>
</html>
```

**속성 변경 - th:action**
- `th:action`
- HTML form에서 `action`에 값이 없으면 현재 URL에 데이터를 전송한다.
- 상품 등록 폼의 URL과 실제 상품 등록을 처리하는 URL을 똑같이 맞추고 HTTP 메서드로 두 기능을 구분한다.
-> 상품 등록 폼 : GET `/basic/items/add`
-> 상품 등록 처리 : POST `/basic/items/add`
- 이렇게 하면 하나의 URL로 등록 폼과, 등록 처리를 깔끔하게 처리할 수 있다. 

**취소**
- 취소시 상품 목록으로 이동한다.
- `th:onclick="|location.href='@{/basic/items}'|"`

## 4. 상품 등록 처리 - @ModelAttribute
이제 상품 등록 폼에서 전달된 데이터로 실제 상품을 등록 처리해보자.
상품 등록 폼은 다음 방식으로 서버에 데이터를 전달한다.
- `POST - HTML Form`
-> `content-type: application/x-www-form-urlencoded`
-> 메시지 바디에 쿼리 파라미터 형식으로 전달 `itemName=itemA&price=10000&quantity=10`
-> ex) 회원 가입, 상품 주문, HTML Form 사용 

요청 파라미터 형식을 처리해야 하므로 `@RequestParam`을 사용하자. 

**상품 등록 처리 - @RequestParam**

**addItemV1 - BasicItemController에 추가**

```java
import hello.itemservice.domain.item.Item;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@PostMapping("/add")
public String addItemV1(@RequestParam String itemName,
                        @RequestParam int price,
                        @RequestParam Integer quantity,
                        Model model) {

  Item item = new Item();
  item.setItemName(itemName);
  item.setPrice(price);
  item.setQuantity(quantity);
  
  itemRepository.save(item);
  
  model.addAttribute("item", item);
  
  return "basic/item"; 

}
```

- 먼저 `@RequestParam String itemName` : itemName 요청 파라미터 데이터를 해당 변수에 받는다.
- `Item` 객체를 생성하고 `itemRepository`를 통해서 저장한다.
- 저장된 `item`을 모델에 담아서 뷰에 전달한다. 

**상품 등록 처리 - @ModelAttribute**
`@RequestParam`으로 변수를 하나하나 받아서 `Item`을 생성하는 과정은 불편했다.
이번에는 `@ModelAttribute`를 사용해서 한번에 처리해보자.

**addItemV2 - 상품 등록 처리 - ModelAttribute**

```java
/**
 * @ModelAttribute("item") Item item
 * model.addAttribute("item", item); 자동 추가 
 */

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@PostMapping("/add")
public String addItemV2(@ModelAttribute("item") Item item, Model model) {
  itemRepository.save(item);
  //model.addAttribute("item", item); // 자동 추가, 생략 가능
  return "basic/item";
}
```

**@ModelAttribute - 요청 파라미터 처리**
`@ModelAttribute`는 `Item`객체를 생성하고, 요청 파라미터의 값을 프로퍼티 접근법으로 입력해준다.

**@ModelAttribute - Model 추가**
`@ModelAttribute`는 중요한 한가지 기능이 더 있는데, 바로 모델에 `@ModelAttribute`로 지정한 객체를 
자동으로 넣어준다. 지금 코드를 보면 `model.addAttribute("item", item)`가 주석처리 되어 있어도
동작하는 것을 확인할 수 있다. 

모델에 데이터를 담을 때는 이름이 필요하다. 이름은 `@ModelAttribute`에 지정한 name(value) 속성을 사용한다.
만약 다음과 같이 `@ModelAttribute`의 이름을 다르게 지정하면 다른 이름으로 모델에 포함된다.

ex) `@ModelAttribute("hello") Item item` -> 이름을 `hello`로 지정.
`model.addAttribute("hello", item);`-> 모델에 hello 이름으로 저장.

**addItemV3 - 상품 등록 처리 - ModelAttribute 이름 생략**

```java
/**
 * @ModelAttribute name 생략 가능
 * model.addAttribute(item); 자동 추가, 생략 가능
 * 생략시 model에 저장되는 name은 클래스명 첫글자만 소문자로 등록 Item -> item
 */

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@PostMapping("/add")
public String addItemV3(@ModelAttribute Item item) {
  itemRepository.save(item);
  return "basic/item"; 
}
```

`@ModelAttribute`의 이름을 생략할 수 있다.

**주의**
`@ModelAttribute`의 이름을 생략하면 모델에 저장될 때 클래스명을 사용한다. 이때 클래스의 첫글자를 변경해서 
등록한다. 
- ex) `@ModelAttribute` 클래스명 -> 모델에 자동 추가되는 이름
-> `Item` -> `item`
-> `HelloWorld` -> `helloWorld`

**addItemV4 - 상품 등록 처리 - ModelAttribute 전체 생략**

```java
/**
 * @ModelAttribute 자체 생략 가능
 * model.addAttribute(item) 자동 추가
 */

import org.springframework.web.bind.annotation.PostMapping;

@PostMapping("/add")
public String addItemV4(Item item) {
  itemRepository.save(item);
  return "basic/item";
}
```

`@ModelAttribute` 자체도 생략가능하다. 대상 객체는 모델에 자동 등록된다.

**상품 수정**

```java
import hello.itemservice.domain.item.Item;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@GetMapping("/{itemId}/edit")
public String editForm(@PathVariable Long itemId, Model model) {
  Item item = itemRepository.findById(itemId);
  model.addAttribute("item", item);
  return "basic/editForm"; 
}
```

**상품 수정 폼 뷰**

`/resources/templates/basic/editForm.html`

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
    <link href="../css/bootstrap.rtl.min.css"
          th:href="@{/css/bootstrap.rtl.min.css}" rel="stylesheet">
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
        
        <form action="item.html" th:action method="post">
            <div>
                <label for="id">상품 ID</label>
                <input type="text" id="id" name="id" class="form-control"
                       value="1"
                       th:value="${item.id}"
                       readonly>
            </div>
            
            <div>
                <label for="itemName">상품명</label>
                <input type="text" id="itemName" name="itemName" class="form-control"
                       value="상품A" th:value="${item.itemName}">
            </div>
            
            <div>
                <label for="price">가격</label>
                <input type="text" id="price" name="price" class="form-control"
                       th:value="${item.price}">
            </div>
            
            <div>
                <label for="quantity">수량</label>
                <input type="text" id="quantity" name="quantity" class="form-control"
                       th:value="${item.quantity}">
            </div>
            
            <hr class="my-4">
            
            <div class="row">
                <div class="col">
                    <button class="w-100 btn btn-primary btn-lg" type="submit">저장</button>
                </div>
                
                <div class="col">
                    <button class="w-100 btn btn-secondary btn-lg"
                            onclick="location.href='item.html'"
                            th:onclick="|location.href='@{/basic/items/{itemId}(itemId=${item.id})}'|"
                            type="button">취소</button>
                            
                </div>
            </div>
        </form>
    </div>
</body>
</html>
```

**상품 수정 개발**
```java
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@PostMapping("/{itemId}/edit")
public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
  itemRepository.update(itemId, item);
  return "redirect:/basic/items/{itemId}"; 
}
```

상품 수정은 상품 등록과 전체 프로세스가 유사하다.
- GET `/items/{itemId}/edit` : 상품 수정 폼
- POST `/items/{itemId}/edit` : 상품 수정 처리 

**리다이렉트**
상품 수정은 마지막에 뷰 템플릿을 호출하는 대신에 상품 상세 화면으로 이동하도록 리다이렉트를 호출한다.
- 스프링은 `redircet:/...`으로 편리하게 리다이렉트를 지원한다.
- `redirect:/basic/items/{itemId}`
-> 컨트롤러에 매핑된 `@PathVariable`의 값은 `redirect`에도 사용 할 수 있다.
-> `redirect:/basic/items/{itemId}` -> `{itemId}`는 `@PathVariable Long itemId`의 값을 그대로 사용한다.

## 5. PRG (Post/Redirect/Get)
사실 지금까지 진행한 상품 등록 처리 컨트롤러는 심각한 문제가 있다. (addItemV1 ~ addItemV4)
addItemV1 ~ addItemV4 컨트롤러를 보기 전에 상품 등록 절차에 대해서 잠깐 생각해보자.

```java
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

  @GetMapping("/{itemId}")
  public String item(@PathVariable("itemId") long itemId, Model model) {
    Item item = itemRepository.findById(itemId);
    model.addAttribute("item", item);
    return "basic/item";
  }

  @GetMapping("/add")
  public String addForm() {
    return "basic/addForm";
  }

  //@PostMapping("/add")
  public String addItemV1(@RequestParam String itemName,
                     @RequestParam int price,
                     @RequestParam Integer quantity,
                     Model model
  ) {

    Item item = new Item();
    item.setItemName(itemName);
    item.setPrice(price);
    item.setQuantity(quantity);

    itemRepository.save(item);

    model.addAttribute("item", item);

    return "basic/item";
  }

  //@PostMapping("/add")
  public String addItemV2(@ModelAttribute("item") Item item, Model model
  ) {
    itemRepository.save(item);
    //model.addAttribute("item", item);
    //@ModelAttribute는 item 객체를 만들어주고 동시에 addAttribute("item", item)도 해준다.
    return "basic/item";
  }

  //@PostMapping("/add")
  public String addItemV3(@ModelAttribute Item item, Model model
  ) {
    // V2처럼 이름을 따로 지정해주지 않는다면?
    // 클래스명이 Item인데 맨앞 알파벳만 소문자로 바꾼다. Item -> item
    // 즉, model.addAttribute("item", item); 이 되는것이다.
    itemRepository.save(item);
    return "basic/item";
  }

  //@PostMapping("/add")
  public String addItemV4(Item item) {
    // 이 경우는 @ModelAttribute가 생략되어 있는 것으로 판단. Item -> item
    itemRepository.save(item);
    return "basic/item";
  }
  
}
```

먼저 `localhost:8080/basic/items`을 입력하여 이동을 한다. 
이동하면 물품들의 리스트가 나오는데 상품 등록 버튼을 누른다면 Post 방식이 아닌 Get 방식으로 요청을 한다.
그래서 상품 등록 양식이 나온다. 
이후에, 등록을 하면 `addForm.html`에서 같은 링크로 이동한다. (form 태그 참고, 이땐 post 형식을 이용)
이제 addItemV1 ~ addItemV4 중 등록한 메서드로 이동을 하는데 마지막에 하는 작업이 `src/main/resources/templates/basic/item.html`으로 이동한다.
이 상황에서 새로 고침하면 마지막 작업은 코드에서 보는것처럼 `itemRepository.save(item)`을 한 이후에 html 파일 렌더링이 되는 것이므로
계속 물품이 추가되는 상황이 발생한다. (새로 고침을 하면 post 방식의 `basic/items/add가 계속 링크로 고정되어 있기 때문)
이 문제를 방지하고자 PRG 패턴을 사용해보자. 

코드를 추가해보자.
**BasicItemController에 추가**

```java
/**
 * PRG - Post/Redirect/Get
 */

import org.springframework.web.bind.annotation.PostMapping;

@PostMapping("/add")
public String addItemV5(Item item) {
  itemRepository.save(item);
  return "redirect:/basic/items/" + item.getId(); 
}
```

상품 등록 처리 이후에 뷰 템플릿이 아니라 상품 상세 화면으로 리다이렉트 하도록 코드를 작성해보자.
이런 문제 해결 방식을 `PRG Post/Redirect/Get`라 한다.

**주의**
`"redirect:/basic/items/" + item.getId()` redirect에서 `+item.getId()`처럼 URL에 변수를 더해서
사용하는 것은 URL 인코딩이 안되기 때문에 위험하다. `RedirectAttributes`를 사용하자. 

**RedirectAttributes**
상품을 저장하고 상품 상세 화면으로 리다이렉트 한 것 까지는 좋았다.
그런데 고객 입장에서 저장이 잘 된 것인지 안 된 것인지 확신이 들지 않는다.
그래서 저장이 잘 되었으면 상품 상세 화면에 "저장되었습니다." 라는 메시지를 보여달라는 요구사항이 왔다고 가정하자.
간단하게 해결해보자.

```java
/**
 * RedirectAttributes
 */

import hello.itemservice.domain.item.Item;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@PostMapping("/add")
public String addItemV6(Item item, RedirectAttributes redirectAttributes) {
  Item savedItem = itemRepository.save(item);
  redirectAttributes.addAttribute("itemId", savedItem.getId());
  redirectAttributes.addAttribute("status", true);
  return "redirect:/basic/items/{itemId}"; 
}
```

리다이렉트 할 때 간단히 `status=true`를 추가해보자. 
그리고 뷰 템플릿에서 이 값이 있으면, 저장되었습니다. 라는 메시지를 출력해보자.

실행해보면 다음과 같은 리다이렉트 결과가 나온다.
`http://localhost:8080/basic/items/3?status=true`

**RedirectAttributes**
`RedirectAttributes`를 사용하면 URL 인코딩도 해주고, `pathVariable`, 쿼리 파라미터까지 처리해준다. 
- `redirect:/baisc/items/{itemId}`
-> pathVariable 바인딩 : `{itemId}`
-> 나머지는 쿼리 파라미터로 처리 : `?status=true`

**뷰 템플릿 메시지 추가**
`resources/templates/basic/item.html`
```html
<div class="container">
    
    <div class="py-5 text-center">
        <h2>상품 상세</h2>
    </div>
    
    <!-- 추가 -->
    <h2 th:if="${param.status}" th:text="'저장 완료!'"></h2>
    
</div>
```
- `th:if`: 해당 조건이 참이면 실행
- `${param.status}` : 타임리프에서 쿼리 파라미터를 편리하게 조회하는 기능
-> 원래는 컨트롤러에서 모델에 직접 담고 값을 꺼내야한다. 그런데 쿼리 파라미터는 자주 사용해서 타임리프에서
직접 지원한다. 



