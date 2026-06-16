package hello.itemservice.domain.item;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data // 핵심 도메인 모델에서는 쓰기 좀 그렇다. (해시코드, Equals, 매개변수를 필요로 하는 생성자를 한번에 만들어주기 때문에 주의해서쓰자.)
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
