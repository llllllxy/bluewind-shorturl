package com.bluewind.shorturl.module.manage.service;

import com.bluewind.shorturl.module.manage.dao.IndexManageDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author liuxingyu01
 * @date 2022-05-18 15:25
 * @description
 **/
@Service
public class IndexManageServiceImpl {

    @Autowired
    private IndexManageDaoImpl indexManageDao;

    public Map<String, Object> getUserInfo(String account) {
        return indexManageDao.getUserInfo(account);
    }
}
