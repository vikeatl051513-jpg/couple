package com.example.couplebackend.mapper;

import com.example.couplebackend.entity.ServiceCategory;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ServiceCategoryMapper {
    @Select("select id, space_id, name, description, icon_url, sort_order, visible from service_category where space_id = #{spaceId} and deleted = 0 order by sort_order asc, id asc")
    List<ServiceCategory> selectBySpaceId(Long spaceId);

    @Select("select id, space_id, name, description, icon_url, sort_order, visible from service_category where id = #{id} and deleted = 0")
    ServiceCategory selectById(Long id);

    @Insert("""
            insert into service_category(id, space_id, name, description, icon_url, sort_order, visible)
            values(#{id}, #{spaceId}, #{name}, #{description}, #{iconUrl}, #{sortOrder}, #{visible})
            """)
    int insert(ServiceCategory category);

    @Update("""
            update service_category
            set name = #{name}, description = #{description}, icon_url = #{iconUrl}, sort_order = #{sortOrder}, visible = #{visible}, updated_at = now()
            where id = #{id}
            """)
    int update(ServiceCategory category);

    @Delete("update service_category set deleted = 1, updated_at = now() where id = #{id}")
    int logicDelete(Long id);
}
