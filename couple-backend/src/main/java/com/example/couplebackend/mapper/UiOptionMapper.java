package com.example.couplebackend.mapper;

import com.example.couplebackend.entity.UiOption;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UiOptionMapper {
    @Select("""
            select id, group_key, option_key, label, icon, description, sort_order, status
            from ui_option
            where group_key = #{groupKey} and status = 'active'
            order by sort_order asc, id asc
            """)
    List<UiOption> selectByGroup(String groupKey);

    @Select("""
            select id, group_key, option_key, label, icon, description, sort_order, status
            from ui_option
            where group_key = #{groupKey} and option_key = #{optionKey} and status = 'active'
            limit 1
            """)
    UiOption selectByGroupAndKey(@Param("groupKey") String groupKey, @Param("optionKey") String optionKey);
}
