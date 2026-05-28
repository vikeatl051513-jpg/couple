package com.example.couplebackend.mapper;

import com.example.couplebackend.entity.PaymentOrder;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PaymentOrderMapper {
    @Select("select id, pay_no, user_id, space_id, product_type, product_id, amount_cent, status, paid_at from payment_order order by id desc")
    List<PaymentOrder> selectAll();

    @Select("select id, pay_no, user_id, space_id, product_type, product_id, amount_cent, status, paid_at from payment_order where space_id = #{spaceId} order by id desc")
    List<PaymentOrder> selectBySpaceId(Long spaceId);

    @Insert("""
            insert into payment_order(id, pay_no, user_id, space_id, product_type, product_id, amount_cent, status, paid_at)
            values(#{id}, #{payNo}, #{userId}, #{spaceId}, #{productType}, #{productId}, #{amountCent}, #{status}, #{paidAt})
            """)
    int insert(PaymentOrder paymentOrder);
}
