package com.example.couplebackend.controller;

import com.example.couplebackend.common.ApiResponse;
import com.example.couplebackend.entity.Activity;
import com.example.couplebackend.entity.DressItem;
import com.example.couplebackend.service.CoupleService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {
    private final CoupleService service;

    public AdminApiController(CoupleService service) {
        this.service = service;
    }

    @PostMapping("/auth/login")
    public ApiResponse<Map<String, Object>> login() {
        return ApiResponse.ok(Map.of("token", "admin-token", "nickname", "运营管理员"));
    }

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        return ApiResponse.ok(service.adminDashboard());
    }

    @GetMapping("/users")
    public ApiResponse<Object> users() {
        return ApiResponse.ok(service.adminDashboard().get("users"));
    }

    @GetMapping("/spaces")
    public ApiResponse<Object> spaces() {
        return ApiResponse.ok(service.adminDashboard().get("spaces"));
    }

    @GetMapping("/service-orders")
    public ApiResponse<Object> orders() {
        return ApiResponse.ok(service.adminDashboard().get("orders"));
    }

    @GetMapping("/todos")
    public ApiResponse<Object> todos() {
        return ApiResponse.ok(service.adminDashboard().get("todos"));
    }

    @GetMapping("/wishes")
    public ApiResponse<Object> wishes() {
        return ApiResponse.ok(service.adminDashboard().get("wishes"));
    }

    @GetMapping("/dress/items")
    public ApiResponse<Object> dressItems() {
        return ApiResponse.ok(service.adminDashboard().get("dressItems"));
    }

    @PostMapping("/dress/items")
    public ApiResponse<DressItem> createDress(@RequestBody(required = false) Map<String, Object> body) {
        return ApiResponse.ok(service.saveDress(body == null ? Map.of() : body));
    }

    @PutMapping("/dress/items/{id}")
    public ApiResponse<DressItem> updateDress(@PathVariable Long id,
                                              @RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> payload = new java.util.LinkedHashMap<>(body == null ? Map.of() : body);
        payload.put("id", id);
        return ApiResponse.ok(service.saveDress(payload));
    }

    @GetMapping("/activities")
    public ApiResponse<Object> activities() {
        return ApiResponse.ok(service.adminDashboard().get("activities"));
    }

    @PostMapping("/activities")
    public ApiResponse<Activity> createActivity(@RequestBody(required = false) Map<String, Object> body) {
        return ApiResponse.ok(service.saveActivity(body == null ? Map.of() : body));
    }

    @PutMapping("/activities/{id}")
    public ApiResponse<Activity> updateActivity(@PathVariable Long id,
                                                @RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> payload = new java.util.LinkedHashMap<>(body == null ? Map.of() : body);
        payload.put("id", id);
        return ApiResponse.ok(service.saveActivity(payload));
    }

    @GetMapping("/payment-orders")
    public ApiResponse<Object> payments() {
        return ApiResponse.ok(service.adminDashboard().get("payments"));
    }

    @GetMapping("/audit/records")
    public ApiResponse<Object> audits() {
        return ApiResponse.ok(service.adminDashboard().get("audits"));
    }
}
