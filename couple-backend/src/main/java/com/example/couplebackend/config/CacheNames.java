package com.example.couplebackend.config;

import java.util.List;

public final class CacheNames {
    public static final String WX_BOOTSTRAP = "wxBootstrap";
    public static final String SPACE_MENU = "spaceMenu";
    public static final String SERVICE_CATEGORIES = "serviceCategories";
    public static final String SERVICE_ITEMS = "serviceItems";
    public static final String SERVICE_ITEM_VIEWS = "serviceItemViews";
    public static final String SERVICE_ORDERS = "serviceOrders";
    public static final String TODOS = "todos";
    public static final String TODO_VIEWS = "todoViews";
    public static final String MOOD_OPTIONS = "moodOptions";
    public static final String QUICK_SIGNALS = "quickSignals";
    public static final String MOODS = "moods";
    public static final String CURRENT_MOOD = "currentMood";
    public static final String DRESS_ITEMS = "dressItems";
    public static final String ACTIVE_DRESS = "activeDress";
    public static final String OWNED_DRESS_IDS = "ownedDressIds";
    public static final String CHECKED_IN_TODAY = "checkedInToday";
    public static final String WISHES = "wishes";
    public static final String ANNIVERSARIES = "anniversaries";
    public static final String MESSAGES = "messages";
    public static final String PRODUCTS = "products";
    public static final String ACTIVITIES = "activities";
    public static final String TEMPLATES = "templates";
    public static final String ADMIN_DASHBOARD = "adminDashboard";
    public static final String SPACE_STATS = "spaceStats";
    public static final String MEMBERS = "members";
    public static final String MEMBER_USERS = "memberUsers";

    public static final List<String> ALL = List.of(
            WX_BOOTSTRAP,
            SPACE_MENU,
            SERVICE_CATEGORIES,
            SERVICE_ITEMS,
            SERVICE_ITEM_VIEWS,
            SERVICE_ORDERS,
            TODOS,
            TODO_VIEWS,
            MOOD_OPTIONS,
            QUICK_SIGNALS,
            MOODS,
            CURRENT_MOOD,
            DRESS_ITEMS,
            ACTIVE_DRESS,
            OWNED_DRESS_IDS,
            CHECKED_IN_TODAY,
            WISHES,
            ANNIVERSARIES,
            MESSAGES,
            PRODUCTS,
            ACTIVITIES,
            TEMPLATES,
            ADMIN_DASHBOARD,
            SPACE_STATS,
            MEMBERS,
            MEMBER_USERS
    );

    private CacheNames() {
    }
}
