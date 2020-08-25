package com.chinatelecom.template.server.service.impl;

import com.chinatelecom.template.server.mapper.TestMapper;
import com.chinatelecom.template.server.mapper.WeappMapper;
import com.chinatelecom.template.server.service.TestService;
import com.chinatelecom.template.server.service.WeappService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class WeappServiceImpl implements WeappService {

    @Autowired
    WeappMapper weappMapper;

    @Override
    public int checkSaved(String openid) {
        try{
            return weappMapper.checkNewRegister(openid);
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public int registerNewUserInfo(String openid,Map<String,Object> map) {
        String uuid1 = UUID.randomUUID().toString().replaceAll("-", "");
        String uuid2 = UUID.randomUUID().toString().replaceAll("-", "");
        map.put("id",uuid1);
        if(weappMapper.allocateAuthority(uuid2,MapUtils.getString(map,"nickName"),uuid1) > 0 && weappMapper.addNewUserInfo(openid,map) > 0) {
            return 1;
        }
        return -1;
    }

    @Override
    public int updateUserInfo(String openid, Map<String, Object> map) {
        int flag = 1;
        if(weappMapper.isAuthorityNameBlank(openid) > 0) {
            String id = weappMapper.queryIdByopenid(openid);
            flag = weappMapper.updateAuthorityName(id,MapUtils.getString(map,"nickName"));
        }
        if(weappMapper.updateUserInfo(openid,map) > 0 && flag > 0) {
            return 1;
        }
        return -1;
    }

    @Override
    public int addNewUserPhone(String openid, Map<String, Object> map) {
        String uuid1 = UUID.randomUUID().toString().replaceAll("-", "");
        String uuid2 = UUID.randomUUID().toString().replaceAll("-", "");
        map.put("id",uuid1);
        if(weappMapper.allocateAuthority(uuid2,"",uuid1) > 0 && weappMapper.addNewUserPhone(openid, map) > 0) {
            return 1;
        }
        return -1;
    }

    @Override
    public int updateUserPhone(String openid, Map<String, Object> map) {
        return weappMapper.updateUserPhone(openid, map);
    }

    @Override
    public String getIdByopenId(String openid) {
        return weappMapper.queryIdByopenid(openid);
    }

    @Override
    public int addEntryFixedInfo(String managerId, String personId, String entryNo, String entryManager) {
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        return weappMapper.addEntryFixedInfo(managerId, personId, entryNo, entryManager, id);
    }

    @Override
    public Map<String, Object> queryEntryInfoByentryNo(String entryNo) {
        return weappMapper.queryEntryInfoByentryNo(entryNo);
    }

    @Override
    public int addNewEntryInfo(Map<String, Object> map, String entryNo) {
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        map.put("id",id);
        return weappMapper.addNewEntryInfo(map, entryNo);
    }

    @Override
    public String checkEntryLock(String entryNo) {
        return weappMapper.checkEntryLock(entryNo);
    }

    @Override
    public String queryEntryStatus(String entryNo) {
        return weappMapper.queryEntryStatus(entryNo);
    }

    @Override
    public int lockThisItem(String entryNo) {
        return weappMapper.lockThisItem(entryNo);
    }

    @Override
    public int unlockThisItem(String entryNo) {
        return weappMapper.unlockThisItem(entryNo);
    }

    @Override
    public int verifiedEntry(String entryNo) {
        return weappMapper.verifiedEntry(entryNo);
    }

    @Override
    public int unpassedEntry(String entryNo) {
        return weappMapper.unpassedEntry(entryNo);
    }

    @Override
    public Map<String,Object> queryMyInfo(String personId) {
        return weappMapper.queryMyInfo(personId);
    }

    @Override
    public List<Map<String, Object>> queryEntryList(String personId, String type) {
        return weappMapper.queryEntryList(personId, type);
    }

    @Override
    public List<Map<String, Object>> queryVerifiedEntryList(String personId, String type) {
        return weappMapper.queryVerifiedEntryList(personId, type);
    }

    @Override
    public int removeEntry(String entryNo) {
        return weappMapper.removeEntry(entryNo);
    }

    @Override
    public int removeVerifyEntry(String entryNo) {
        return weappMapper.removeVerifyEntry(entryNo);
    }

    @Override
    public String isEntryAdmin(String personId) {
        return weappMapper.isEntryAdmin(personId);
    }

    @Override
    public int modifyEntryInfo(Map<String, Object> map, String entryNo) {
        return weappMapper.modifyEntryInfo(map,entryNo);
    }

    @Override
    public String queryOfficerIdByEntryNo(String entryNo) {
        return weappMapper.queryOfficerIdByEntryNo(entryNo);
    }

    @Override
    public String queryOpenidById(String uid) {
        return weappMapper.queryOpenidById(uid);
    }

    @Override
    public Map<String, Object> queryOfficerInfo() {
        return weappMapper.queryOfficerInfo();
    }

    @Override
    public String checkVerified(String entryNo) {
        return weappMapper.checkVerified(entryNo);
    }

}
