package com.chinatelecom.template;

import com.chinatelecom.template.server.service.TestService;
import com.chinatelecom.template.server.service.impl.TestServiceImpl;
import com.chinatelecom.template.utils.ExcelUtil;
import com.chinatelecom.template.utils.PdfExportUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.util.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class TemplateApplicationTests {

    @Autowired
    TestService testService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * mysql连接查询测试
     */
    @Test
    void test1() {
        List<Map<String,Object>> list = testService.queryAllData();
        for(Map<String, Object> map : list) {
            System.out.println(MapUtils.getString(map,"id") + ": " + MapUtils.getString(map,"name"));
        }
    }

    /**
     * redis使用测试
     */
    @Test
    void test2() {
        Map<String,Object> map = new HashMap<>();
        map.put("name","宝马");
        map.put("enName","BMW");
        //存放数据
        redisTemplate.opsForValue().set("car1", map);
        //取数据
        Map<String,Object> resMap = (Map<String, Object>) redisTemplate.opsForValue().get("car1");
    }

    /**
     * 表格上传测试
     */
    @Test
    void test3() throws IOException, NoSuchMethodException, ClassNotFoundException {
        File file = new File("D:/desktop/test1.xlsx");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile =new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
        HttpServletRequest request = new MockHttpServletRequest();

        Class<?> cls = Class.forName("com.chinatelecom.template.server.service.TestService");
        Object bean = applicationContext.getBean(cls);
        Method method = cls.getMethod("insertData", Map.class);

        System.out.println(ExcelUtil.uploadExcelFile(multipartFile, request, Arrays.asList(new String[]{"id","name","status"}),method,bean,3));
    }

    /**
     * 表格下载测试
     */
    @Test
    void test4() {

        HttpServletResponse response = new MockHttpServletResponse();
        HttpServletRequest request = new MockHttpServletRequest();
        String fileName = "下载1";
//        List<Map<String,Object>> list = testService.queryAllData();
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> map1 = new HashMap<>();
        map1.put("id","1");
        map1.put("name","alan");
        Map<String,Object> map2 = new HashMap<>();
        map2.put("id","2");
        map2.put("name","andrew");
        list.add(map1);
        list.add(map2);

        ExcelUtil.downloadExcelFile(list,response,request,fileName);

    }

    /**
     * 文档下载测试
     */
    @Test
    void test5() {
//        HttpServletResponse response = new MockHttpServletResponse();
//        String templateName = "D:\\desktop\\template1.pdf";
//        String fileName = "download2";
//        Map<String,Object> data = new HashMap<>();
////        复选框
//        data.put("sx","Yes");
//        data.put("fw1","Yes");
//        data.put("zj1","Yes");
//        data.put("csjd1","Yes");
//        data.put("cszl1","Yes");
//        data.put("cssf1","Yes");
//        data.put("zlmy1","Yes");
////        内容填写
//        data.put("fwwtdbh","12345678");
//        data.put("fwnrms","描述");
//        PdfExportUtil.writePdf(templateName,fileName,data,response);
    }

    /**
     * 路由器密码修改测试
     */
    @Test
    void test6() {

    }

}
