package com.example.couplebackend.mapper;

import com.example.couplebackend.entity.Anniversary;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AnniversaryMapper {
    @Select("select id, space_id, name, date, important from anniversary where space_id = #{spaceId} and deleted = 0 order by date asc")
    List<Anniversary> selectBySpaceId(Long spaceId);

    @Select("select id, space_id, name, date, important from anniversary where id = #{id} and deleted = 0")
    Anniversary selectById(Long id);

    @Insert("insert into anniversary(id, space_id, name, date, important) values(#{id}, #{spaceId}, #{name}, #{date}, #{important})")
    int insert(Anniversary anniversary);

    @Update("update anniversary set name = #{name}, date = #{date}, important = #{important}, updated_at = now() where id = #{id} and deleted = 0")
    int update(Anniversary anniversary);

    @Update("update anniversary set deleted = 1, updated_at = now() where id = #{id}")
    int logicDelete(Long id);
}
