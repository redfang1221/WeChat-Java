package com.chinatelecom.template.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
public interface WeappMapper {

    int checkNewRegister(@Param("openid") String openid);

    int addNewUserInfo(@Param("openid") String openid, @Param("map") Map<String, Object> map);

    int updateUserInfo(@Param("openid") String openid, @Param("map") Map<String, Object> map);

    int addNewUserPhone(@Param("openid") String openid, @Param("map") Map<String, Object> map);

    int updateUserPhone(@Param("openid") String openid, @Param("map") Map<String, Object> map);

    int allocateAuthority(@Param("id") String uuid2, @Param("name") String nickName, @Param("uid") String uuid1);

    int isAuthorityNameBlank(@Param("openid") String openid);

    int updateAuthorityName(@Param("id") String id, @Param("name") String nickName);

    String queryIdByopenid(@Param("openid") String openid);

    int addEntryFixedInfo(@Param("officerId") String managerId, @Param("applicantId") String personId, @Param("entryNo") String entryNo, @Param("entryManager") String entryManager, @Param("id") String id);

    Map<String, Object> queryEntryInfoByentryNo(@Param("entryNo") String entryNo);

    int addNewEntryInfo(@Param("map") Map<String, Object> map, @Param("entryNo") String entryNo);

    String checkEntryLock(@Param("entryNo") String entryNo);

    String queryEntryStatus(@Param("entryNo") String entryNo);

    int lockThisItem(@Param("entryNo") String entryNo);

    int unlockThisItem(@Param("entryNo") String entryNo);

    int verifiedEntry(@Param("entryNo") String entryNo);

    int unpassedEntry(@Param("entryNo") String entryNo);

    Map<String,Object> queryMyInfo(@Param("id") String personId);

    List<Map<String, Object>> queryEntryList(@Param("id") String personId, @Param("type") String type);

    List<Map<String, Object>> queryVerifiedEntryList(@Param("id") String personId, @Param("type") String type);

    int removeEntry(@Param("entryNo") String entryNo);

    int removeVerifyEntry(@Param("entryNo") String entryNo);

    String isEntryAdmin(@Param("personId") String personId);

    int modifyEntryInfo(@Param("map") Map<String, Object> map, @Param("entryNo") String entryNo);

    String queryOfficerIdByEntryNo(@Param("entryNo") String entryNo);

    String queryOpenidById(@Param("id") String uid);

    Map<String, Object> queryOfficerInfo();
}
