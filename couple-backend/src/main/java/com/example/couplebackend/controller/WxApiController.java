package com.example.couplebackend.controller;

import com.example.couplebackend.common.ApiResponse;
import com.example.couplebackend.common.BusinessException;
import com.example.couplebackend.entity.Anniversary;
import com.example.couplebackend.entity.CheckinRecord;
import com.example.couplebackend.entity.DressItem;
import com.example.couplebackend.entity.Message;
import com.example.couplebackend.entity.MoodStatus;
import com.example.couplebackend.entity.PaymentOrder;
import com.example.couplebackend.entity.Product;
import com.example.couplebackend.entity.ServiceCategory;
import com.example.couplebackend.entity.ServiceItem;
import com.example.couplebackend.entity.ServiceOrder;
import com.example.couplebackend.entity.ServiceTemplate;
import com.example.couplebackend.entity.Space;
import com.example.couplebackend.entity.TodoItem;
import com.example.couplebackend.entity.User;
import com.example.couplebackend.entity.WishItem;
import com.example.couplebackend.service.CoupleService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/wx")
public class WxApiController {
    private final CoupleService service;

    public WxApiController(CoupleService service) {
        this.service = service;
    }

    @PostMapping("/auth/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody(required = false) Map<String, Object> body) {
        return ApiResponse.ok(service.login(body == null ? Map.of() : body));
    }

    @GetMapping("/bootstrap")
    public ApiResponse<Map<String, Object>> bootstrap(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return ApiResponse.ok(service.bootstrap(userId(userId)));
    }

    @GetMapping("/spaces/current")
    public ApiResponse<Space> currentSpace(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return ApiResponse.ok(service.currentSpace(userId(userId)));
    }

    @PutMapping("/spaces/current")
    public ApiResponse<Space> updateCurrentSpace(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                 @RequestBody(required = false) Map<String, Object> body) {
        return ApiResponse.ok(service.updateCurrentSpace(userId(userId), body == null ? Map.of() : body));
    }

    @PostMapping("/spaces/current/manager")
    public ApiResponse<Space> transferCurrentSpaceManager(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                          @RequestBody(required = false) Map<String, Object> body) {
        return ApiResponse.ok(service.transferCurrentSpaceManager(userId(userId), body == null ? Map.of() : body));
    }

    @PostMapping("/spaces")
    public ApiResponse<Space> createSpace(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                          @RequestBody(required = false) Map<String, Object> body) {
        return ApiResponse.ok(service.createSpace(userId(userId), body == null ? Map.of() : body));
    }

    @PostMapping("/spaces/join")
    public ApiResponse<Space> joinSpace(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                        @RequestBody(required = false) Map<String, Object> body) {
        return ApiResponse.ok(service.joinSpace(userId(userId), body == null ? Map.of() : body));
    }

    @PostMapping("/spaces/invites")
    public ApiResponse<Map<String, Object>> createInvite(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return ApiResponse.ok(service.createSpaceInvite(userId(userId)));
    }

    @PostMapping("/spaces/invites/accept")
    public ApiResponse<Map<String, Object>> acceptInvite(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                         @RequestBody(required = false) Map<String, Object> body) {
        return ApiResponse.ok(service.acceptSpaceInvite(userId(userId), body == null ? Map.of() : body));
    }

    @PutMapping("/users/profile")
    public ApiResponse<User> updateProfile(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                           @RequestBody(required = false) Map<String, Object> body) {
        return ApiResponse.ok(service.updateProfile(userId(userId), body == null ? Map.of() : body));
    }

    @PostMapping("/files/avatar")
    public ApiResponse<Map<String, Object>> uploadAvatar(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                         @RequestParam("file") MultipartFile file) {
        return ApiResponse.ok(service.uploadAvatar(userId(userId), file));
    }

    @PostMapping("/files/service-image")
    public ApiResponse<Map<String, Object>> uploadServiceImage(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                               @RequestParam("file") MultipartFile file) {
        return ApiResponse.ok(service.uploadServiceImage(userId(userId), file));
    }

    @GetMapping("/spaces/{spaceId}/menu")
    public ApiResponse<List<Map<String, Object>>> menu(@PathVariable Long spaceId) {
        return ApiResponse.ok(service.menu(spaceId));
    }

    @GetMapping("/service-categories")
    public ApiResponse<List<ServiceCategory>> categories(@RequestParam(defaultValue = "1") Long spaceId) {
        return ApiResponse.ok(service.categories(spaceId));
    }

    @PostMapping("/service-categories")
    public ApiResponse<ServiceCategory> createCategory(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                       @RequestBody(required = false) Map<String, Object> body) {
        return ApiResponse.ok(service.saveCategory(userId(userId), body == null ? Map.of() : body));
    }

    @PutMapping("/service-categories/{id}")
    public ApiResponse<ServiceCategory> updateCategory(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                       @PathVariable Long id,
                                                       @RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> payload = new java.util.LinkedHashMap<>(body == null ? Map.of() : body);
        payload.put("id", id);
        return ApiResponse.ok(service.saveCategory(userId(userId), payload));
    }

    @DeleteMapping("/service-categories/{id}")
    public ApiResponse<Void> removeCategory(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                            @PathVariable Long id) {
        service.removeCategory(userId(userId), id);
        return ApiResponse.ok();
    }

    @GetMapping("/service-items")
    public ApiResponse<List<ServiceItem>> serviceItems(@RequestParam(defaultValue = "1") Long spaceId) {
        return ApiResponse.ok(service.serviceItems(spaceId));
    }

    @PostMapping("/service-items")
    public ApiResponse<ServiceItem> createServiceItem(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                      @RequestBody(required = false) Map<String, Object> body) {
        return ApiResponse.ok(service.saveServiceItem(userId(userId), body == null ? Map.of() : body));
    }

    @PutMapping("/service-items/{id}")
    public ApiResponse<ServiceItem> updateServiceItem(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                      @PathVariable Long id,
                                                      @RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> payload = new java.util.LinkedHashMap<>(body == null ? Map.of() : body);
        payload.put("id", id);
        return ApiResponse.ok(service.saveServiceItem(userId(userId), payload));
    }

    @DeleteMapping("/service-items/{id}")
    public ApiResponse<Void> removeServiceItem(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                               @PathVariable Long id) {
        service.removeServiceItem(userId(userId), id);
        return ApiResponse.ok();
    }

    @GetMapping("/service-orders")
    public ApiResponse<List<Map<String, Object>>> orders(@RequestParam(defaultValue = "1") Long spaceId,
                                                         @RequestParam(required = false) String status) {
        return ApiResponse.ok(service.orders(spaceId, status));
    }

    @PostMapping("/service-orders")
    public ApiResponse<ServiceOrder> createOrder(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                 @RequestBody(required = false) Map<String, Object> body) {
        return ApiResponse.ok(service.createOrder(userId(userId), body == null ? Map.of() : body));
    }

    @PostMapping("/service-orders/{id}/{action}")
    public ApiResponse<ServiceOrder> changeOrder(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                 @PathVariable Long id,
                                                 @PathVariable String action) {
        return ApiResponse.ok(service.changeOrderStatus(userId(userId), id, action));
    }

    @GetMapping("/todos")
    public ApiResponse<List<TodoItem>> todos(@RequestParam(defaultValue = "1") Long spaceId,
                                             @RequestParam(required = false) String status) {
        return ApiResponse.ok(service.todos(spaceId, status));
    }

    @PostMapping("/todos")
    public ApiResponse<TodoItem> createTodo(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                            @RequestBody(required = false) Map<String, Object> body) {
        return ApiResponse.ok(service.createTodo(userId(userId), body == null ? Map.of() : body));
    }

    @PostMapping("/todos/{id}/{status}")
    public ApiResponse<TodoItem> changeTodo(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                            @PathVariable Long id,
                                            @PathVariable String status) {
        return ApiResponse.ok(service.changeTodoStatus(userId(userId), id, status));
    }

    @PostMapping("/moods")
    public ApiResponse<MoodStatus> updateMood(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                              @RequestBody(required = false) Map<String, Object> body) {
        return ApiResponse.ok(service.updateMood(userId(userId), body == null ? Map.of() : body));
    }

    @PostMapping("/signals")
    public ApiResponse<Message> sendSignal(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                           @RequestBody(required = false) Map<String, Object> body) {
        return ApiResponse.ok(service.sendSignal(userId(userId), body == null ? Map.of() : body));
    }

    @GetMapping("/dress/items")
    public ApiResponse<List<DressItem>> dressItems() {
        return ApiResponse.ok(service.dressItems());
    }

    @PostMapping("/dress/items/{id}/apply")
    public ApiResponse<DressItem> applyDress(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                             @PathVariable Long id) {
        return ApiResponse.ok(service.applyDress(userId(userId), id));
    }

    @PostMapping("/checkin")
    public ApiResponse<CheckinRecord> checkin(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return ApiResponse.ok(service.checkin(userId(userId)));
    }

    @GetMapping("/wishes")
    public ApiResponse<List<WishItem>> wishes(@RequestParam(defaultValue = "1") Long spaceId) {
        return ApiResponse.ok(service.wishes(spaceId));
    }

    @PostMapping("/wishes")
    public ApiResponse<WishItem> createWish(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                            @RequestBody(required = false) Map<String, Object> body) {
        return ApiResponse.ok(service.createWish(userId(userId), body == null ? Map.of() : body));
    }

    @PostMapping("/wishes/{id}/claim")
    public ApiResponse<WishItem> claimWish(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                           @PathVariable Long id) {
        return ApiResponse.ok(service.claimWish(userId(userId), id));
    }

    @GetMapping("/anniversaries")
    public ApiResponse<List<Anniversary>> anniversaries(@RequestParam(defaultValue = "1") Long spaceId) {
        return ApiResponse.ok(service.anniversaries(spaceId));
    }

    @PostMapping("/anniversaries")
    public ApiResponse<Anniversary> createAnniversary(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                      @RequestBody(required = false) Map<String, Object> body) {
        return ApiResponse.ok(service.createAnniversary(userId(userId), body == null ? Map.of() : body));
    }

    @PutMapping("/anniversaries/{id}")
    public ApiResponse<Anniversary> updateAnniversary(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                      @PathVariable Long id,
                                                      @RequestBody(required = false) Map<String, Object> body) {
        return ApiResponse.ok(service.updateAnniversary(userId(userId), id, body == null ? Map.of() : body));
    }

    @DeleteMapping("/anniversaries/{id}")
    public ApiResponse<Void> removeAnniversary(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                               @PathVariable Long id) {
        service.removeAnniversary(userId(userId), id);
        return ApiResponse.ok();
    }

    @GetMapping("/messages")
    public ApiResponse<List<Message>> messages(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                               @RequestParam(defaultValue = "1") Long spaceId) {
        return ApiResponse.ok(service.messages(spaceId, userId(userId)));
    }

    @PostMapping("/messages/{id}/read")
    public ApiResponse<Message> readMessage(@PathVariable Long id) {
        return ApiResponse.ok(service.readMessage(id));
    }

    @GetMapping("/templates")
    public ApiResponse<List<ServiceTemplate>> templates() {
        return ApiResponse.ok(service.templates());
    }

    @PostMapping("/templates/{id}/import")
    public ApiResponse<Map<String, Object>> importTemplate(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                          @PathVariable Long id) {
        return ApiResponse.ok(service.importTemplate(userId(userId), id));
    }

    @GetMapping("/products")
    public ApiResponse<List<Product>> products() {
        return ApiResponse.ok(service.products());
    }

    @PostMapping("/payments")
    public ApiResponse<PaymentOrder> createPayment(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                   @RequestBody(required = false) Map<String, Object> body) {
        return ApiResponse.ok(service.createPayment(userId(userId), body == null ? Map.of() : body));
    }

    private Long userId(Long userId) {
        if (userId == null) {
            throw new BusinessException(401, "请先微信登录");
        }
        return userId;
    }
}
