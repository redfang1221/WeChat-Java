package com.chinatelecom.template.server.service;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

public interface TestService {

    List<Map<String,Object>> queryAllData();

    int insertData(Map<String,Object> map);

}
