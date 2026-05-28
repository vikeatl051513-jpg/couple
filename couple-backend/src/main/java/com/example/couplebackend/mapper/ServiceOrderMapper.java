package com.example.couplebackend.mapper;

import com.example.couplebackend.entity.ServiceOrder;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ServiceOrderMapper {
    @Select("select id, order_no, space_id, service_item_id, requester_id, assignee_id, status, remark, appoint_time, created_at, accepted_at, completed_at, canceled_at from service_order where space_id = #{spaceId} and deleted = 0 order by created_at desc")
    List<ServiceOrder> selectBySpaceId(Long spaceId);

    @Select("select id, order_no, space_id, service_item_id, requester_id, assignee_id, status, remark, appoint_time, created_at, accepted_at, completed_at, canceled_at from service_order where id = #{id} and deleted = 0")
    ServiceOrder selectById(Long id);

    @Insert("""
            insert into service_order(id, order_no, space_id, service_item_id, requester_id, assignee_id, status, remark, appoint_time, created_at, accepted_at, completed_at, canceled_at)
            values(#{id}, #{orderNo}, #{spaceId}, #{serviceItemId}, #{requesterId}, #{assigneeId}, #{status}, #{remark}, #{appointTime}, #{createdAt}, #{acceptedAt}, #{completedAt}, #{canceledAt})
            """)
    int insert(ServiceOrder order);

    @Update("""
            update service_order
            set status = #{status}, accepted_at = #{acceptedAt}, completed_at = #{completedAt}, canceled_at = #{canceledAt}, updated_at = now()
            where id = #{id}
            """)
    int updateStatus(ServiceOrder order);
}
