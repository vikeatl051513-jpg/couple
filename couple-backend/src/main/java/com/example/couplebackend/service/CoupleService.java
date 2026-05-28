package com.example.couplebackend.service;

import com.example.couplebackend.common.BusinessException;
import com.example.couplebackend.common.IdGenerator;
import com.example.couplebackend.common.QueryCacheInvalidator;
import com.example.couplebackend.config.CacheNames;
import com.example.couplebackend.entity.Activity;
import com.example.couplebackend.entity.Anniversary;
import com.example.couplebackend.entity.AuditRecord;
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
import com.example.couplebackend.entity.SpaceDress;
import com.example.couplebackend.entity.SpaceInvite;
import com.example.couplebackend.entity.SpaceMember;
import com.example.couplebackend.entity.TemplateItem;
import com.example.couplebackend.entity.TodoItem;
import com.example.couplebackend.entity.UiOption;
import com.example.couplebackend.entity.User;
import com.example.couplebackend.entity.WishItem;
import com.example.couplebackend.mapper.ActivityMapper;
import com.example.couplebackend.mapper.AnniversaryMapper;
import com.example.couplebackend.mapper.AuditRecordMapper;
import com.example.couplebackend.mapper.CheckinRecordMapper;
import com.example.couplebackend.mapper.DressItemMapper;
import com.example.couplebackend.mapper.MessageMapper;
import com.example.couplebackend.mapper.MoodStatusMapper;
import com.example.couplebackend.mapper.PaymentOrderMapper;
import com.example.couplebackend.mapper.ProductMapper;
import com.example.couplebackend.mapper.ServiceCategoryMapper;
import com.example.couplebackend.mapper.ServiceItemMapper;
import com.example.couplebackend.mapper.ServiceOrderMapper;
import com.example.couplebackend.mapper.ServiceTemplateItemMapper;
import com.example.couplebackend.mapper.ServiceTemplateMapper;
import com.example.couplebackend.mapper.SpaceDressMapper;
import com.example.couplebackend.mapper.SpaceInviteMapper;
import com.example.couplebackend.mapper.SpaceMapper;
import com.example.couplebackend.mapper.SpaceMemberMapper;
import com.example.couplebackend.mapper.TodoItemMapper;
import com.example.couplebackend.mapper.UiOptionMapper;
import com.example.couplebackend.mapper.UserMapper;
import com.example.couplebackend.mapper.WishItemMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CoupleService {
    private static final Long DEFAULT_DRESS_ID = 201L;

    @Value("${app.public-base-url:https://couple.vikeatl.xyz}")
    private String publicBaseUrl;

    private final IdGenerator idGenerator;
    private final UserMapper userMapper;
    private final SpaceMapper spaceMapper;
    private final SpaceMemberMapper spaceMemberMapper;
    private final SpaceInviteMapper spaceInviteMapper;
    private final ServiceCategoryMapper serviceCategoryMapper;
    private final ServiceItemMapper serviceItemMapper;
    private final ServiceOrderMapper serviceOrderMapper;
    private final ServiceTemplateMapper serviceTemplateMapper;
    private final ServiceTemplateItemMapper serviceTemplateItemMapper;
    private final TodoItemMapper todoItemMapper;
    private final MoodStatusMapper moodStatusMapper;
    private final DressItemMapper dressItemMapper;
    private final SpaceDressMapper spaceDressMapper;
    private final ActivityMapper activityMapper;
    private final CheckinRecordMapper checkinRecordMapper;
    private final WishItemMapper wishItemMapper;
    private final AnniversaryMapper anniversaryMapper;
    private final MessageMapper messageMapper;
    private final ProductMapper productMapper;
    private final PaymentOrderMapper paymentOrderMapper;
    private final AuditRecordMapper auditRecordMapper;
    private final UiOptionMapper uiOptionMapper;
    private final WeChatSessionClient weChatSessionClient;
    private final QueryCacheInvalidator queryCacheInvalidator;

    public CoupleService(
            IdGenerator idGenerator,
            UserMapper userMapper,
            SpaceMapper spaceMapper,
            SpaceMemberMapper spaceMemberMapper,
            SpaceInviteMapper spaceInviteMapper,
            ServiceCategoryMapper serviceCategoryMapper,
            ServiceItemMapper serviceItemMapper,
            ServiceOrderMapper serviceOrderMapper,
            ServiceTemplateMapper serviceTemplateMapper,
            ServiceTemplateItemMapper serviceTemplateItemMapper,
            TodoItemMapper todoItemMapper,
            MoodStatusMapper moodStatusMapper,
            DressItemMapper dressItemMapper,
            SpaceDressMapper spaceDressMapper,
            ActivityMapper activityMapper,
            CheckinRecordMapper checkinRecordMapper,
            WishItemMapper wishItemMapper,
            AnniversaryMapper anniversaryMapper,
            MessageMapper messageMapper,
            ProductMapper productMapper,
            PaymentOrderMapper paymentOrderMapper,
            AuditRecordMapper auditRecordMapper,
            UiOptionMapper uiOptionMapper,
            WeChatSessionClient weChatSessionClient,
            QueryCacheInvalidator queryCacheInvalidator
    ) {
        this.idGenerator = idGenerator;
        this.userMapper = userMapper;
        this.spaceMapper = spaceMapper;
        this.spaceMemberMapper = spaceMemberMapper;
        this.spaceInviteMapper = spaceInviteMapper;
        this.serviceCategoryMapper = serviceCategoryMapper;
        this.serviceItemMapper = serviceItemMapper;
        this.serviceOrderMapper = serviceOrderMapper;
        this.serviceTemplateMapper = serviceTemplateMapper;
        this.serviceTemplateItemMapper = serviceTemplateItemMapper;
        this.todoItemMapper = todoItemMapper;
        this.moodStatusMapper = moodStatusMapper;
        this.dressItemMapper = dressItemMapper;
        this.spaceDressMapper = spaceDressMapper;
        this.activityMapper = activityMapper;
        this.checkinRecordMapper = checkinRecordMapper;
        this.wishItemMapper = wishItemMapper;
        this.anniversaryMapper = anniversaryMapper;
        this.messageMapper = messageMapper;
        this.productMapper = productMapper;
        this.paymentOrderMapper = paymentOrderMapper;
        this.auditRecordMapper = auditRecordMapper;
        this.uiOptionMapper = uiOptionMapper;
        this.weChatSessionClient = weChatSessionClient;
        this.queryCacheInvalidator = queryCacheInvalidator;
    }

    @Transactional
    public Map<String, Object> login(Map<String, Object> body) {
        String code = string(body, "code", "");
        String openid = string(body, "openid", "");
        if (!code.isBlank()) {
            openid = weChatSessionClient.code2Session(code).openid();
        }
        if (openid.isBlank()) {
            throw new BusinessException("缺少微信登录凭证");
        }
        String nickname = string(body, "nickname", "微信用户");
        String avatarUrl = string(body, "avatarUrl", "");
        String phoneCode = string(body, "phoneCode", "");
        WeChatSessionClient.PhoneNumber phoneNumber = phoneCode.isBlank() ? null : weChatSessionClient.getPhoneNumber(phoneCode);
        User user = userMapper.selectByOpenid(openid);
        if (user == null) {
            user = new User(nextId(), openid, nickname, "空间成员", avatarUrl, 20, 0, "active");
            userMapper.insert(user);
        } else if (!avatarUrl.isBlank() || !"微信用户".equals(nickname)) {
            String nextNickname = "微信用户".equals(nickname) ? user.nickname() : nickname;
            String nextAvatar = avatarUrl.isBlank() ? user.avatarUrl() : avatarUrl;
            user = new User(user.id(), user.openid(), nextNickname, user.title(), nextAvatar, user.points(), user.contribution(), user.status());
            userMapper.updateProfile(user);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("token", "token-" + user.id());
        result.put("user", user);
        result.put("currentSpace", currentSpace(user.id()));
        result.put("phoneAuthorized", phoneNumber != null);
        evictQueryCaches();
        return result;
    }

    @Cacheable(cacheNames = CacheNames.WX_BOOTSTRAP, key = "#userId")
    public Map<String, Object> bootstrap(Long userId) {
        Space space = currentSpace(userId);
        User currentUser = user(userId);
        List<Map<String, Object>> anniversaryViews = anniversaryViews(space.id());
        DressItem currentDress = activeDress(space.id());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("user", currentUser);
        result.put("space", space);
        result.put("currentMemberRole", currentMemberRole(space.id(), userId));
        result.put("currentRoleText", roleText(currentMemberRole(space.id(), userId)));
        result.put("isSpaceAdmin", isSpaceAdmin(userId, space.id()));
        result.put("members", members(space.id()));
        result.put("memberUsers", memberUsers(space.id()));
        result.put("categories", categories(space.id()));
        result.put("serviceItems", serviceItemViews(space.id()));
        result.put("orders", orders(space.id(), null));
        result.put("todos", todoViews(space.id()));
        result.put("moodOptions", moodOptions());
        result.put("quickSignals", quickSignals());
        result.put("orderSides", optionViews("order_side"));
        result.put("orderStatusFilters", optionViews("order_status"));
        result.put("todoTabs", optionViews("todo_status"));
        result.put("todoPriorities", optionViews("todo_priority"));
        result.put("moods", moods(space.id()));
        result.put("currentMood", mood(space.id(), userId));
        result.put("dressItems", dressItems());
        result.put("activeDress", currentDress);
        result.put("ownedDressIds", ownedDressIds(space.id()));
        result.put("activities", activities());
        result.put("checkinToday", checkedInToday(space.id(), userId));
        result.put("wishes", wishes(space.id()));
        result.put("anniversaries", anniversaryViews);
        result.put("messages", messages(space.id(), userId));
        result.put("products", products());
        result.put("templates", templates());
        result.put("stats", spaceStats(space.id()));
        result.put("mineMenuGroups", mineMenuGroups(currentUser, currentDress, anniversaryViews, ownedDressIds(space.id())));
        return result;
    }

    @Transactional
    public Space currentSpace(Long userId) {
        Space space = spaceMapper.selectCurrentByUserId(userId);
        if (space != null) {
            return space;
        }
        User user = user(userId);
        String defaultSpaceName = "微信用户".equals(user.nickname()) ? "我们的服务小店" : user.nickname() + "的小家";
        return createSpace(userId, Map.of("name", defaultSpaceName, "type", "couple"));
    }

    @Transactional
    public Space createSpace(Long userId, Map<String, Object> body) {
        Space space = new Space(nextId(), string(body, "name", "我们的服务小店"), string(body, "type", "couple"), userId,
                string(body, "announcement", "今天也要好好照顾彼此"),
                string(body, "coverUrl", ""),
                longValue(body, "currentDressId", DEFAULT_DRESS_ID), "active", intValue(body, "level", 1));
        spaceMapper.insert(space);
        spaceMemberMapper.insert(new SpaceMember(nextId(), space.id(), userId, "owner", "空间主人", "active"));
        spaceDressMapper.insertIgnore(new SpaceDress(nextId(), space.id(), space.currentDressId(), userId, "default", true));
        evictQueryCaches();
        return space;
    }

    @Transactional
    public Space updateCurrentSpace(Long userId, Map<String, Object> body) {
        Space old = currentSpace(userId);
        assertSpaceAdmin(userId, old.id());
        String name = value(body, "name", old.name()).trim();
        if (name.isBlank()) {
            throw new BusinessException("空间名称不能为空");
        }
        String announcement = body != null && body.containsKey("announcement")
                ? value(body, "announcement", "")
                : old.announcement();
        String coverUrl = body != null && body.containsKey("coverUrl")
                ? value(body, "coverUrl", "")
                : old.coverUrl();
        Space updated = new Space(old.id(), name, old.type(), old.ownerUserId(), announcement.trim(), coverUrl.trim(),
                old.currentDressId(), old.status(), old.level());
        spaceMapper.updateProfile(updated);
        evictQueryCaches();
        return updated;
    }

    @Transactional
    public Space transferCurrentSpaceManager(Long userId, Map<String, Object> body) {
        Space old = currentSpace(userId);
        if (!Objects.equals(old.ownerUserId(), userId)) {
            throw new BusinessException(403, "只有当前空间管理员可以交接管理权限");
        }
        Long targetUserId = longValue(body, "targetUserId", null);
        if (targetUserId == null) {
            throw new BusinessException("请选择要交接的对象");
        }
        if (Objects.equals(targetUserId, userId)) {
            return old;
        }
        SpaceMember targetMember = spaceMemberMapper.selectActiveBySpaceIdAndUserId(old.id(), targetUserId);
        if (targetMember == null) {
            throw new BusinessException("只能把管理权限交给当前空间成员");
        }

        Space updated = new Space(old.id(), old.name(), old.type(), targetUserId, old.announcement(), old.coverUrl(),
                old.currentDressId(), old.status(), old.level());
        spaceMapper.updateOwner(updated);
        spaceMemberMapper.updateRole(old.id(), userId, "partner");
        spaceMemberMapper.updateRole(old.id(), targetUserId, "owner");
        serviceItemMapper.transferDefaultAssignee(old.id(), userId, targetUserId);
        evictQueryCaches();
        return updated;
    }

    @Transactional
    public Space joinSpace(Long userId, Map<String, Object> body) {
        Long spaceId = longValue(body, "spaceId", 1L);
        Space space = space(spaceId);
        if (spaceMemberMapper.countBySpaceIdAndUserId(spaceId, userId) == 0) {
            spaceMemberMapper.insert(new SpaceMember(nextId(), spaceId, userId, "member", string(body, "displayName", user(userId).nickname()), "active"));
            evictQueryCaches();
        }
        return space;
    }

    @Transactional
    public Map<String, Object> createSpaceInvite(Long userId) {
        Space space = currentSpace(userId);
        SpaceInvite invite = spaceInviteMapper.selectReusable(space.id(), userId);
        if (invite == null) {
            invite = new SpaceInvite(nextId(), space.id(), inviteCode(), userId, LocalDateTime.now().plusDays(7), 1, 0, "active");
            spaceInviteMapper.insert(invite);
        }
        return inviteView(invite, space, user(userId));
    }

    @Transactional
    public Map<String, Object> acceptSpaceInvite(Long userId, Map<String, Object> body) {
        String inviteCode = string(body, "inviteCode", "").trim();
        if (inviteCode.isBlank()) {
            throw new BusinessException("邀请已失效，请让对方重新分享");
        }
        SpaceInvite invite = spaceInviteMapper.selectByCode(inviteCode);
        if (invite == null || !"active".equals(invite.status())) {
            throw new BusinessException("邀请已失效，请让对方重新分享");
        }
        if (invite.expireAt() != null && invite.expireAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("邀请已过期，请让对方重新分享");
        }
        Space space = space(invite.spaceId());
        User user = user(userId);
        if (Objects.equals(invite.createdBy(), userId)) {
            return inviteResult(space, user, false, "这是你自己的空间");
        }
        if (spaceMemberMapper.countBySpaceIdAndUserId(space.id(), userId) > 0) {
            return inviteResult(space, user, false, "已经在这个空间里了");
        }
        if (invite.usedCount() >= invite.maxUseCount()) {
            throw new BusinessException("邀请名额已经被使用，请让对方重新分享");
        }
        spaceMemberMapper.insert(new SpaceMember(nextId(), space.id(), userId, "partner", user.nickname(), "active"));
        spaceInviteMapper.increaseUsed(invite.id());
        evictQueryCaches();
        addMessage(space.id(), invite.createdBy(), userId, "invite", "对象已加入", user.nickname() + " 加入了你的服务空间", 0L);
        return inviteResult(space, user, true, "绑定成功");
    }

    @Transactional
    public User updateProfile(Long userId, Map<String, Object> body) {
        User old = user(userId);
        String nickname = string(body, "nickname", old.nickname());
        String avatarUrl = string(body, "avatarUrl", old.avatarUrl());
        User updated = new User(old.id(), old.openid(), nickname, old.title(), avatarUrl, old.points(), old.contribution(), old.status());
        userMapper.updateProfile(updated);
        evictQueryCaches();
        return updated;
    }

    public Map<String, Object> uploadAvatar(Long userId, MultipartFile file) {
        return uploadImage(userId, file, "avatars", "头像");
    }

    public Map<String, Object> uploadServiceImage(Long userId, MultipartFile file) {
        currentSpace(userId);
        return uploadImage(userId, file, "services", "服务图片");
    }

    private Map<String, Object> uploadImage(Long userId, MultipartFile file, String directoryName, String label) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择" + label);
        }
        try {
            String originalName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
            String suffix = ".jpg";
            int dotIndex = originalName.lastIndexOf('.');
            if (dotIndex >= 0 && dotIndex < originalName.length() - 1) {
                suffix = originalName.substring(dotIndex).replaceAll("[^a-zA-Z0-9.]", "");
            }
            String filename = userId + "-" + nextId() + suffix;
            java.nio.file.Path directory = java.nio.file.Paths.get("uploads", directoryName).toAbsolutePath().normalize();
            java.nio.file.Files.createDirectories(directory);
            java.nio.file.Path target = directory.resolve(filename).normalize();
            if (!target.startsWith(directory)) {
                throw new BusinessException(label + "路径非法");
            }
            file.transferTo(target);
            String url = publicBaseUrl.replaceAll("/$", "") + "/uploads/" + directoryName + "/" + filename;
            return Map.of("url", url);
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessException(label + "上传失败：" + exception.getMessage());
        }
    }

    @Cacheable(cacheNames = CacheNames.SERVICE_CATEGORIES, key = "#spaceId")
    public List<ServiceCategory> categories(Long spaceId) {
        return serviceCategoryMapper.selectBySpaceId(spaceId);
    }

    @Transactional
    public ServiceCategory saveCategory(Long userId, Map<String, Object> body) {
        Space space = currentSpace(userId);
        assertSpaceAdmin(userId, space.id());
        Long id = longValue(body, "id", null);
        ServiceCategory old = id == null ? null : serviceCategoryMapper.selectById(id);
        if (old != null) {
            space = space(old.spaceId());
            assertSpaceAdmin(userId, space.id());
        }
        ServiceCategory category = new ServiceCategory(
                id == null ? nextId() : id,
                space.id(),
                string(body, "name", "新分类"),
                string(body, "description", old == null ? "" : old.description()),
                string(body, "iconUrl", old == null ? "" : old.iconUrl()),
                intValue(body, "sortOrder", old == null ? categories(space.id()).size() + 1 : old.sortOrder()),
                boolValue(body, "visible", old == null || old.visible())
        );
        if (id == null) {
            serviceCategoryMapper.insert(category);
        } else {
            serviceCategoryMapper.update(category);
        }
        evictQueryCaches();
        return category;
    }

    @Transactional
    public void removeCategory(Long userId, Long id) {
        ServiceCategory category = serviceCategoryMapper.selectById(id);
        if (category == null) {
            return;
        }
        assertSpaceAdmin(userId, category.spaceId());
        serviceCategoryMapper.logicDelete(id);
        serviceItemMapper.logicDeleteByCategoryId(id);
        evictQueryCaches();
    }

    @Cacheable(cacheNames = CacheNames.SERVICE_ITEMS, key = "#spaceId")
    public List<ServiceItem> serviceItems(Long spaceId) {
        return serviceItemMapper.selectBySpaceId(spaceId);
    }

    @Cacheable(cacheNames = CacheNames.SERVICE_ITEM_VIEWS, key = "#spaceId")
    public List<Map<String, Object>> serviceItemViews(Long spaceId) {
        return serviceItems(spaceId).stream()
                .map(this::serviceItemView)
                .toList();
    }

    @Transactional
    public ServiceItem saveServiceItem(Long userId, Map<String, Object> body) {
        Space space = currentSpace(userId);
        Long id = longValue(body, "id", null);
        ServiceItem old = id == null ? null : serviceItemMapper.selectById(id);
        if (old != null) {
            space = space(old.spaceId());
        }
        assertSpaceAdmin(userId, space.id());
        Long categoryId = longValue(body, "categoryId", old == null ? firstCategoryId(space.id()) : old.categoryId());
        ServiceItem item = new ServiceItem(
                id == null ? nextId() : id,
                space.id(),
                categoryId,
                string(body, "name", old == null ? "新服务" : old.name()),
                string(body, "description", old == null ? "" : old.description()),
                string(body, "imageUrl", old == null ? "" : old.imageUrl()),
                string(body, "tag", old == null ? "服务" : old.tag()),
                intValue(body, "pointCost", old == null ? 0 : old.pointCost()),
                intValue(body, "dailyLimit", old == null ? 0 : old.dailyLimit()),
                intValue(body, "cooldownMinutes", old == null ? 0 : old.cooldownMinutes()),
                boolValue(body, "requireRemark", old != null && old.requireRemark()),
                boolValue(body, "requireAppointTime", old != null && old.requireAppointTime()),
                intValue(body, "sortOrder", old == null ? serviceItems(space.id()).size() + 1 : old.sortOrder()),
                string(body, "status", old == null ? "active" : old.status()),
                old == null ? 0 : old.monthlySales(),
                longValue(body, "defaultAssigneeId", old == null ? space.ownerUserId() : old.defaultAssigneeId())
        );
        if (old == null) {
            serviceItemMapper.insert(item);
        } else {
            serviceItemMapper.update(item);
        }
        evictQueryCaches();
        return item;
    }

    @Transactional
    public void removeServiceItem(Long userId, Long id) {
        ServiceItem item = serviceItemMapper.selectById(id);
        if (item == null) {
            return;
        }
        assertSpaceAdmin(userId, item.spaceId());
        serviceItemMapper.logicDelete(id);
        evictQueryCaches();
    }

    @Cacheable(cacheNames = CacheNames.SPACE_MENU, key = "#spaceId")
    public List<Map<String, Object>> menu(Long spaceId) {
        return categories(spaceId).stream()
                .map(category -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("category", category);
                    item.put("items", serviceItems(spaceId).stream()
                            .filter(service -> Objects.equals(service.categoryId(), category.id()))
                            .toList());
                    return item;
                })
                .toList();
    }

    @Transactional
    public ServiceOrder createOrder(Long userId, Map<String, Object> body) {
        Space space = currentSpace(userId);
        Long serviceItemId = longValue(body, "serviceItemId", null);
        if (serviceItemId == null) {
            throw new BusinessException("请选择服务");
        }
        ServiceItem service = serviceItemMapper.selectById(serviceItemId);
        if (service == null) {
            throw new BusinessException("服务不存在");
        }
        validateOrderLimit(userId, service);
        if (service.pointCost() > 0) {
            spendPoints(userId, service.pointCost());
        }

        LocalDateTime now = LocalDateTime.now();
        Long assigneeId = service.defaultAssigneeId() == null ? space.ownerUserId() : service.defaultAssigneeId();
        if (spaceMemberMapper.selectActiveBySpaceIdAndUserId(space.id(), assigneeId) == null) {
            assigneeId = space.ownerUserId();
        }
        ServiceOrder order = new ServiceOrder(nextId(), "SO" + System.currentTimeMillis(), space.id(), serviceItemId, userId, assigneeId,
                "pending", string(body, "remark", ""), null, now, null, null, null);
        serviceOrderMapper.insert(order);
        serviceItemMapper.increaseMonthlySales(service.id());
        addMessage(space.id(), assigneeId, userId, "service_order_created", "新的点单来了", service.name() + " 等待接单：" + order.remark(), order.id());
        evictQueryCaches();
        return order;
    }

    @Cacheable(cacheNames = CacheNames.SERVICE_ORDERS, key = "#spaceId + ':' + (#status == null ? '' : #status)")
    public List<Map<String, Object>> orders(Long spaceId, String status) {
        return serviceOrderMapper.selectBySpaceId(spaceId).stream()
                .filter(item -> status == null || status.isBlank() || Objects.equals(item.status(), status))
                .map(this::enrichOrder)
                .toList();
    }

    @Transactional
    public ServiceOrder changeOrderStatus(Long userId, Long orderId, String action) {
        ServiceOrder old = serviceOrderMapper.selectById(orderId);
        if (old == null) {
            throw new BusinessException("订单不存在");
        }
        String nextStatus = switch (action) {
            case "accept" -> "accepted";
            case "complete" -> "completed";
            case "cancel" -> "canceled";
            case "reject" -> "rejected";
            default -> throw new BusinessException("未知订单动作");
        };
        LocalDateTime now = LocalDateTime.now();
        ServiceOrder updated = new ServiceOrder(old.id(), old.orderNo(), old.spaceId(), old.serviceItemId(), old.requesterId(), old.assigneeId(), nextStatus,
                old.remark(), old.appointTime(), old.createdAt(),
                "accepted".equals(nextStatus) ? now : old.acceptedAt(),
                "completed".equals(nextStatus) ? now : old.completedAt(),
                "canceled".equals(nextStatus) ? now : old.canceledAt());
        serviceOrderMapper.updateStatus(updated);
        addMessage(old.spaceId(), old.requesterId(), userId, "service_order_" + nextStatus, "点单状态更新", "你的点单已变为：" + nextStatus, old.id());
        if ("completed".equals(nextStatus)) {
            addContribution(old.assigneeId(), 2);
        }
        evictQueryCaches();
        return updated;
    }

    @Cacheable(cacheNames = CacheNames.TODOS, key = "#spaceId + ':' + (#status == null ? '' : #status)")
    public List<TodoItem> todos(Long spaceId, String status) {
        return todoItemMapper.selectBySpaceId(spaceId).stream()
                .filter(item -> status == null || status.isBlank() || Objects.equals(item.status(), status))
                .toList();
    }

    @Cacheable(cacheNames = CacheNames.TODO_VIEWS, key = "#spaceId")
    public List<Map<String, Object>> todoViews(Long spaceId) {
        Map<Long, User> memberMap = memberUsers(spaceId).stream()
                .collect(java.util.stream.Collectors.toMap(User::id, item -> item, (left, right) -> left));
        Map<String, UiOption> priorities = uiOptionMap("todo_priority");
        return todos(spaceId, null).stream()
                .map(item -> todoView(item, memberMap, priorities))
                .toList();
    }

    @Transactional
    public TodoItem createTodo(Long userId, Map<String, Object> body) {
        Space space = currentSpace(userId);
        Long assigneeId = longValue(body, "assigneeId", userId);
        TodoItem todo = new TodoItem(nextId(), space.id(), string(body, "title", "新待办"), string(body, "description", ""), userId,
                assigneeId, intValue(body, "priority", 1), dateTime(body, "dueTime", LocalDateTime.now().plusDays(1)), "pending");
        todoItemMapper.insert(todo);
        addMessage(space.id(), assigneeId, userId, "todo_assigned", "新的待办", todo.title(), todo.id());
        evictQueryCaches();
        return todo;
    }

    @Transactional
    public TodoItem changeTodoStatus(Long userId, Long id, String status) {
        TodoItem old = todoItemMapper.selectById(id);
        if (old == null) {
            throw new BusinessException("待办不存在");
        }
        todoItemMapper.updateStatus(id, status);
        if ("done".equals(status)) {
            addContribution(userId, 1);
            addPoints(userId, 2);
        }
        evictQueryCaches();
        return todoItemMapper.selectById(id);
    }

    @Cacheable(cacheNames = CacheNames.MOOD_OPTIONS)
    public List<Map<String, Object>> moodOptions() {
        return optionViews("mood");
    }

    @Cacheable(cacheNames = CacheNames.QUICK_SIGNALS)
    public List<Map<String, Object>> quickSignals() {
        return uiOptionMapper.selectByGroup("quick_signal").stream()
                .map(item -> {
                    Map<String, Object> option = optionView(item);
                    option.put("title", item.label());
                    option.put("content", item.description());
                    return option;
                })
                .toList();
    }

    @Cacheable(cacheNames = CacheNames.MOODS, key = "#spaceId")
    public List<MoodStatus> moods(Long spaceId) {
        return moodStatusMapper.selectBySpaceId(spaceId);
    }

    @Cacheable(cacheNames = CacheNames.CURRENT_MOOD, key = "#spaceId + ':' + #userId")
    public MoodStatus mood(Long spaceId, Long userId) {
        return moodStatusMapper.selectBySpaceIdAndUserId(spaceId, userId);
    }

    @Transactional
    public MoodStatus updateMood(Long userId, Map<String, Object> body) {
        Space space = currentSpace(userId);
        String key = string(body, "moodKey", "happy");
        String label = moodOptions().stream()
                .filter(item -> key.equals(item.get("key")))
                .map(item -> String.valueOf(item.get("label")))
                .findFirst()
                .orElse(string(body, "moodLabel", "心情不错"));
        MoodStatus old = mood(space.id(), userId);
        MoodStatus mood = new MoodStatus(old == null ? nextId() : old.id(), space.id(), userId, key, label, string(body, "note", ""), LocalDateTime.now());
        moodStatusMapper.upsert(mood);
        evictQueryCaches();
        return mood;
    }

    @Transactional
    public Message sendSignal(Long userId, Map<String, Object> body) {
        Space space = currentSpace(userId);
        Long receiverId = longValue(body, "receiverId", firstOtherMember(space.id(), userId));
        String key = string(body, "key", "");
        UiOption option = key.isBlank() ? null : uiOptionMapper.selectByGroupAndKey("quick_signal", key);
        String title = string(body, "title", option == null ? "想你了" : option.label());
        String content = string(body, "content", option == null ? "对方戳了你一下" : option.description());
        Message message = addMessage(space.id(), receiverId, userId, "signal", title, content, 0L);
        evictQueryCaches();
        return message;
    }

    @Cacheable(cacheNames = CacheNames.DRESS_ITEMS)
    public List<DressItem> dressItems() {
        return dressItemMapper.selectAll().stream()
                .filter(item -> "active".equals(item.status()))
                .toList();
    }

    @Cacheable(cacheNames = CacheNames.ACTIVE_DRESS, key = "#spaceId")
    public DressItem activeDress(Long spaceId) {
        Long activeDressId = spaceDressMapper.selectBySpaceId(spaceId).stream()
                .filter(SpaceDress::active)
                .map(SpaceDress::dressItemId)
                .findFirst()
                .orElseGet(() -> {
                    Space space = space(spaceId);
                    return space.currentDressId() == null ? DEFAULT_DRESS_ID : space.currentDressId();
                });
        return dressItemMapper.selectById(activeDressId);
    }

    @Cacheable(cacheNames = CacheNames.OWNED_DRESS_IDS, key = "#spaceId")
    public List<Long> ownedDressIds(Long spaceId) {
        return spaceDressMapper.selectBySpaceId(spaceId).stream()
                .map(SpaceDress::dressItemId)
                .distinct()
                .toList();
    }

    @Transactional
    public DressItem applyDress(Long userId, Long dressId) {
        Space space = currentSpace(userId);
        DressItem dress = dressItemMapper.selectById(dressId);
        if (dress == null) {
            throw new BusinessException("装扮不存在");
        }
        if (!ownedDressIds(space.id()).contains(dressId)) {
            if ("point".equals(dress.obtainType())) {
                spendPoints(userId, dress.pointPrice());
            } else if ("paid".equals(dress.obtainType())) {
                throw new BusinessException("该装扮需要先购买");
            }
            spaceDressMapper.insertIgnore(new SpaceDress(nextId(), space.id(), dressId, userId, dress.obtainType(), false));
        }
        spaceDressMapper.deactivateBySpaceId(space.id());
        spaceDressMapper.activate(space.id(), dressId);
        spaceMapper.updateCurrentDress(new Space(space.id(), space.name(), space.type(), space.ownerUserId(), space.announcement(), space.coverUrl(), dressId, space.status(), space.level()));
        evictQueryCaches();
        return dress;
    }

    @Transactional
    public CheckinRecord checkin(Long userId) {
        Space space = currentSpace(userId);
        LocalDate today = LocalDate.now();
        CheckinRecord existing = checkinRecordMapper.selectByDay(space.id(), userId, today);
        if (existing != null) {
            return existing;
        }
        int continuous = checkinRecordMapper.selectByUser(space.id(), userId).stream()
                .map(CheckinRecord::continuousDays)
                .max(Integer::compareTo)
                .orElse(0) + 1;
        Long rewardDressId = continuous >= 7 ? 202L : null;
        CheckinRecord record = new CheckinRecord(nextId(), space.id(), userId, today, continuous, 5, rewardDressId);
        checkinRecordMapper.insert(record);
        addPoints(userId, record.rewardPoint());
        if (rewardDressId != null && !ownedDressIds(space.id()).contains(rewardDressId)) {
            spaceDressMapper.insertIgnore(new SpaceDress(nextId(), space.id(), rewardDressId, userId, "checkin", false));
        }
        evictQueryCaches();
        return record;
    }

    @Cacheable(cacheNames = CacheNames.CHECKED_IN_TODAY, key = "#spaceId + ':' + #userId + ':' + T(java.time.LocalDate).now()")
    public boolean checkedInToday(Long spaceId, Long userId) {
        return checkinRecordMapper.selectByDay(spaceId, userId, LocalDate.now()) != null;
    }

    @Cacheable(cacheNames = CacheNames.WISHES, key = "#spaceId")
    public List<WishItem> wishes(Long spaceId) {
        return wishItemMapper.selectBySpaceId(spaceId);
    }

    @Transactional
    public WishItem createWish(Long userId, Map<String, Object> body) {
        Space space = currentSpace(userId);
        WishItem wish = new WishItem(nextId(), space.id(), userId, null, string(body, "title", "新愿望"),
                string(body, "description", ""), string(body, "type", "life"), "open");
        wishItemMapper.insert(wish);
        evictQueryCaches();
        return wish;
    }

    @Transactional
    public WishItem claimWish(Long userId, Long id) {
        WishItem old = wishItemMapper.selectById(id);
        if (old == null) {
            throw new BusinessException("愿望不存在");
        }
        WishItem updated = new WishItem(old.id(), old.spaceId(), old.creatorId(), userId, old.title(), old.description(), old.type(), "claimed");
        wishItemMapper.updateClaim(updated);
        evictQueryCaches();
        return updated;
    }

    @Cacheable(cacheNames = CacheNames.ANNIVERSARIES, key = "#spaceId")
    public List<Anniversary> anniversaries(Long spaceId) {
        return anniversaryMapper.selectBySpaceId(spaceId);
    }

    @Transactional
    public Anniversary createAnniversary(Long userId, Map<String, Object> body) {
        Space space = currentSpace(userId);
        String name = string(body, "name", "新纪念日").trim();
        if (name.isBlank()) {
            throw new BusinessException("纪念日名称不能为空");
        }
        Anniversary anniversary = new Anniversary(nextId(), space.id(), name,
                date(body, "date", LocalDate.now()), boolValue(body, "important", false));
        anniversaryMapper.insert(anniversary);
        evictQueryCaches();
        return anniversary;
    }

    @Transactional
    public Anniversary updateAnniversary(Long userId, Long id, Map<String, Object> body) {
        Space space = currentSpace(userId);
        Anniversary old = anniversaryMapper.selectById(id);
        if (old == null || !Objects.equals(old.spaceId(), space.id())) {
            throw new BusinessException("纪念日不存在");
        }
        String name = string(body, "name", old.name()).trim();
        if (name.isBlank()) {
            throw new BusinessException("纪念日名称不能为空");
        }
        Anniversary updated = new Anniversary(old.id(), old.spaceId(), name,
                date(body, "date", old.date()), boolValue(body, "important", old.important()));
        anniversaryMapper.update(updated);
        evictQueryCaches();
        return updated;
    }

    @Transactional
    public void removeAnniversary(Long userId, Long id) {
        Space space = currentSpace(userId);
        Anniversary old = anniversaryMapper.selectById(id);
        if (old == null || !Objects.equals(old.spaceId(), space.id())) {
            return;
        }
        anniversaryMapper.logicDelete(id);
        evictQueryCaches();
    }

    @Cacheable(cacheNames = CacheNames.MESSAGES, key = "#spaceId + ':' + #userId")
    public List<Message> messages(Long spaceId, Long userId) {
        return messageMapper.selectByReceiver(spaceId, userId);
    }

    @Transactional
    public Message readMessage(Long id) {
        Message old = messageMapper.selectById(id);
        if (old == null) {
            throw new BusinessException("消息不存在");
        }
        messageMapper.markRead(id);
        evictQueryCaches();
        return messageMapper.selectById(id);
    }

    @Cacheable(cacheNames = CacheNames.PRODUCTS)
    public List<Product> products() {
        return productMapper.selectActive();
    }

    @Transactional
    public PaymentOrder createPayment(Long userId, Map<String, Object> body) {
        Long productId = longValue(body, "productId", null);
        if (productId == null) {
            throw new BusinessException("请选择商品");
        }
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        Space space = currentSpace(userId);
        PaymentOrder payment = new PaymentOrder(nextId(), "PO" + System.currentTimeMillis(), userId, space.id(), product.type(), product.targetId(),
                product.amountCent(), "paid", LocalDateTime.now());
        paymentOrderMapper.insert(payment);
        if ("dress".equals(product.type()) && !ownedDressIds(space.id()).contains(product.targetId())) {
            spaceDressMapper.insertIgnore(new SpaceDress(nextId(), space.id(), product.targetId(), userId, "paid", false));
        }
        evictQueryCaches();
        return payment;
    }

    @Cacheable(cacheNames = CacheNames.ACTIVITIES)
    public List<Activity> activities() {
        return activityMapper.selectAll().stream()
                .filter(item -> "active".equals(item.status()))
                .toList();
    }

    @Transactional
    public Activity saveActivity(Map<String, Object> body) {
        Long id = longValue(body, "id", null);
        Activity old = id == null ? null : activityMapper.selectAll().stream().filter(item -> Objects.equals(item.id(), id)).findFirst().orElse(null);
        Activity activity = new Activity(id == null ? nextId() : id,
                string(body, "name", old == null ? "新活动" : old.name()),
                string(body, "type", old == null ? "checkin" : old.type()),
                string(body, "description", old == null ? "" : old.description()),
                date(body, "startDate", old == null ? LocalDate.now() : old.startDate()),
                date(body, "endDate", old == null ? LocalDate.now().plusDays(7) : old.endDate()),
                intValue(body, "rewardPoint", old == null ? 1 : old.rewardPoint()),
                longValue(body, "rewardDressId", old == null ? null : old.rewardDressId()),
                string(body, "status", old == null ? "active" : old.status()));
        if (old == null) {
            activityMapper.insert(activity);
        } else {
            activityMapper.update(activity);
        }
        evictQueryCaches();
        return activity;
    }

    @Transactional
    public DressItem saveDress(Map<String, Object> body) {
        Long id = longValue(body, "id", null);
        DressItem old = id == null ? null : dressItemMapper.selectById(id);
        DressItem dress = new DressItem(id == null ? nextId() : id,
                string(body, "name", old == null ? "新装扮" : old.name()),
                string(body, "type", old == null ? "theme" : old.type()),
                string(body, "obtainType", old == null ? "free" : old.obtainType()),
                longValue(body, "priceCent", old == null ? 0L : old.priceCent()),
                intValue(body, "pointPrice", old == null ? 0 : old.pointPrice()),
                string(body, "rarity", old == null ? "R" : old.rarity()),
                string(body, "status", old == null ? "active" : old.status()),
                string(body, "background", old == null ? "linear-gradient(180deg,#fff,#f7f3ff)" : old.background()),
                string(body, "primaryColor", old == null ? "#9b88ed" : old.primaryColor()),
                string(body, "cardColor", old == null ? "#ffffff" : old.cardColor()),
                string(body, "textColor", old == null ? "#333333" : old.textColor()));
        if (old == null) {
            dressItemMapper.insert(dress);
        } else {
            dressItemMapper.update(dress);
        }
        evictQueryCaches();
        return dress;
    }

    @Cacheable(cacheNames = CacheNames.TEMPLATES)
    public List<ServiceTemplate> templates() {
        List<ServiceTemplate> templates = serviceTemplateMapper.selectActive();
        templates.forEach(template -> template.items.addAll(serviceTemplateItemMapper.selectByTemplateId(template.id)));
        return templates;
    }

    @Transactional
    public Map<String, Object> importTemplate(Long userId, Long templateId) {
        Space space = currentSpace(userId);
        assertSpaceAdmin(userId, space.id());
        ServiceTemplate template = templates().stream()
                .filter(item -> Objects.equals(item.id, templateId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("模板不存在"));
        int count = 0;
        for (TemplateItem templateItem : template.items) {
            Long categoryId = categories(space.id()).stream()
                    .filter(item -> item.name().equals(templateItem.categoryName()))
                    .map(ServiceCategory::id)
                    .findFirst()
                    .orElseGet(() -> {
                        ServiceCategory category = new ServiceCategory(nextId(), space.id(), templateItem.categoryName(), "模板导入", "", categories(space.id()).size() + 1, true);
                        serviceCategoryMapper.insert(category);
                        return category.id();
                    });
            serviceItemMapper.insert(new ServiceItem(nextId(), space.id(), categoryId, templateItem.serviceName(), templateItem.description(), templateImageUrl(count),
                    firstText(templateItem.categoryName(), "服"), 0, 0, 0, templateItem.serviceName().contains("奶茶") || templateItem.serviceName().contains("晚饭"), true, serviceItems(space.id()).size() + 1, "active", 0, space.ownerUserId()));
            count++;
        }
        evictQueryCaches();
        return Map.of("imported", count, "template", template);
    }

    private String templateImageUrl(int index) {
        int imageIndex = Math.floorMod(index, 4) + 1;
        return "/assets/service-icons/kitty-tea-" + imageIndex + ".png";
    }

    @Cacheable(cacheNames = CacheNames.ADMIN_DASHBOARD)
    public Map<String, Object> adminDashboard() {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Space> spaces = spaceMapper.selectAll();
        List<User> users = userMapper.selectAll();
        Map<Long, User> userIndex = new LinkedHashMap<>();
        for (User user : users) {
            userIndex.put(user.id(), user);
        }
        List<Map<String, Object>> orders = spaces.stream().flatMap(space -> orders(space.id(), null).stream()).toList();
        List<TodoItem> todos = spaces.stream().flatMap(space -> todos(space.id(), null).stream()).toList();
        result.put("stats", Map.of(
                "users", users.size(),
                "spaces", spaces.size(),
                "serviceItems", spaces.stream().mapToInt(space -> serviceItems(space.id()).size()).sum(),
                "orders", orders.size(),
                "todos", todos.size(),
                "dressItems", dressItemMapper.selectAll().size()
        ));
        result.put("users", users);
        result.put("spaces", spaces.stream().map(space -> adminSpaceView(space, userIndex)).toList());
        result.put("orders", orders);
        result.put("todos", todos);
        result.put("wishes", spaces.stream().flatMap(space -> wishes(space.id()).stream()).toList());
        result.put("dressItems", dressItemMapper.selectAll());
        result.put("activities", activityMapper.selectAll());
        result.put("payments", paymentOrderMapper.selectAll());
        result.put("audits", auditRecordMapper.selectAll());
        result.put("templates", templates());
        return result;
    }

    private Map<String, Object> adminSpaceView(Space space, Map<Long, User> userIndex) {
        User owner = userIndex.get(space.ownerUserId());
        List<Map<String, Object>> memberViews = members(space.id()).stream()
                .map(member -> adminMemberView(member, userIndex.get(member.userId())))
                .toList();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", space.id());
        result.put("name", space.name());
        result.put("type", space.type());
        result.put("ownerUserId", space.ownerUserId());
        result.put("ownerName", owner == null ? "-" : owner.nickname());
        result.put("announcement", space.announcement());
        result.put("coverUrl", space.coverUrl());
        result.put("currentDressId", space.currentDressId());
        result.put("level", space.level());
        result.put("status", space.status());
        result.put("memberCount", memberViews.size());
        result.put("stats", spaceStats(space.id()));
        result.put("members", memberViews);
        return result;
    }

    private Map<String, Object> adminMemberView(SpaceMember member, User user) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", member.id());
        result.put("spaceId", member.spaceId());
        result.put("userId", member.userId());
        result.put("role", member.role());
        result.put("roleText", roleText(member.role()));
        result.put("displayName", member.displayName());
        result.put("memberStatus", member.status());
        result.put("nickname", user == null ? "-" : user.nickname());
        result.put("title", user == null ? "-" : user.title());
        result.put("avatarUrl", user == null ? "" : user.avatarUrl());
        result.put("points", user == null ? 0 : user.points());
        result.put("contribution", user == null ? 0 : user.contribution());
        result.put("userStatus", user == null ? "unknown" : user.status());
        return result;
    }

    @Cacheable(cacheNames = CacheNames.SPACE_STATS, key = "#spaceId")
    public Map<String, Object> spaceStats(Long spaceId) {
        Map<String, Object> stats = new LinkedHashMap<>();
        List<ServiceOrder> orderList = serviceOrderMapper.selectBySpaceId(spaceId);
        List<TodoItem> todoList = todoItemMapper.selectBySpaceId(spaceId);
        stats.put("memberCount", members(spaceId).size());
        stats.put("pendingOrders", orderList.stream().filter(item -> "pending".equals(item.status())).count());
        stats.put("completedOrders", orderList.stream().filter(item -> "completed".equals(item.status())).count());
        stats.put("pendingTodos", todoList.stream().filter(item -> "pending".equals(item.status())).count());
        stats.put("openWishes", wishes(spaceId).stream().filter(item -> "open".equals(item.status())).count());
        return stats;
    }

    @Cacheable(cacheNames = CacheNames.MEMBERS, key = "#spaceId")
    public List<SpaceMember> members(Long spaceId) {
        return spaceMemberMapper.selectActiveBySpaceId(spaceId);
    }

    @Cacheable(cacheNames = CacheNames.MEMBER_USERS, key = "#spaceId")
    public List<User> memberUsers(Long spaceId) {
        return members(spaceId).stream()
                .map(member -> userMapper.selectById(member.userId()))
                .filter(Objects::nonNull)
                .toList();
    }

    private void validateOrderLimit(Long userId, ServiceItem service) {
        LocalDate today = LocalDate.now();
        if (service.dailyLimit() > 0) {
            long count = serviceOrderMapper.selectBySpaceId(service.spaceId()).stream()
                    .filter(order -> Objects.equals(order.requesterId(), userId) && Objects.equals(order.serviceItemId(), service.id()))
                    .filter(order -> order.createdAt() != null && order.createdAt().toLocalDate().equals(today))
                    .count();
            if (count >= service.dailyLimit()) {
                throw new BusinessException("今天这个服务已经点够啦");
            }
        }
        if (service.cooldownMinutes() > 0) {
            boolean cooling = serviceOrderMapper.selectBySpaceId(service.spaceId()).stream()
                    .filter(order -> Objects.equals(order.requesterId(), userId) && Objects.equals(order.serviceItemId(), service.id()))
                    .anyMatch(order -> order.createdAt() != null && order.createdAt().isAfter(LocalDateTime.now().minusMinutes(service.cooldownMinutes())));
            if (cooling) {
                throw new BusinessException("这个服务正在冷却中，稍后再点");
            }
        }
    }

    private Map<String, Object> enrichOrder(ServiceOrder order) {
        ServiceItem serviceItem = serviceItemMapper.selectById(order.serviceItemId());
        UiOption status = uiOptionMapper.selectByGroupAndKey("order_status", order.status());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", order.id());
        result.put("orderNo", order.orderNo());
        result.put("spaceId", order.spaceId());
        result.put("serviceItemId", order.serviceItemId());
        result.put("requesterId", order.requesterId());
        result.put("assigneeId", order.assigneeId());
        result.put("status", order.status());
        result.put("remark", order.remark());
        result.put("appointTime", order.appointTime());
        result.put("createdAt", order.createdAt());
        result.put("acceptedAt", order.acceptedAt());
        result.put("completedAt", order.completedAt());
        result.put("canceledAt", order.canceledAt());
        result.put("serviceItem", serviceItem);
        result.put("requester", userMapper.selectById(order.requesterId()));
        result.put("assignee", userMapper.selectById(order.assigneeId()));
        result.put("statusLabel", status == null ? order.status() : status.label());
        result.put("statusClass", "status-" + order.status());
        result.put("icon", serviceItem == null ? "·" : displayIcon(serviceItem));
        result.put("title", serviceItem == null ? "服务点单" : serviceItem.name());
        result.put("remarkText", order.remark() == null || order.remark().isBlank() ? "无备注" : order.remark());
        result.put("timeText", formatDateTime(order.createdAt() == null ? order.appointTime() : order.createdAt()));
        result.put("finishText", formatDateTime(order.completedAt()));
        return result;
    }

    private Map<String, Object> serviceItemView(ServiceItem item) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", item.id());
        result.put("spaceId", item.spaceId());
        result.put("categoryId", item.categoryId());
        result.put("name", item.name());
        result.put("description", item.description());
        result.put("imageUrl", item.imageUrl());
        result.put("tag", item.tag());
        result.put("pointCost", item.pointCost());
        result.put("dailyLimit", item.dailyLimit());
        result.put("cooldownMinutes", item.cooldownMinutes());
        result.put("requireRemark", item.requireRemark());
        result.put("requireAppointTime", item.requireAppointTime());
        result.put("sortOrder", item.sortOrder());
        result.put("status", item.status());
        result.put("monthlySales", item.monthlySales());
        result.put("defaultAssigneeId", item.defaultAssigneeId());
        result.put("visualText", displayIcon(item));
        result.put("priceText", item.pointCost() > 0 ? item.pointCost() + " 积分" : "免费");
        result.put("needTimeText", item.requireAppointTime() ? "需预约" : "立即");
        result.put("needRemarkText", item.requireRemark() ? "填备注" : "");
        result.put("orderButtonText", item.cooldownMinutes() > 0 ? "冷却中" : "立即召唤");
        result.put("cooldownText", item.cooldownMinutes() > 0 ? item.cooldownMinutes() + " 分钟" : "");
        return result;
    }

    private Map<String, Object> todoView(TodoItem item, Map<Long, User> memberMap, Map<String, UiOption> priorities) {
        boolean done = "done".equals(item.status()) || "completed".equals(item.status());
        UiOption priority = priorities.get(String.valueOf(item.priority()));
        User assignee = memberMap.get(item.assigneeId());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", item.id());
        result.put("spaceId", item.spaceId());
        result.put("title", item.title());
        result.put("description", item.description());
        result.put("creatorId", item.creatorId());
        result.put("assigneeId", item.assigneeId());
        result.put("priority", item.priority());
        result.put("dueTime", item.dueTime());
        result.put("status", item.status());
        result.put("done", done);
        result.put("assigneeName", assignee == null ? "未指定" : assignee.nickname());
        result.put("priorityLabel", priority == null ? "普通" : priority.label());
        result.put("priorityClass", priority == null ? "priority-normal" : priority.description());
        result.put("dueText", formatDue(item.dueTime()));
        return result;
    }

    private List<Map<String, Object>> optionViews(String groupKey) {
        return uiOptionMapper.selectByGroup(groupKey).stream()
                .map(this::optionView)
                .toList();
    }

    private Map<String, UiOption> uiOptionMap(String groupKey) {
        return uiOptionMapper.selectByGroup(groupKey).stream()
                .collect(java.util.stream.Collectors.toMap(UiOption::optionKey, item -> item, (left, right) -> left));
    }

    private Map<String, Object> optionView(UiOption item) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", item.id());
        result.put("key", item.optionKey());
        result.put("label", item.label());
        result.put("icon", item.icon());
        result.put("hint", item.description());
        result.put("description", item.description());
        return result;
    }

    private List<Map<String, Object>> anniversaryViews(Long spaceId) {
        return anniversaries(spaceId).stream()
                .map(item -> {
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("id", item.id());
                    result.put("spaceId", item.spaceId());
                    result.put("name", item.name());
                    result.put("date", item.date());
                    result.put("important", item.important());
                    result.put("days", daysFrom(item.date()));
                    return result;
                })
                .toList();
    }

    private List<Map<String, Object>> mineMenuGroups(User user, DressItem activeDress, List<Map<String, Object>> anniversaries, List<Long> ownedDressIds) {
        String anniversaryTip = anniversaries.isEmpty()
                ? "暂无纪念日"
                : anniversaries.get(0).get("name") + "已经" + anniversaries.get(0).get("days") + "天";
        List<Map<String, Object>> firstGroup = List.of(
                mineMenuItem("invite", "💞", "邀请对象", "分享或填写邀请码", ""),
                mineMenuItem("points", "🪙", "积分中心", "做任务赚积分", "HOT"),
                mineMenuItem("dress", "✨", "装扮商店", "当前：" + (activeDress == null ? "默认主题" : activeDress.name()), ""),
                mineMenuItem("anniversary", "📅", "纪念日倒计时", anniversaryTip, "")
        );
        List<Map<String, Object>> secondGroup = List.of(
                mineMenuItem("manage", "🏠", "空间设置", "空间名称与菜单", ""),
                mineMenuItem("feedback", "💬", "意见反馈", "", ""),
                mineMenuItem("account", "👤", "账号与登录", user.openid() == null || user.openid().isBlank() ? "未登录" : "已登录", ""),
                mineMenuItem("settings", "⚙️", "系统设置", "", "")
        );
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("points", user.points());
        stats.put("unlockedDressCount", ownedDressIds.size());
        stats.put("companionDays", anniversaries.isEmpty() ? 0 : anniversaries.get(0).get("days"));
        return List.of(
                Map.of("key", "profile_stats", "items", firstGroup, "stats", stats),
                Map.of("key", "settings", "items", secondGroup)
        );
    }

    private Map<String, Object> inviteView(SpaceInvite invite, Space space, User inviter) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("inviteCode", invite.inviteCode());
        result.put("spaceId", space.id());
        result.put("spaceName", space.name());
        result.put("title", inviter.nickname() + "邀请你加入「" + space.name() + "」");
        result.put("path", "/pages/index/index?inviteCode=" + invite.inviteCode());
        result.put("expireAt", invite.expireAt());
        return result;
    }

    private Map<String, Object> inviteResult(Space space, User user, boolean joined, String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("space", space);
        result.put("user", user);
        result.put("joined", joined);
        result.put("message", message);
        return result;
    }

    private String inviteCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private Map<String, Object> mineMenuItem(String action, String icon, String title, String subtitle, String badge) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("action", action);
        result.put("icon", icon);
        result.put("title", title);
        result.put("subtitle", subtitle);
        result.put("badge", badge);
        return result;
    }

    private String displayIcon(ServiceItem item) {
        if (item.imageUrl() != null && !item.imageUrl().isBlank()) {
            return "";
        }
        String source = item.tag() == null || item.tag().isBlank() ? item.name() : item.tag();
        return firstText(source, "服");
    }

    private String firstText(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        int end = value.offsetByCodePoints(0, 1);
        return value.substring(0, end);
    }

    private String formatDateTime(LocalDateTime value) {
        if (value == null) {
            return "";
        }
        return value.toString().replace('T', ' ').substring(0, 16);
    }

    private String formatDue(LocalDateTime value) {
        if (value == null) {
            return "未设置";
        }
        if (value.toLocalDate().equals(LocalDate.now())) {
            return "今天 " + value.toLocalTime().toString().substring(0, 5);
        }
        return value.toLocalDate().toString().substring(5) + " " + value.toLocalTime().toString().substring(0, 5);
    }

    private long daysFrom(LocalDate date) {
        if (date == null) {
            return 0;
        }
        return Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(date, LocalDate.now()));
    }

    private Message addMessage(Long spaceId, Long receiverId, Long senderId, String type, String title, String content, Long bizId) {
        Message message = new Message(nextId(), spaceId, receiverId, senderId, type, title, content, bizId, false, LocalDateTime.now());
        messageMapper.insert(message);
        return message;
    }

    private void addPoints(Long userId, int points) {
        User user = user(userId);
        userMapper.updateScore(new User(user.id(), user.openid(), user.nickname(), user.title(), user.avatarUrl(), user.points() + points, user.contribution(), user.status()));
    }

    private void spendPoints(Long userId, int points) {
        User user = user(userId);
        if (user.points() < points) {
            throw new BusinessException("积分不足");
        }
        userMapper.updateScore(new User(user.id(), user.openid(), user.nickname(), user.title(), user.avatarUrl(), user.points() - points, user.contribution(), user.status()));
    }

    private void addContribution(Long userId, int value) {
        User user = user(userId);
        userMapper.updateScore(new User(user.id(), user.openid(), user.nickname(), user.title(), user.avatarUrl(), user.points(), user.contribution() + value, user.status()));
    }

    private User user(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }
        return user;
    }

    private Space space(Long id) {
        Space space = spaceMapper.selectById(id);
        if (space == null) {
            throw new BusinessException("空间不存在");
        }
        return space;
    }

    private Long firstCategoryId(Long spaceId) {
        return categories(spaceId).stream().findFirst().map(ServiceCategory::id).orElseThrow(() -> new BusinessException("请先创建分类"));
    }

    private Long firstOtherMember(Long spaceId, Long userId) {
        return members(spaceId).stream()
                .map(SpaceMember::userId)
                .filter(id -> !Objects.equals(id, userId))
                .findFirst()
                .orElse(userId);
    }

    private String currentMemberRole(Long spaceId, Long userId) {
        Space space = space(spaceId);
        if (Objects.equals(space.ownerUserId(), userId)) {
            return "owner";
        }
        return members(spaceId).stream()
                .filter(member -> Objects.equals(member.userId(), userId))
                .map(SpaceMember::role)
                .findFirst()
                .orElse("member");
    }

    private boolean isSpaceAdmin(Long userId, Long spaceId) {
        String role = currentMemberRole(spaceId, userId);
        return "owner".equals(role) || "admin".equals(role);
    }

    private void assertSpaceAdmin(Long userId, Long spaceId) {
        if (!isSpaceAdmin(userId, spaceId)) {
            throw new BusinessException(403, "只有空间管理员可以管理分类和服务");
        }
    }

    private String roleText(String role) {
        return switch (role) {
            case "owner" -> "空间管理员";
            case "admin" -> "管理员";
            case "partner" -> "空间成员";
            default -> "空间成员";
        };
    }

    private long nextId() {
        return idGenerator.nextId();
    }

    private void evictQueryCaches() {
        queryCacheInvalidator.evictAllAfterCommit();
    }

    private String string(Map<String, Object> body, String key, String fallback) {
        Object value = body == null ? null : body.get(key);
        return value == null || value.toString().isBlank() ? fallback : value.toString();
    }

    private String value(Map<String, Object> body, String key, String fallback) {
        Object value = body == null ? null : body.get(key);
        return value == null ? fallback : value.toString();
    }

    private Long longValue(Map<String, Object> body, String key, Long fallback) {
        Object value = body == null ? null : body.get(key);
        if (value == null || value.toString().isBlank()) {
            return fallback;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(value.toString());
    }

    private int intValue(Map<String, Object> body, String key, int fallback) {
        Object value = body == null ? null : body.get(key);
        if (value == null || value.toString().isBlank()) {
            return fallback;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(value.toString());
    }

    private boolean boolValue(Map<String, Object> body, String key, boolean fallback) {
        Object value = body == null ? null : body.get(key);
        if (value == null || value.toString().isBlank()) {
            return fallback;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        return Boolean.parseBoolean(value.toString());
    }

    private LocalDateTime dateTime(Map<String, Object> body, String key, LocalDateTime fallback) {
        Object value = body == null ? null : body.get(key);
        if (value == null || value.toString().isBlank()) {
            return fallback;
        }
        return LocalDateTime.parse(value.toString());
    }

    private LocalDate date(Map<String, Object> body, String key, LocalDate fallback) {
        Object value = body == null ? null : body.get(key);
        if (value == null || value.toString().isBlank()) {
            return fallback;
        }
        return LocalDate.parse(value.toString());
    }
}
