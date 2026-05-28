package com.example.couplebackend.mapper;

import com.example.couplebackend.entity.TodoItem;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TodoItemMapper {
    @Select("select id, space_id, title, description, creator_id, assignee_id, priority, due_time, status from todo_item where space_id = #{spaceId} and deleted = 0 order by due_time asc, id desc")
    List<TodoItem> selectBySpaceId(Long spaceId);

    @Select("select id, space_id, title, description, creator_id, assignee_id, priority, due_time, status from todo_item where id = #{id} and deleted = 0")
    TodoItem selectById(Long id);

    @Insert("""
            insert into todo_item(id, space_id, title, description, creator_id, assignee_id, priority, due_time, status)
            values(#{id}, #{spaceId}, #{title}, #{description}, #{creatorId}, #{assigneeId}, #{priority}, #{dueTime}, #{status})
            """)
    int insert(TodoItem todo);

    @Update("update todo_item set status = #{status}, completed_at = if(#{status} = 'done', now(), completed_at), updated_at = now() where id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
