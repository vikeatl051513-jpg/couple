package com.example.couplebackend.mapper;

import com.example.couplebackend.entity.DressItem;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface DressItemMapper {
    @Select("select id, name, type, obtain_type, price_cent, point_price, rarity, status, json_unquote(json_extract(config_json, '$.background')) background, json_unquote(json_extract(config_json, '$.primaryColor')) primary_color, json_unquote(json_extract(config_json, '$.cardColor')) card_color, json_unquote(json_extract(config_json, '$.textColor')) text_color from dress_item where deleted = 0 order by id desc")
    List<DressItem> selectAll();

    @Select("select id, name, type, obtain_type, price_cent, point_price, rarity, status, json_unquote(json_extract(config_json, '$.background')) background, json_unquote(json_extract(config_json, '$.primaryColor')) primary_color, json_unquote(json_extract(config_json, '$.cardColor')) card_color, json_unquote(json_extract(config_json, '$.textColor')) text_color from dress_item where id = #{id} and deleted = 0")
    DressItem selectById(Long id);

    @Insert("""
            insert into dress_item(id, name, type, obtain_type, price_cent, point_price, rarity, status, config_json)
            values(#{id}, #{name}, #{type}, #{obtainType}, #{priceCent}, #{pointPrice}, #{rarity}, #{status},
                   json_object('background', #{background}, 'primaryColor', #{primaryColor}, 'cardColor', #{cardColor}, 'textColor', #{textColor}))
            """)
    int insert(DressItem item);

    @Update("""
            update dress_item
            set name = #{name}, type = #{type}, obtain_type = #{obtainType}, price_cent = #{priceCent}, point_price = #{pointPrice},
                rarity = #{rarity}, status = #{status},
                config_json = json_object('background', #{background}, 'primaryColor', #{primaryColor}, 'cardColor', #{cardColor}, 'textColor', #{textColor}),
                updated_at = now()
            where id = #{id}
            """)
    int update(DressItem item);
}
