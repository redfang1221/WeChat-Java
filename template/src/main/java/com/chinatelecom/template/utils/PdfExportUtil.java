package com.chinatelecom.template.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PdfExportUtil {

    public static int writePdf(String templateUrl, String fileName, Map<String,Object> data, HttpServletResponse response) {
        try {
            if(fileName != null && !fileName.endsWith(".pdf")){
                fileName = fileName + ".pdf";
            }
            fileName = new String(fileName.getBytes("UTF-8"),"iso-8859-1");

            response.reset();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            OutputStream out = response.getOutputStream();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            PdfReader pdfReader = new PdfReader(templateUrl);
            PdfStamper pdfStamper = new PdfStamper(pdfReader, bos);
            AcroFields form = pdfStamper.getAcroFields();

            Iterator<String> it = form.getFields().keySet().iterator();
            while(it.hasNext()){
                String name = it.next();
                if(StringUtils.isNotBlank(name) && data.containsKey(name)) {
                    form.setField(name, MapUtils.getString(data,name));
                }
            }
            pdfStamper.setFormFlattening(true);
            pdfStamper.close();

            Document document = new Document();
            PdfCopy copy = new PdfCopy(document,out);
            document.open();

            PdfReader reader = new PdfReader(bos.toByteArray());
            copy.addDocument(reader);
            copy.freeReader(reader);

            document.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            return -1;
        }
        return 1;
    }

    public static int writePdf2Zip(String templateUrl, String fileName, List<Map<String,Object>> list, HttpServletResponse response) {
        int i = 0;
        try {
            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String sdate = formatter.format(currentTime);
            String zipName = sdate+fileName;
            if(zipName != null && !zipName.endsWith(".zip")){
                zipName = zipName + ".zip";
            }
            zipName = new String(zipName.getBytes("UTF-8"),"iso-8859-1");

            response.reset();
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment;filename=" + zipName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            OutputStream os = response.getOutputStream();
            ZipOutputStream zip = new ZipOutputStream(os);

            for(i = 0; i<list.size(); i++) {
                String tempName = i+".pdf";
                Map<String,Object> data = list.get(i);
                ZipEntry entry = new ZipEntry(tempName);
                zip.putNextEntry(entry);

                ByteArrayOutputStream fos = new ByteArrayOutputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                PdfReader pdfReader = new PdfReader(templateUrl);
                PdfStamper pdfStamper = new PdfStamper(pdfReader, bos);
                AcroFields form = pdfStamper.getAcroFields();
                Iterator<String> it = form.getFields().keySet().iterator();
                while(it.hasNext()){
                    String name = it.next();
                    if(StringUtils.isNotBlank(name) && data.containsKey(name)) {
                        form.setField(name, MapUtils.getString(data,name));
                    }
                }
                pdfStamper.setFormFlattening(true);
                pdfStamper.close();
                Document document = new Document();
                PdfCopy copy = new PdfCopy(document,fos);
                document.open();
                PdfReader reader = new PdfReader(bos.toByteArray());
                copy.addDocument(reader);
                copy.freeReader(reader);
                document.close();

                zip.write(fos.toByteArray());
                zip.closeEntry();
            }
            zip.close();
        } catch (Exception e) {
            System.out.println(i);
            return -1;
        }
        return 1;
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
