package hello.itemservice.domain;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository // Spring 사용 - Component 의 대상이 된다.
public class ItemRepository {
    private static final Map<Long, Item> store = new HashMap<>(); //static, 멀티쓰레드 환경에서 ConcurrentHashMap
    private static long sequence = 0L; // Long의 경우에도 멀티쓰레드 환경에서 AtomicLong 사용

    // 아이템 저장
    public Item save(Item item) {
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id) {
        return store.get(id);
    }

    public List<Item> findAll() {
        // 한번 ArrayList 로 감싸서 반환 -> store 에 영향을 주지 않아서 안전
        return new ArrayList<>(store.values());
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
