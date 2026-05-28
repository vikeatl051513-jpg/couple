package com.example.couplebackend.mapper;

import com.example.couplebackend.entity.Activity;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ActivityMapper {
    @Select("select id, name, type, description, start_date, end_date, reward_point, reward_dress_id, status from activity order by id desc")
    List<Activity> selectAll();

    @Insert("""
            insert into activity(id, name, type, description, start_date, end_date, reward_point, reward_dress_id, status)
            values(#{id}, #{name}, #{type}, #{description}, #{startDate}, #{endDate}, #{rewardPoint}, #{rewardDressId}, #{status})
            """)
    int insert(Activity activity);

    @Update("""
            update activity
            set name = #{name}, type = #{type}, description = #{description}, start_date = #{startDate}, end_date = #{endDate},
                reward_point = #{rewardPoint}, reward_dress_id = #{rewardDressId}, status = #{status}, updated_at = now()
            where id = #{id}
            """)
    int update(Activity activity);
}
