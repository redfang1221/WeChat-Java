package com.chinatelecom.template.server.service;

import java.util.List;
import java.util.Map;

public interface WeappService {

    int checkSaved(String openid);

    int registerNewUserInfo(String openid,Map<String,Object> map);

    int updateUserInfo(String openid,Map<String,Object> map);

    int addNewUserPhone(String openid, Map<String, Object> map);

    int updateUserPhone(String openid, Map<String, Object> map);

    String getIdByopenId(String openid);

    int addEntryFixedInfo(String managerId, String personId,String entryNo, String entryManager);

    Map<String, Object> queryEntryInfoByentryNo(String entryNo);

    int addNewEntryInfo(Map<String, Object> map, String entryNo);

    String checkEntryLock(String entryNo);

    String queryEntryStatus(String entryNo);

    int lockThisItem(String entryNo);

    int unlockThisItem(String entryNo);

    int verifiedEntry(String entryNo);

    int unpassedEntry(String entryNo);

    Map<String,Object> queryMyInfo(String personId);

    List<Map<String, Object>> queryEntryList(String personId);

    List<Map<String, Object>> queryVerifiedEntryList(String personId);

    int removeEntry(String entryNo);

    int removeVerifyEntry(String entryNo);

    String isEntryAdmin(String personId);

    int modifyEntryInfo(Map<String, Object> map, String entryNo);

    String queryOfficerIdByEntryNo(String entryNo);

    String queryOpenidById(String uid);

    Map<String, Object> queryOfficerInfo();
}
