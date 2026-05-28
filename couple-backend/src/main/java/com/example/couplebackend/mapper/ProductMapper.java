package com.example.couplebackend.mapper;

import com.example.couplebackend.entity.Product;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductMapper {
    @Select("select id, name, type, target_id, amount_cent, status from product where status = 'active' order by id desc")
    List<Product> selectActive();

    @Select("select id, name, type, target_id, amount_cent, status from product where id = #{id}")
    Product selectById(Long id);
}
