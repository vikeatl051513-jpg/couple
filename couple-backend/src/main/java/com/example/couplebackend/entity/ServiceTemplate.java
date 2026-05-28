package com.example.couplebackend.entity;

import java.util.ArrayList;
import java.util.List;

public class ServiceTemplate {
    public Long id;
    public String name;
    public String description;
    public String scene;
    public List<TemplateItem> items = new ArrayList<>();

    public ServiceTemplate(Long id, String name, String description, String scene) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.scene = scene;
    }
}
