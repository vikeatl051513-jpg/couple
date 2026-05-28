package com.example.couplebackend.mapper;

import com.example.couplebackend.entity.SpaceDress;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SpaceDressMapper {
    @Select("select id, space_id, dress_item_id, obtained_by, obtain_source, active from space_dress where space_id = #{spaceId} order by id asc")
    List<SpaceDress> selectBySpaceId(Long spaceId);

    @Insert("""
            insert ignore into space_dress(id, space_id, dress_item_id, obtained_by, obtain_source, active)
            values(#{id}, #{spaceId}, #{dressItemId}, #{obtainedBy}, #{obtainSource}, #{active})
            """)
    int insertIgnore(SpaceDress spaceDress);

    @Update("update space_dress set active = 0 where space_id = #{spaceId}")
    int deactivateBySpaceId(Long spaceId);

    @Update("update space_dress set active = 1 where space_id = #{spaceId} and dress_item_id = #{dressItemId}")
    int activate(@Param("spaceId") Long spaceId, @Param("dressItemId") Long dressItemId);
}
