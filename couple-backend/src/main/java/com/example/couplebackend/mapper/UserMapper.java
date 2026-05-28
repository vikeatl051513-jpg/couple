package com.example.couplebackend.mapper;

import com.example.couplebackend.entity.User;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    @Select("select id, openid, nickname, title, avatar_url, points, contribution, status from `user` where deleted = 0 order by id desc")
    List<User> selectAll();

    @Select("select id, openid, nickname, title, avatar_url, points, contribution, status from `user` where id = #{id} and deleted = 0")
    User selectById(Long id);

    @Select("select id, openid, nickname, title, avatar_url, points, contribution, status from `user` where openid = #{openid} and deleted = 0 limit 1")
    User selectByOpenid(String openid);

    @Select("select id, openid, nickname, title, avatar_url, points, contribution, status from `user` where nickname = #{nickname} and deleted = 0 limit 1")
    User selectByNickname(String nickname);

    @Insert("""
            insert into `user`(id, openid, nickname, title, avatar_url, points, contribution, status)
            values(#{id}, #{openid}, #{nickname}, #{title}, #{avatarUrl}, #{points}, #{contribution}, #{status})
            """)
    int insert(User user);

    @Update("update `user` set points = #{points}, contribution = #{contribution}, updated_at = now() where id = #{id}")
    int updateScore(User user);

    @Update("update `user` set nickname = #{nickname}, title = #{title}, avatar_url = #{avatarUrl}, updated_at = now() where id = #{id}")
    int updateProfile(User user);
}
