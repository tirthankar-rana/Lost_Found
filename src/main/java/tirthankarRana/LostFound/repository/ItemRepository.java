package tirthankarRana.LostFound.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tirthankarRana.LostFound.model.Item;
import tirthankarRana.LostFound.model.ItemStatus;
import tirthankarRana.LostFound.model.ItemType;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByStatusOrderByCreatedAtDesc(ItemStatus status);
    List<Item> findByTypeAndStatusOrderByCreatedAtDesc(ItemType type, ItemStatus status);
    List<Item> findByUserIdOrderByCreatedAtDesc(Long userId);
    long countByStatus(ItemStatus status);
    long countByType(ItemType type);
    List<Item> findByTypeAndStatusAndLocationContainingIgnoreCaseOrderByCreatedAtDesc(
        ItemType type, ItemStatus status, String location
    );
}
