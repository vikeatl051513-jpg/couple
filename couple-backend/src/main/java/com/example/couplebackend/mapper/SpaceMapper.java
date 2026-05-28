package com.example.couplebackend.mapper;

import com.example.couplebackend.entity.Space;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SpaceMapper {
    @Select("select id, name, type, owner_user_id, announcement, cover_url, current_dress_id, status, level from space where deleted = 0 order by id desc")
    List<Space> selectAll();

    @Select("select id, name, type, owner_user_id, announcement, cover_url, current_dress_id, status, level from space where id = #{id} and deleted = 0")
    Space selectById(Long id);

    @Select("""
            select s.id, s.name, s.type, s.owner_user_id, s.announcement, s.cover_url, s.current_dress_id, s.status, s.level
            from space s
            join space_member sm on sm.space_id = s.id
            where sm.user_id = #{userId} and sm.status = 'active' and sm.deleted = 0 and s.deleted = 0
            order by sm.id desc
            limit 1
            """)
    Space selectCurrentByUserId(Long userId);

    @Insert("""
            insert into space(id, name, type, owner_user_id, announcement, cover_url, current_dress_id, status, level)
            values(#{id}, #{name}, #{type}, #{ownerUserId}, #{announcement}, #{coverUrl}, #{currentDressId}, #{status}, #{level})
            """)
    int insert(Space space);

    @Update("update space set current_dress_id = #{currentDressId}, updated_at = now() where id = #{id}")
    int updateCurrentDress(Space space);

    @Update("""
            update space
            set name = #{name}, announcement = #{announcement}, cover_url = #{coverUrl}, updated_at = now()
            where id = #{id} and deleted = 0
            """)
    int updateProfile(Space space);

    @Update("update space set owner_user_id = #{ownerUserId}, updated_at = now() where id = #{id} and deleted = 0")
    int updateOwner(Space space);
}
