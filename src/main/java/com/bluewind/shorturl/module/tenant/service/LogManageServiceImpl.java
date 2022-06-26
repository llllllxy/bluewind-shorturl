package com.bluewind.shorturl.module.tenant.service;

import com.bluewind.shorturl.common.util.page.Page;
import com.bluewind.shorturl.module.tenant.dao.LogManageDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author liuxingyu01
 * @date 2022-05-18 11:28
 * @description
 **/
@Service
public class LogManageServiceImpl {

    @Autowired
    private LogManageDaoImpl logManageDao;

    public Page getPage(Integer pageSize, Integer pageNumber, String surl, String startTime,String  endTime, String accessIp ,String sortName, String sortOrder) {
       return logManageDao.getPage(pageSize, pageNumber, surl, startTime, endTime, accessIp ,sortName, sortOrder);
    }

    public Map<String, Object> findByLogId(String logId) {
        return logManageDao.findByLogId(logId);
    }

}
