package hello.itemservice.web.item.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor // 매개변수를 필요로 하는 생성자 자동으로 만들어줌.
public class BasicItemController {

  private final ItemRepository itemRepository;

  /*
  생성자가 한개면 @Autowired는 붙음.
  @Autowired
  public BasicItemController(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }
  */

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

  //@PostMapping("/add")
  public String addItemV5(Item item) {
    itemRepository.save(item);
    return "redirect:/basic/items/" + item.getId();
  }

  @PostMapping("/add")
  public String addItemV6(Item item, RedirectAttributes redirectAttributes) {
    Item savedItem = itemRepository.save(item);
    redirectAttributes.addAttribute("itemId", savedItem.getId());
    redirectAttributes.addAttribute("status", true);
    return "redirect:/basic/items/{itemId}";
    // 남는 것은 쿼리 파라미터 형식으로 들어간다.
  }

  @GetMapping("/{itemId}/edit")
  public String editForm(@PathVariable Long itemId, Model model) {
    Item item = itemRepository.findById(itemId);
    model.addAttribute("item", item);
    return "basic/editForm";
  }

  @PostMapping("/{itemId}/edit")
  public String edit(@PathVariable Long itemId, Item item) {
    itemRepository.update(itemId, item);
    return "redirect:/basic/items/{itemId}";
  }

  /**
   * 테스트용 데이터 추가
   */
  @PostConstruct
  public void init() {
    itemRepository.save(new Item("itemA", 10000, 10));
    itemRepository.save(new Item("itemB", 20000, 20));
  }

}
