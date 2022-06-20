package com.bluewind.shorturl.module.api.service;


import com.bluewind.shorturl.module.api.dao.ApiDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ApiServiceImpl {

    @Autowired
    private ApiDaoImpl apiDao;

    public Map<String, Object> getConfigData(String accessKey) {
        return apiDao.getConfigData(accessKey);
    }

    public int expire(String shortUrl, String expireDate, String tenantId) {
        return apiDao.expire(shortUrl, expireDate, tenantId);
    }


    public int disable(String shortUrl, String tenantId) {
        return apiDao.disable(shortUrl, tenantId);
    }


    public int enable(String shortUrl, String tenantId) {
        return apiDao.enable(shortUrl, tenantId);
    }
}
