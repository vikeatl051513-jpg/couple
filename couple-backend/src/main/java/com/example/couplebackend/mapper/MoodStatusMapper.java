package com.example.couplebackend.mapper;

import com.example.couplebackend.entity.MoodStatus;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MoodStatusMapper {
    @Select("select id, space_id, user_id, mood_key, mood_label, note, updated_at from mood_status where space_id = #{spaceId} order by updated_at desc")
    List<MoodStatus> selectBySpaceId(Long spaceId);

    @Select("select id, space_id, user_id, mood_key, mood_label, note, updated_at from mood_status where space_id = #{spaceId} and user_id = #{userId} limit 1")
    MoodStatus selectBySpaceIdAndUserId(@Param("spaceId") Long spaceId, @Param("userId") Long userId);

    @Insert("""
            insert into mood_status(id, space_id, user_id, mood_key, mood_label, note, updated_at)
            values(#{id}, #{spaceId}, #{userId}, #{moodKey}, #{moodLabel}, #{note}, #{updatedAt})
            on duplicate key update mood_key = values(mood_key), mood_label = values(mood_label), note = values(note), updated_at = values(updated_at)
            """)
    int upsert(MoodStatus mood);
}
