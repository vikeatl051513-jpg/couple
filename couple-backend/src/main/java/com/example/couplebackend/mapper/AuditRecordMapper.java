package com.example.couplebackend.mapper;

import com.example.couplebackend.entity.AuditRecord;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AuditRecordMapper {
    @Select("select id, target_type, target_id, status, remark, created_at from audit_record order by id desc")
    List<AuditRecord> selectAll();
}
