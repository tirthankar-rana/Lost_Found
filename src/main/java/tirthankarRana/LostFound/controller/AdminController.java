package tirthankarRana.LostFound.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tirthankarRana.LostFound.model.Item;
import tirthankarRana.LostFound.model.ItemStatus;
import tirthankarRana.LostFound.model.ItemType;
import tirthankarRana.LostFound.model.User;
import tirthankarRana.LostFound.repository.ItemRepository;
import tirthankarRana.LostFound.repository.UserRepository;
import tirthankarRana.LostFound.util.PasswordUtil;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public AdminController(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @GetMapping("/summary")
    public ResponseEntity<?> summary() {
        return ResponseEntity.ok(
            Map.of(
                "totalUsers", userRepository.count(),
                "totalItems", itemRepository.count(),
                "openItems", itemRepository.countByStatus(ItemStatus.OPEN),
                "resolvedItems", itemRepository.countByStatus(ItemStatus.RESOLVED),
                "lostItems", itemRepository.countByType(ItemType.LOST),
                "foundItems", itemRepository.countByType(ItemType.FOUND)
            )
        );
    }

    @PostMapping("/generate-duplicate-items")
    public ResponseEntity<?> generateDuplicateItems(@RequestParam(defaultValue = "24") int count) {
        if (count < 1 || count > 200) {
            return ResponseEntity.badRequest().body(Map.of("error", "count must be between 1 and 200"));
        }

        User demoUser = userRepository.findByEmail("demo.admin@lostfound.local").orElseGet(() -> {
            User user = new User();
            user.setName("Demo Admin");
            user.setEmail("demo.admin@lostfound.local");
            user.setPhone("9000000000");
            user.setPassword(PasswordUtil.hash("admin123"));
            return userRepository.save(user);
        });

        List<String> titles = List.of("Black Wallet", "Blue Backpack", "iPhone 13", "College ID Card");
        List<String> locations = List.of("Kolkata", "Delhi", "Noida", "Mumbai", "Pune");
        List<String> categories = List.of("Accessories", "Electronics", "Documents");
        List<String> images = List.of(
            "https://images.unsplash.com/photo-1627123424574-724758594e93?auto=format&fit=crop&w=1200&q=80",
            "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=1200&q=80",
            "https://images.unsplash.com/photo-1585386959984-a41552231658?auto=format&fit=crop&w=1200&q=80",
            "https://images.unsplash.com/photo-1517677208171-0bc6725a3e60?auto=format&fit=crop&w=1200&q=80"
        );

        for (int i = 0; i < count; i++) {
            Item item = new Item();
            item.setUser(demoUser);
            item.setType(i % 5 == 0 ? ItemType.FOUND : ItemType.LOST);
            item.setStatus(ItemStatus.OPEN);
            item.setTitle(titles.get(i % titles.size())); // intentionally repeated
            item.setDescription("Demo duplicate entry #" + (i + 1) + " for UI testing and pagination.");
            item.setCategory(categories.get(i % categories.size()));
            item.setLocation(locations.get(i % locations.size()));
            item.setImageUrl(images.get(i % images.size()));
            item.setIncidentDate(LocalDate.now().minusDays(i % 12));
            itemRepository.save(item);
        }

        return ResponseEntity.ok(Map.of("message", "Duplicate demo entries generated", "count", count));
    }
}
