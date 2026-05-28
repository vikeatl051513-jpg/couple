package com.example.couplebackend.mapper;

import com.example.couplebackend.entity.SpaceMember;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SpaceMemberMapper {
    @Select("select id, space_id, user_id, role, display_name, status from space_member where space_id = #{spaceId} and status = 'active' and deleted = 0 order by id asc")
    List<SpaceMember> selectActiveBySpaceId(Long spaceId);

    @Select("select id, space_id, user_id, role, display_name, status from space_member where user_id = #{userId} and status = 'active' and deleted = 0 order by id asc")
    List<SpaceMember> selectActiveByUserId(Long userId);

    @Select("select id, space_id, user_id, role, display_name, status from space_member where space_id = #{spaceId} and user_id = #{userId} and status = 'active' and deleted = 0 limit 1")
    SpaceMember selectActiveBySpaceIdAndUserId(@Param("spaceId") Long spaceId, @Param("userId") Long userId);

    @Select("select count(1) from space_member where space_id = #{spaceId} and user_id = #{userId} and deleted = 0")
    int countBySpaceIdAndUserId(@Param("spaceId") Long spaceId, @Param("userId") Long userId);

    @Insert("""
            insert into space_member(id, space_id, user_id, role, display_name, status)
            values(#{id}, #{spaceId}, #{userId}, #{role}, #{displayName}, #{status})
            """)
    int insert(SpaceMember member);

    @Update("update space_member set role = #{role}, updated_at = now() where space_id = #{spaceId} and user_id = #{userId} and deleted = 0")
    int updateRole(@Param("spaceId") Long spaceId, @Param("userId") Long userId, @Param("role") String role);
}
