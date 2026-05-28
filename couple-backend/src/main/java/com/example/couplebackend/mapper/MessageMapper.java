package com.example.couplebackend.mapper;

import com.example.couplebackend.entity.Message;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MessageMapper {
    @Select("select id, space_id, receiver_id, sender_id, type, title, content, biz_id, read_status, created_at from message where space_id = #{spaceId} and receiver_id = #{receiverId} order by created_at desc")
    List<Message> selectByReceiver(@Param("spaceId") Long spaceId, @Param("receiverId") Long receiverId);

    @Select("select id, space_id, receiver_id, sender_id, type, title, content, biz_id, read_status, created_at from message where id = #{id}")
    Message selectById(Long id);

    @Insert("""
            insert into message(id, space_id, receiver_id, sender_id, type, title, content, biz_id, read_status, created_at)
            values(#{id}, #{spaceId}, #{receiverId}, #{senderId}, #{type}, #{title}, #{content}, #{bizId}, #{readStatus}, #{createdAt})
            """)
    int insert(Message message);

    @Update("update message set read_status = 1 where id = #{id}")
    int markRead(Long id);
}
