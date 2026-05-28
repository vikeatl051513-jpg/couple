package com.example.couplebackend.mapper;

import com.example.couplebackend.entity.ServiceItem;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ServiceItemMapper {
    @Select("select id, space_id, category_id, name, description, image_url, tag, point_cost, daily_limit, cooldown_minutes, require_remark, require_appoint_time, sort_order, status, monthly_sales, default_assignee_id from service_item where space_id = #{spaceId} and deleted = 0 order by sort_order asc, id asc")
    List<ServiceItem> selectBySpaceId(Long spaceId);

    @Select("select id, space_id, category_id, name, description, image_url, tag, point_cost, daily_limit, cooldown_minutes, require_remark, require_appoint_time, sort_order, status, monthly_sales, default_assignee_id from service_item where id = #{id} and deleted = 0")
    ServiceItem selectById(Long id);

    @Insert("""
            insert into service_item(id, space_id, category_id, name, description, image_url, tag, point_cost, daily_limit, cooldown_minutes, require_remark, require_appoint_time, sort_order, status, monthly_sales, default_assignee_id)
            values(#{id}, #{spaceId}, #{categoryId}, #{name}, #{description}, #{imageUrl}, #{tag}, #{pointCost}, #{dailyLimit}, #{cooldownMinutes}, #{requireRemark}, #{requireAppointTime}, #{sortOrder}, #{status}, #{monthlySales}, #{defaultAssigneeId})
            """)
    int insert(ServiceItem item);

    @Update("""
            update service_item
            set category_id = #{categoryId}, name = #{name}, description = #{description}, image_url = #{imageUrl}, tag = #{tag},
                point_cost = #{pointCost}, daily_limit = #{dailyLimit}, cooldown_minutes = #{cooldownMinutes},
                require_remark = #{requireRemark}, require_appoint_time = #{requireAppointTime},
                sort_order = #{sortOrder}, status = #{status}, default_assignee_id = #{defaultAssigneeId}, updated_at = now()
            where id = #{id}
            """)
    int update(ServiceItem item);

    @Update("update service_item set monthly_sales = monthly_sales + 1, updated_at = now() where id = #{id}")
    int increaseMonthlySales(Long id);

    @Update("""
            update service_item
            set default_assignee_id = #{newAssigneeId}, updated_at = now()
            where space_id = #{spaceId}
              and deleted = 0
              and (default_assignee_id = #{oldAssigneeId} or default_assignee_id is null)
            """)
    int transferDefaultAssignee(@Param("spaceId") Long spaceId,
                                @Param("oldAssigneeId") Long oldAssigneeId,
                                @Param("newAssigneeId") Long newAssigneeId);

    @Delete("update service_item set deleted = 1, updated_at = now() where id = #{id}")
    int logicDelete(Long id);

    @Update("update service_item set deleted = 1, updated_at = now() where category_id = #{categoryId}")
    int logicDeleteByCategoryId(Long categoryId);
}
