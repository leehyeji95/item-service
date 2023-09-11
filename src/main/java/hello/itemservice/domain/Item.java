package hello.itemservice.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//@Data     // 위험하기 때문에 사용 권장하지 않음
// DTO : 데이터 전달(왔다갔다) 에서 사용
@Getter @Setter
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
