package com.example.couplebackend.mapper;

import com.example.couplebackend.entity.SpaceInvite;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SpaceInviteMapper {
    @Select("""
            select id, space_id, invite_code, created_by, expire_at, max_use_count, used_count, status
            from space_invite
            where space_id = #{spaceId}
              and created_by = #{createdBy}
              and status = 'active'
              and used_count < max_use_count
              and (expire_at is null or expire_at > now())
            order by created_at desc
            limit 1
            """)
    SpaceInvite selectReusable(@Param("spaceId") Long spaceId, @Param("createdBy") Long createdBy);

    @Select("""
            select id, space_id, invite_code, created_by, expire_at, max_use_count, used_count, status
            from space_invite
            where invite_code = #{inviteCode}
            limit 1
            """)
    SpaceInvite selectByCode(String inviteCode);

    @Insert("""
            insert into space_invite(id, space_id, invite_code, created_by, expire_at, max_use_count, used_count, status)
            values(#{id}, #{spaceId}, #{inviteCode}, #{createdBy}, #{expireAt}, #{maxUseCount}, #{usedCount}, #{status})
            """)
    int insert(SpaceInvite invite);

    @Update("""
            update space_invite
            set used_count = used_count + 1,
                status = if(used_count + 1 >= max_use_count, 'used', status)
            where id = #{id}
            """)
    int increaseUsed(Long id);
}
