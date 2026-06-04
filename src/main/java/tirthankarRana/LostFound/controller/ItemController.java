package tirthankarRana.LostFound.controller;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tirthankarRana.LostFound.dto.CreateItemRequest;
import tirthankarRana.LostFound.model.Item;
import tirthankarRana.LostFound.model.ItemStatus;
import tirthankarRana.LostFound.model.ItemType;
import tirthankarRana.LostFound.model.User;
import tirthankarRana.LostFound.repository.ItemRepository;
import tirthankarRana.LostFound.repository.UserRepository;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemController(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody CreateItemRequest request) {
        if (request.getUserId() == null || request.getType() == null || request.getTitle() == null
            || request.getDescription() == null || request.getCategory() == null
            || request.getLocation() == null || request.getIncidentDate() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "All fields are required"));
        }

        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }

        Item item = new Item();
        item.setUser(userOpt.get());
        item.setType(request.getType());
        item.setTitle(request.getTitle().trim());
        item.setDescription(request.getDescription().trim());
        item.setCategory(request.getCategory().trim());
        item.setLocation(request.getLocation().trim());
        if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            item.setImageUrl(request.getImageUrl().trim());
        }
        item.setIncidentDate(request.getIncidentDate());
        item.setStatus(ItemStatus.OPEN);

        Item saved = itemRepository.save(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(toItemResponse(saved));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getOpenItems(
        @RequestParam(required = false) ItemType type,
        @RequestParam(required = false) String location
    ) {
        List<Item> items;

        if (type == null) {
            items = itemRepository.findByStatusOrderByCreatedAtDesc(ItemStatus.OPEN);
        } else if (location != null && !location.isBlank()) {
            items = itemRepository.findByTypeAndStatusAndLocationContainingIgnoreCaseOrderByCreatedAtDesc(
                type, ItemStatus.OPEN, location.trim()
            );
        } else {
            items = itemRepository.findByTypeAndStatusOrderByCreatedAtDesc(type, ItemStatus.OPEN);
        }
        return ResponseEntity.ok(items.stream().map(this::toItemResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(@PathVariable Long id) {
        return itemRepository.findById(id)
            .<ResponseEntity<?>>map(item -> ResponseEntity.ok(toItemResponse(item)))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Item not found")));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getItemsByUser(@PathVariable Long userId) {
        List<Item> items = itemRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return ResponseEntity.ok(items.stream().map(this::toItemResponse).collect(Collectors.toList()));
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<?> markResolved(@PathVariable Long id, @RequestParam Long userId) {
        Optional<Item> itemOpt = itemRepository.findById(id);
        if (itemOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Item not found"));
        }

        Item item = itemOpt.get();
        if (!item.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Only owner can resolve item"));
        }

        item.setStatus(ItemStatus.RESOLVED);
        Item saved = itemRepository.save(item);
        return ResponseEntity.ok(toItemResponse(saved));
    }

    private Map<String, Object> toItemResponse(Item item) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", item.getId());
        response.put("type", item.getType());
        response.put("title", item.getTitle());
        response.put("description", item.getDescription());
        response.put("category", item.getCategory());
        response.put("location", item.getLocation());
        response.put("imageUrl", item.getImageUrl());
        response.put("incidentDate", item.getIncidentDate());
        response.put("status", item.getStatus());
        response.put("createdAt", item.getCreatedAt());
        response.put("userId", item.getUser().getId());
        response.put("userName", item.getUser().getName());
        response.put("userPhone", item.getUser().getPhone());
        return response;
    }
}
