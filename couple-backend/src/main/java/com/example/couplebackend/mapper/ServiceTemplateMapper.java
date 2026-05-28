package com.example.couplebackend.mapper;

import com.example.couplebackend.entity.ServiceTemplate;
import java.util.List;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ServiceTemplateMapper {
    @ConstructorArgs({
            @Arg(column = "id", javaType = Long.class),
            @Arg(column = "name", javaType = String.class),
            @Arg(column = "description", javaType = String.class),
            @Arg(column = "scene", javaType = String.class)
    })
    @Select("""
            select id, name, description, scene
            from service_template
            where status = 'active'
            order by sort_order asc, id asc
            """)
    List<ServiceTemplate> selectActive();
}
