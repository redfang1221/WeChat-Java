package com.chinatelecom.template.utils;


import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class CommonUtil {

    public String fileNameConvertor(String fileName, HttpServletRequest request) throws UnsupportedEncodingException {
        String agent = request.getHeader("User-Agent").toLowerCase();
        if (agent != null && (agent.indexOf("msie") != -1 ||
                (agent.indexOf("rv") != -1 && agent.indexOf("firefox") == -1))) {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } else {
            fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
        }
        return fileName;
    }

    public static String getWebappNo(boolean isEntry){
        Calendar calendar = Calendar.getInstance();
        UUID id=UUID.randomUUID();
        String[] idd=id.toString().split("-");
        String header = "E";
        if(!isEntry) header = "O";
        return header + calendar.get(Calendar.YEAR)+calendar.get((Calendar.MONTH)+1)+calendar.get(Calendar.DATE)+idd[1];
    }

    public static String getCurrentTime() {
        Date d=new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String time=format.format(d);
        return time;
    }

    public static String compareTime(String before, String later) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date b = format.parse(before);
        Date l = format.parse(later);
        long between = l.getTime() - b.getTime();
        return Long.toString(between);
    }

    public static boolean isTokenExpired(String interval, String before) throws Exception {
        long med = Long.parseLong(interval);
        String after = getCurrentTime();
        if(Long.parseLong(compareTime(before, after)) >= med) {
            return true;
        }
        return false;
    }

}
