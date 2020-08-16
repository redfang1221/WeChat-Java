package com.chinatelecom.template.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;


public class ExcelUtil {

//    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtil.class);

    public static int uploadExcelFile(MultipartFile file, HttpServletRequest request, List<String> headers, Method method, Object bean, int cellLength) {
        JSONObject params=new JSONObject();
        String filePath = request.getSession().getServletContext().getRealPath("/");
        File tempFile = new File(filePath+file.getOriginalFilename());
        try {
            file.transferTo(tempFile);
            List<Map<Integer,String>> dataList = ExcelImport.read(tempFile,cellLength);
            for (int i = 0; i < dataList.size(); i++) {
                for(int j = 0; j<headers.size(); j++) {
                    params.put(headers.get(j),dataList.get(i).get(j));
                }
                method.invoke(bean, params);
            }
            tempFile.delete();
        } catch (Exception e) {
            tempFile.delete();
            return -1;
        }
        return 1;
    }

    public static void downloadExcelFile(List<Map<String, Object>> list,HttpServletResponse response, HttpServletRequest request, String fileName) {
        String[] title = new String[list.get(0).size()];
        String[][] objects = new String[list.size()][list.get(0).size()];

        int k = 0;
        for(Map.Entry<String,Object> entry : list.get(0).entrySet()) {
            title[k] = entry.getKey();
            k++;
        }

        for(int i = 0; i<list.size(); i++) {
            Map<String,Object> map = list.get(i);
            k = 0;
            for(Map.Entry<String,Object> entry : map.entrySet()) {
                objects[i][k] = (String) entry.getValue();
                k++;
            }
        }

        SXSSFWorkbook wb = ExcelExport.createWorkbook("sheet-1", title, objects);
        try {
            if(fileName != null && !fileName.endsWith(".xlsx")){
                fileName = fileName + ".xlsx";
            }

            fileName = URLEncoder.encode(fileName,"UTF-8");
            if ("FF".equals(getBrowser(request))) {
                fileName = new String(fileName.getBytes("UTF-8"),
                        "iso-8859-1");
            }

            response.reset();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");

            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 以下为服务器端判断客户端浏览器类型的方法
     */
    private static String getBrowser(HttpServletRequest request) {
        String UserAgent = request.getHeader("USER-AGENT").toLowerCase();
        if (UserAgent != null) {
            if (UserAgent.indexOf("msie") >= 0)
                return "IE";
            if (UserAgent.indexOf("firefox") >= 0)
                return "FF";
            if (UserAgent.indexOf("safari") >= 0)
                return "SF";
        }
        return null;
    }
}