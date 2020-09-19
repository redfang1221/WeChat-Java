package com.chinatelecom.template.server.action;

import com.chinatelecom.template.server.service.WeappService;
import com.chinatelecom.template.utils.ExcelUtil;
import com.chinatelecom.template.utils.PdfExportUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping(value = "/manage")
@RestController
@Api(tags = {"后台管理"})
public class ManageController {

    @Autowired
    WeappService weappService;

    @ApiOperation(value = "downloadEntryInfo", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @RequestMapping(value = "/downloadEntryInfo", method = RequestMethod.GET)
    @ApiImplicitParams({@ApiImplicitParam(name = "year", value = "年份", required = true,dataType = "String", paramType = "query",defaultValue = "2020"),
            @ApiImplicitParam(name = "month", value = "月份", required = true,dataType = "String", paramType = "query",defaultValue = "09"),
            @ApiImplicitParam(name = "day", value = "日期", required = true,dataType = "String", paramType = "query",defaultValue = "03")})
    public Map<String,Object> downloadEntryInfoAction(HttpServletResponse response, HttpServletRequest request) {
        String year = request.getParameter("year");
        String month = request.getParameter("month");
        String day = request.getParameter("day");
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String sdate = formatter.format(currentTime);

        String fileName = sdate+"进场单";
        List<Map<String,Object>> list = weappService.queryEntryData(year,month,day);
        Map<String,Object> map = new HashMap<>();
        if(list.isEmpty()) {
            map.put("success",false);
            map.put("msg","索引内容为空");
            return map;
        }
        ExcelUtil.downloadExcelFile(list, response, request, fileName);
        map.put("success",true);
        return map;
    }

    @ApiOperation(value = "downloadPdf", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @RequestMapping(value = "/downloadPdf", method = RequestMethod.GET)
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "id", required = true,dataType = "String", paramType = "query",defaultValue = "8ad237feaacd4cc1b471c7eaa13f0d6b")})
    public String downloadPdfAction(HttpServletResponse response, HttpServletRequest request) {
        String id = request.getParameter("id");
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String sdate = formatter.format(currentTime);

        String fileName = sdate+"进场单";
        Map<String,Object> map = weappService.downloadPdfById(id);
        String templateUrl = "D:/desktop/1.pdf";
        PdfExportUtil.writePdf(templateUrl,fileName,map,response);
        return null;
    }

}
