package com.example.couplebackend.mapper;

import com.example.couplebackend.entity.WishItem;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface WishItemMapper {
    @Select("select id, space_id, creator_id, claimed_by, title, description, type, status from wish_item where space_id = #{spaceId} and deleted = 0 order by id desc")
    List<WishItem> selectBySpaceId(Long spaceId);

    @Select("select id, space_id, creator_id, claimed_by, title, description, type, status from wish_item where id = #{id} and deleted = 0")
    WishItem selectById(Long id);

    @Insert("""
            insert into wish_item(id, space_id, creator_id, claimed_by, title, description, type, status)
            values(#{id}, #{spaceId}, #{creatorId}, #{claimedBy}, #{title}, #{description}, #{type}, #{status})
            """)
    int insert(WishItem wish);

    @Update("update wish_item set claimed_by = #{claimedBy}, status = #{status}, updated_at = now() where id = #{id}")
    int updateClaim(WishItem wish);
}
