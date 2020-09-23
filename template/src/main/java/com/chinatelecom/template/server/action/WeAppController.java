package com.chinatelecom.template.server.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatelecom.template.server.entity.Constants;
import com.chinatelecom.template.server.entity.TemplateData;
import com.chinatelecom.template.server.entity.WxMssVo;
import com.chinatelecom.template.server.service.WeappService;
import com.chinatelecom.template.utils.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;


@RequestMapping(value = "/weapp")
@RestController
public class WeAppController extends BaseAction {


    @Value("${oauth.wx.appid}")
    private String appid;
    @Value("${oauth.wx.appsecret}")
    private String appsecret;
    @Value("${weapp.token.interval}")
    private String interval;
    @Value("${weapp.token.templateid}")
    private String templateid;

    @Autowired
    private WeappService service;

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(String code){
        HashMap<String,String> result=new HashMap<String, String>();
        HashMap<String,String> paraMap=new HashMap<String, String>();
        HttpServletRequest request = getHttpRequest();
        HttpServletResponse response = getHttpResponse();
        String sessionid = request.getSession().getId();
        HttpSession session = MySessionContext.getSession(sessionid);
        // 防止重复登陆
        if (session != null) {
            String sessionKey = (String) session.getAttribute(Constants.WX_SESSION_KEY);
            String openid = (String) session.getAttribute(Constants.WX_SESSION_OPEN_ID);
            Map<String, Object> resMap = new HashMap<>();
            resMap.put("session_key", sessionKey);
            resMap.put("open_id", openid);
            return JSON.toJSONString(resMap);
        }

        MySessionContext.addSession(request.getSession());

        response.setContentType("application/json");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cookie",sessionid);

        String url="https://api.weixin.qq.com/sns/jscode2session";
        paraMap.put("appid",appid);
        paraMap.put("secret",appsecret);
        paraMap.put("js_code",code);
        paraMap.put("grant_type","authorization_code");

        String wxResult= HttpClientUtil.doPost(url,paraMap);

        JSONObject jsonObject = JSONObject.parseObject(wxResult);
        String session_key = jsonObject.get("session_key").toString();
        String open_id = jsonObject.get("openid").toString();

        result.put("session_key", session_key);
        result.put("open_id", open_id);
//        HttpSession tempSession = getHttpSession();
        HttpSession tempSession =  MySessionContext.getSession(sessionid);
        tempSession.setAttribute(Constants.WX_SESSION_OPEN_ID,open_id);
        tempSession.setAttribute(Constants.WX_SESSION_KEY,session_key);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "/decodePhoneNumber",method = RequestMethod.POST)
    public String decodePhoneNumber() throws Exception {
        JSONObject data = getParameters();
        String encryptedData = data.getString("encryptedData");
        String iv = data.getString("iv");

        String sessionid = getSessionId();
        HttpSession session = MySessionContext.getSession(sessionid);
        String sessionKey = (String) session.getAttribute(Constants.WX_SESSION_KEY);
        String openid = (String) session.getAttribute(Constants.WX_SESSION_OPEN_ID);

        String before = CommonUtil.getCurrentTime();
        while(session == null || sessionKey==null || openid==null) {
            sessionid = getSessionId();
            session = MySessionContext.getSession(sessionid);
            sessionKey = (String) session.getAttribute(Constants.WX_SESSION_KEY);
            openid = (String) session.getAttribute(Constants.WX_SESSION_OPEN_ID);
            String after = CommonUtil.getCurrentTime();
            if(Long.parseLong(CommonUtil.compareTime(before,after)) >= 1000) {
                return "";
            }
        }

        JSONObject info = JSONObject.parseObject(WxUtils.wxDecrypt(encryptedData,sessionKey,iv));
        String phoneNumber = info.getString("phoneNumber");
        String countryCode = info.getString("countryCode");

        Map<String,Object> map = new HashMap<>();
        map.put("phoneNumber",phoneNumber);
        map.put("countryCode",countryCode);

        int flag = service.checkSaved(openid);
        if(flag == 0) {
            service.addNewUserPhone(openid, map);
        } else if(flag > 0) {
            service.updateUserPhone(openid, map);
        }

        System.out.println(info.getString("phoneNumber"));
        return phoneNumber;
    }

    @RequestMapping(value = "/addNewEntryInfo",method = RequestMethod.POST)
    public String addNewEntryInfo() {
        JSONObject data = getParameters();
        String sessionid = getSessionId();
        HttpSession session = MySessionContext.getSession(sessionid);
        String openid = (String) session.getAttribute(Constants.WX_SESSION_OPEN_ID);
        Map<String,Object> map = new HashMap<>();

        String date = data.getString("date");

        map.put("deptName",data.getString("deptName"));
        map.put("projName",data.getString("projName"));
        map.put("contact",data.getString("contact"));
        map.put("tel",data.getString("tel"));
        map.put("email",data.getString("email"));
        map.put("staff",data.getString("staff"));
        map.put("description",data.getString("description"));
        map.put("requirement",data.getString("requirement"));
        map.put("sDate",date.split("//s+")[0]);
        map.put("eDate",date.split("//s+")[1]);
        map.put("currentDate",data.getString("currentDate"));
        map.put("entryManager",data.getString("entryManager"));
        map.put("applicantId",data.getString("applicantId"));
        map.put("officerId",data.getString("officerId"));

        service.addNewEntryInfo(map,data.getString("entryNo"));

        System.out.println(map);
        return "success";
    }

    @RequestMapping(value = "/loginUserInfo",method = RequestMethod.POST)
    public String loginUserInfo() throws Exception {
        JSONObject data = getParameters();
        String sessionid = getSessionId();
        HttpSession session = MySessionContext.getSession(sessionid);
        String before = CommonUtil.getCurrentTime();
        while(session==null) {
            sessionid = getSessionId();
            session = MySessionContext.getSession(sessionid);
            String after = CommonUtil.getCurrentTime();
            if(Long.parseLong(CommonUtil.compareTime(before,after)) >= 1000) {
                return "";
            }
        }
        String openid = (String) session.getAttribute(Constants.WX_SESSION_OPEN_ID);
        Map<String,Object> map = new HashMap<>();

        map.put("nickName",data.getString("nickName"));
        map.put("gender",data.getString("gender"));
        map.put("language",data.getString("language"));
        map.put("city",data.getString("city"));
        map.put("province",data.getString("province"));
        map.put("country",data.getString("country"));
        map.put("avatarUrl",data.getString("avatarUrl"));

        int flag = service.checkSaved(openid);
        Map<String,Object> resultMap = new HashMap<>();

        if(flag > 0) {
            if(service.updateUserInfo(openid,map) != 1) {
                resultMap.put("success",false);
            } else {
                resultMap.put("success",true);
            }
        } else if(flag == -1) {
            resultMap.put("success",false);
        } else {
            if(service.registerNewUserInfo(openid,map) != 1) {
                resultMap.put("success",false);
            } else {
                resultMap.put("success",true);
            }
        }

        System.out.println(map);
        return JSONObject.toJSONString(resultMap);
    }

    @RequestMapping(value = "/getEntryFixedInfo",method = RequestMethod.POST)
    public String getEntryFixedInfo() {
        Map<String,Object> map = new HashMap<>();
        String sessionid = getSessionId();
        HttpSession session = MySessionContext.getSession(sessionid);
        String openid = (String) session.getAttribute(Constants.WX_SESSION_OPEN_ID);
        String entryNo = CommonUtil.getWebappNo(true);

        map = service.queryOfficerInfo();

        String personId = service.getIdByopenId(openid);
//        service.addEntryFixedInfo(managerId,personId,entryNo,entryManager);
        map.put("entryNo",entryNo);
        map.put("applicantId",personId);
        return JSONObject.toJSONString(map);
    }

    @RequestMapping(value = "/queryMyInfo",method = RequestMethod.POST)
    public String queryMyInfo() {
        Map<String,Object> map = new HashMap<>();
//        map.put("avatarUrl","https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLXHP1QPFPsx4F3fguiaicwBYb7iax4HgOdU1yZ15wppWaHlAZWOFZe2nibHzk6ichbbycpAFY5biab6Lng/132");
//        map.put("tel","18306101098");
//        map.put("nickName","朱方");
        String sessionid = getSessionId();
        HttpSession session = MySessionContext.getSession(sessionid);
        String openid = (String) session.getAttribute(Constants.WX_SESSION_OPEN_ID);
        String personId = service.getIdByopenId(openid);
        map = service.queryMyInfo(personId);
        return JSONObject.toJSONString(map);
    }

    @RequestMapping(value = "/queryEntryStatus",method = RequestMethod.POST)
    public String queryEntryStatus() {
        Map<String,Object> map = new HashMap<>();
        String entryNo = getParameters().getString("id");
//        System.out.println(entryNo);
        String status = service.queryEntryStatus(entryNo);
        map.put("verifyStatus",status);
        return JSONObject.toJSONString(map);
    }

    @RequestMapping(value = "/queryEntryList",method = RequestMethod.POST)
    public String queryEntryList() {
        String sessionid = getSessionId();
        JSONObject data = getParameters();
        HttpSession session = MySessionContext.getSession(sessionid);
        String openid = (String) session.getAttribute(Constants.WX_SESSION_OPEN_ID);
//        System.out.println(openid);
        String personId = service.getIdByopenId(openid);

        List<Map<String,Object>> list = new ArrayList<>();
        list = service.queryEntryList(personId,data.getString("type"));

        return JSONObject.toJSONString(list);
    }

    @RequestMapping(value = "/queryVerifiedEntryList",method = RequestMethod.POST)
    public String queryVerifiedEntryList() {
        String sessionid = getSessionId();
        HttpSession session = MySessionContext.getSession(sessionid);
        String openid = (String) session.getAttribute(Constants.WX_SESSION_OPEN_ID);
        System.out.println(openid);
        String personId = service.getIdByopenId(openid);

        JSONObject data = getParameters();
        List<Map<String,Object>> list = new ArrayList<>();
        list = service.queryVerifiedEntryList(personId,data.getString("type"));

        return JSONObject.toJSONString(list);
    }

    @RequestMapping(value = "/removeEntry",method = RequestMethod.POST)
    public String removeEntry() {
        Map<String,Object> map = new HashMap<>();
        String entryNo = getParameters().getString("entryNo");
//        System.out.println(entryNo);
        service.removeEntry(entryNo);
        return JSONObject.toJSONString(map);
    }

    @RequestMapping(value = "/removeVerifyEntry",method = RequestMethod.POST)
    public String removeVerifyEntry() {
        Map<String,Object> map = new HashMap<>();
        String entryNo = getParameters().getString("entryNo");
//        System.out.println(entryNo);
        service.removeVerifyEntry(entryNo);
        return JSONObject.toJSONString(map);
    }

    @RequestMapping(value = "/isEntryAdmin",method = RequestMethod.POST)
    public String isEntryAdmin() {
        Map<String,Object> map = new HashMap<>();
        String sessionid = getSessionId();
        if(sessionid == null) return "";
        HttpSession session = MySessionContext.getSession(sessionid);
        String openid = (String) session.getAttribute(Constants.WX_SESSION_OPEN_ID);
//        System.out.println(openid);
        //是管理员身份（0是，1不是）
        String personId = service.getIdByopenId(openid);
        String level = service.isEntryAdmin(personId);
        map.put("id",level);
        return JSONObject.toJSONString(map);
    }

    @RequestMapping(value = "/lockThisItem",method = RequestMethod.POST)
    public String lockThisItem() {
        Map<String,Object> map = new HashMap<>();
        String entryNo = getParameters().getString("entryNo");
//        System.out.println(entryNo);
        //锁住这一项不能修改
        service.lockThisItem(entryNo);
        return JSONObject.toJSONString(map);
    }

    @RequestMapping(value = "/unlockThisItem",method = RequestMethod.POST)
    public String unlockThisItem() {
        Map<String,Object> map = new HashMap<>();
        String entryNo = getParameters().getString("entryNo");
//        System.out.println(entryNo);
        //去数据库查看是否已经评价，若无则解锁，若有则不操作
        String status = service.checkVerified(entryNo);
        if(status.equals("0")) {
            service.unlockThisItem(entryNo);
        } else {
            service.lockThisItem(entryNo);
        }
        return JSONObject.toJSONString(map);
    }

    @RequestMapping(value = "/verifiedEntry",method = RequestMethod.POST)
    public String verifiedEntry() {
        Map<String,Object> map = new HashMap<>();
        String entryNo = getParameters().getString("entryNo");
        System.out.println(entryNo);
        //通过操作，锁上内容
        service.verifiedEntry(entryNo);
        return JSONObject.toJSONString(map);
    }

    @RequestMapping(value = "/unpassedEntry",method = RequestMethod.POST)
    public String unpassedEntry() {
        Map<String,Object> map = new HashMap<>();
        String entryNo = getParameters().getString("entryNo");
        System.out.println(entryNo);
        //不通过操作，锁上内容
        service.unpassedEntry(entryNo);
        return JSONObject.toJSONString(map);
    }

    @RequestMapping(value = "/checkEntryLock",method = RequestMethod.POST)
    public String checkEntryLock() {
        Map<String,Object> map = new HashMap<>();
        String entryNo = getParameters().getString("entryNo");
//        System.out.println(entryNo);
        String lock = service.checkEntryLock(entryNo);
        //检查锁：0没锁，1锁住了
        map.put("lock",lock);
        return JSONObject.toJSONString(map);
    }

    @RequestMapping(value = "/queryEntryDetail",method = RequestMethod.POST)
    public String queryEntryDetail() {
        String entryNo = getParameters().getString("entryNo");
        System.out.println(entryNo);
        Map<String,Object> map = new HashMap<>();
        map = service.queryEntryInfoByentryNo(entryNo);
        return JSONObject.toJSONString(map);
    }

    @RequestMapping(value = "/modifyEntryInfo",method = RequestMethod.POST)
    public String modifyEntryInfo() {
        JSONObject data = getParameters();
        String sessionid = getSessionId();
        HttpSession session = MySessionContext.getSession(sessionid);
        String openid = (String) session.getAttribute(Constants.WX_SESSION_OPEN_ID);
        Map<String, Object> map = new HashMap<>();
        String date = data.getString("date");


        map.put("deptName", data.getString("deptName"));
        map.put("projName", data.getString("projName"));
        map.put("contact", data.getString("contact"));
        map.put("tel", data.getString("tel"));
        map.put("email", data.getString("email"));
        map.put("staff", data.getString("staff"));
        map.put("description", data.getString("description"));
        map.put("requirement", data.getString("requirement"));
        map.put("sDate", date.split("//s+")[0]);
        map.put("eDate", date.split("//s+")[1]);

        service.modifyEntryInfo(map,data.getString("entryNo"));
        System.out.println(map);
        return "success";
    }

    @RequestMapping(value = "/getAccessToken" ,method = RequestMethod.POST)
    public String getAccessToken() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = new HashMap<>();
        String sessionid = getSessionId();
        HttpSession session = MySessionContext.getSession(sessionid);
        String token = (String) session.getAttribute(Constants.WX_ACCESS_TOKEN);
        String expiredTime = (String) session.getAttribute(Constants.WX_TOKEN_EXPIRED_TIME);
        if(StringUtils.isBlank(token) || StringUtils.isBlank(expiredTime) || CommonUtil.isTokenExpired(interval,expiredTime)) {
            session.setAttribute(Constants.WX_TOKEN_EXPIRED_TIME,CommonUtil.getCurrentTime());
            params.put("APPID", appid);  //
            params.put("APPSECRET", appsecret);  //
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                    "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={APPID}&secret={APPSECRET}", String.class, params);
            String body = responseEntity.getBody();
            JSONObject object = JSON.parseObject(body);
            token = object.getString("access_token");
            String expiresIn = object.getString("expires_in");
            session.setAttribute(Constants.WX_ACCESS_TOKEN,token);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("token",token);
        return JSONObject.toJSONString(map);
    }

    @RequestMapping(value = "/queryOpenidByEntryNo",method = RequestMethod.POST)
    public String queryOpenidByEntryNo() throws Exception{
        JSONObject data = getParameters();
        String entryNo = data.getString("entryNo");
        String uid = service.queryOfficerIdByEntryNo(entryNo);
        String openid = service.queryOpenidById(uid);
        Map<String,Object> map = new HashMap<>();
        map.put("openid",openid);
        return JSONObject.toJSONString(map);
    }

    @RequestMapping(value = "/pushToUser",method = RequestMethod.GET)
    public String pushToUser() throws Exception {
        String sessionid = getSessionId();
        HttpSession session = MySessionContext.getSession(sessionid);
        String openid = (String) session.getAttribute(Constants.WX_SESSION_OPEN_ID);
        String pageUrl="pages/login/login";
        JSONObject data = getParameters();
        Map<String,Object> map = new HashMap<>();
        Iterator it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
            map.put(entry.getKey(), entry.getValue());
        }
        return push(openid,templateid,pageUrl,map);
    }


    public String push(String openid, String templateid, String pageUrl, Map<String,Object> map) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        //这里简单起见我们每次都获取最新的access_token（时间开发中，应该在access_token快过期时再重新获取）
        String url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + getAccessToken();
        //拼接推送的模版
        WxMssVo wxMssVo = new WxMssVo();
        wxMssVo.setTouser(openid);//用户的openid（要发送给那个用户，通常这里应该动态传进来的）
        wxMssVo.setTemplate_id(templateid);//订阅消息模板id
        wxMssVo.setPage(pageUrl);

        Map<String, TemplateData> m = new HashMap<>();
        for(Map.Entry<String,Object> entry : map.entrySet()) {
            m.put(entry.getKey(), new TemplateData((String) entry.getValue()));
        }
        wxMssVo.setData(m);
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity(url, wxMssVo, String.class);
        return responseEntity.getBody();
    }



}
