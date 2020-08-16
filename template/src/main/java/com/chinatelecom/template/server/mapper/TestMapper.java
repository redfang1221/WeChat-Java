package com.chinatelecom.template.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
public interface TestMapper {

    List<Map<String,Object>> queryAllData();

    int insertData(@Param("map") Map<String, Object> map);
}
