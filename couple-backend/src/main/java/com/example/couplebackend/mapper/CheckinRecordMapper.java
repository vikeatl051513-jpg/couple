package com.example.couplebackend.mapper;

import com.example.couplebackend.entity.CheckinRecord;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CheckinRecordMapper {
    @Select("select id, space_id, user_id, checkin_date, continuous_days, reward_point, reward_dress_id from checkin_record where space_id = #{spaceId} and user_id = #{userId} and checkin_date = #{date} limit 1")
    CheckinRecord selectByDay(@Param("spaceId") Long spaceId, @Param("userId") Long userId, @Param("date") LocalDate date);

    @Select("select id, space_id, user_id, checkin_date, continuous_days, reward_point, reward_dress_id from checkin_record where space_id = #{spaceId} and user_id = #{userId} order by checkin_date desc")
    List<CheckinRecord> selectByUser(@Param("spaceId") Long spaceId, @Param("userId") Long userId);

    @Insert("""
            insert into checkin_record(id, space_id, user_id, checkin_date, continuous_days, reward_point, reward_dress_id)
            values(#{id}, #{spaceId}, #{userId}, #{checkinDate}, #{continuousDays}, #{rewardPoint}, #{rewardDressId})
            """)
    int insert(CheckinRecord record);
}
