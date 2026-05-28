package com.example.couplebackend.mapper;

import com.example.couplebackend.entity.TemplateItem;
import java.util.List;
import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ServiceTemplateItemMapper {
    @ConstructorArgs({
            @Arg(column = "category_name", javaType = String.class),
            @Arg(column = "service_name", javaType = String.class),
            @Arg(column = "description", javaType = String.class)
    })
    @Select("""
            select category_name, service_name, description
            from service_template_item
            where template_id = #{templateId}
            order by sort_order asc, id asc
            """)
    List<TemplateItem> selectByTemplateId(Long templateId);
}
