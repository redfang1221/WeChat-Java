package com.chinatelecom.template.server.service.impl;

import com.chinatelecom.template.server.mapper.TestMapper;
import com.chinatelecom.template.server.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    TestMapper testMapper;

    public List<Map<String,Object>> queryAllData() {
        return testMapper.queryAllData();
    }

    @Override
    public int insertData(Map<String, Object> map) {
        return testMapper.insertData(map);
    }

}
