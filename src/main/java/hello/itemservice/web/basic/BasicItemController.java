package hello.itemservice.web.basic;

import hello.itemservice.domain.Item;
import hello.itemservice.domain.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import java.util.List;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor // 생성자 주입 생략가능
public class BasicItemController {

    private final ItemRepository itemRepository;

//    @Autowired  // 생성자 주입 (생성자 1개이면 Autowired 생략 가능
//    public BasicItemController(ItemRepository itemRepository) {
//        this.itemRepository = itemRepository;
//    }

    /**
     * 상품 목록
     * @param model
     * @return
     */
    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items";    // View 이름
    }

    /**
     * 상품 상세
     * @param itemId
     * @param model
     * @return
     */
    @GetMapping("/{itemId}")
    public String item(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item";
    }

    /**
     * 상품 등록 Form 보여주기
     * @return
     */
    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm";
    }

    /**
     * 상품 등록 처리
     * 등록 후 상품 상세 페이지로 이동
     * @return
     *
     * 참고 ! HTML Form 은 PUT, PATCH 지원하지 않고, GET, POST 만 사용 가능.
     * PUT, PATCH 는 HTTP API 에서만 사용 가능
     */
//    @PostMapping("/add")
    public String addItemV1(@RequestParam String itemName, @RequestParam int price, @RequestParam Integer quantity, Model model) {
        Item item = new Item(itemName, price, quantity);
        itemRepository.save(item);
        model.addAttribute("item", item);
        return "basic/item";
    }

//    @PostMapping("/add")
    public String addItemV2(@ModelAttribute("item") Item item, Model model) {
        // @ModelAttribute 가 Item 만들어준다.
        itemRepository.save(item);
        // @ModelAttribute 애너테이션이 "item" key 로 데이터 자동으로 넣어줌 (생략도 가능-> addAttribute 해줘야함)
        // @ModelAttribute("item2") == model.addAttribute("item2", item);
//        model.addAttribute("item", item); // 자동 추가, 생략가능

        return "basic/item";
    }

//    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item) {
//        @ModelAttribute name 생략하면 Item -> item 으로 바꿔서 넣어준다
        itemRepository.save(item);
        return "basic/item";
    }

//    @PostMapping("/add")
    public String addItemV4(Item item) {
        itemRepository.save(item);
        return "basic/item";
        // 여기서 문제점 !! 상품 등록 후 결과로 상품 상세 페이지 보이지만 url은 /add 와 POST 그대로
        // 이때 새로고침(마지막 요청 다시 보냄) -> 마지막 요청이었던 POST /add 계속 요청된다. (상품 중복 등록)
        // 따라서, Redirect 로 상품 상세로 보내서 (items/{id} url 변경)
        // 새로고침 하면 GET /items/{id} 로 요청보낸다. PRG (Post, Redirect, GET)
        // 즉, 뷰 템플릿 호출 하지 않고, 상품 상세화면으로 리다이렉트 호출해주자! => addItemV5
    }

    // PRG 적용
//    @PostMapping("/add")
    public String addItemV5(Item item) {
        itemRepository.save(item);
        return "redirect:/basic/items/" + item.getId(); // url 인코딩 문제로 RedirectAttribute 사용하기
    }

    // 상품 등록 되면 "저장되었음을 알려주자"
    @PostMapping("/add")
    public String addItemV6(Item item, RedirectAttributes redirectAttributes) {
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());   // {itemId} 로 사용
        redirectAttributes.addAttribute("status", true);    // QueryParameter 로 들어간다. ?status=true
        return "redirect:/basic/items/{itemId}";
    }

    /**
     * 상품 수정 Form 보여주기
     * @param itemId
     * @return
     */
    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/editForm";
    }

    /**
     * 상품 수정 처리
     * @param itemId
     * @param item
     * @return
     */
    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
//        return "basic/item"; // basic/items/3/edit 경로 그대로
        // 경로 변경 -> redirect 사용하기 (@PathVariable 에 있는 값 넣어준다)
        return "redirect:/basic/items/{itemId}";
    }

    /**
     * 테스트 용 데이터 추가
     * 클래스 생성 후 실행
     */
    @PostConstruct
    public void init() {
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }

}
