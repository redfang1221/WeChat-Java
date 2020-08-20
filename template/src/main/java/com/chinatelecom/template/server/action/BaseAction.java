package com.chinatelecom.template.server.action;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class BaseAction {

    protected HttpServletRequest httpRequest;
    protected HttpServletResponse httpResponse;
    protected HttpSession httpSession;

    @ModelAttribute
    public void setReqAndRes(HttpServletRequest request, HttpServletResponse response) {
        this.httpRequest = request;
        this.httpResponse = response;
        this.httpSession = request.getSession();
    }


    public HttpServletRequest getHttpRequest() {
        return httpRequest;
    }

    public HttpServletResponse getHttpResponse() {
        return httpResponse;
    }

    public HttpSession getHttpSession() {
        return httpSession;
    }

    public String getSessionId() {
        System.out.println(httpRequest.getHeader("Cookie"));
        return httpRequest.getHeader("Cookie");
    }

    public JSONObject getParameters() {
        JSONObject data = new JSONObject();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(httpRequest.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String s = null;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            data = JSONObject.parseObject(sb.toString());
            return data;
        } catch (Exception e) {
            return null;
        }
    }

}
