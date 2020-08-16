package com.chinatelecom.template.server.action;

import com.alibaba.fastjson.JSON;
import com.chinatelecom.template.config.annotation.Access;
import com.chinatelecom.template.server.service.TestService;
import com.chinatelecom.template.utils.ExcelUtil;
import com.chinatelecom.template.utils.PdfExportUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.mapping.ResultMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping(value = "/test")
@RestController
@Api(tags = {"测试案例"})
public class TestController {

    @Autowired
    TestService testService;

//    @Access(login = false, privilege = false)
    @ApiOperation(value = "downloadExcel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @RequestMapping(value = "/downloadExcel", method = RequestMethod.GET)
    public Map<String,Object> downloadExcelAction(HttpServletResponse response, HttpServletRequest request) {
        String fileName = "下载1";
        List<Map<String,Object>> list = testService.queryAllData();
        ExcelUtil.downloadExcelFile(list, response, request, fileName);
        return null;
    }

    //    @Access(login = false, privilege = false)
    @ApiOperation(value = "downloadPdf", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @RequestMapping(value = "/downloadPdf", method = RequestMethod.GET)
    public Map<String,Object> downloadPdfAction(HttpServletResponse response, HttpServletRequest request) {
        String templateUrl = "D:/desktop/template1.pdf";
        String fileName = "下载2";
        Map<String,Object> data = new HashMap<>();
//        复选框
        data.put("sx","Yes");
        data.put("fw1","Yes");
        data.put("zj1","Yes");
        data.put("csjd1","Yes");
        data.put("cszl1","Yes");
        data.put("cssf1","Yes");
        data.put("zlmy1","Yes");
//        内容填写
        data.put("wtdbh","123@email.com");
        data.put("fwnrms","描述");
        int res = PdfExportUtil.writePdf(templateUrl,fileName,data,response);
        return null;
    }

    @ApiOperation(value = "miniProgTest1",produces = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value="/miniProgTest1",method = RequestMethod.GET)
    public String miniProgTest1Action(HttpServletResponse response, HttpServletRequest request){
        List<Map<String,Object>> list = new ArrayList<>();
        for(int i = 1; i <= 5; i++) {
            Map<String,Object> map = new HashMap<>();
            map.put("id","" + i);
            map.put("name","Dave" + i);
            list.add(map);
        }
        String res = JSON.toJSONString(list);
        return res;
    }


}
