package com.bluewind.shorturl.module.tenant.service;

import com.bluewind.shorturl.common.util.page.Page;
import com.bluewind.shorturl.module.tenant.dao.UrlManageDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liuxingyu01
 * @date 2022-05-18 11:27
 * @description
 **/
@Service
public class UrlManageServiceImpl {

    @Autowired
    private UrlManageDaoImpl UrlManageDaoImpl;

    public Page getPage(Integer pageSize, Integer pageNumber, String surl, String createdAt, String status, String sortName, String sortOrder) {
        return UrlManageDaoImpl.getPage(pageSize, pageNumber, surl, createdAt, status, sortName, sortOrder);
    }

    public int del(String id) {
        return UrlManageDaoImpl.del(id);
    }

    public int batchDel(String idlistStr) {
        return UrlManageDaoImpl.batchDel(idlistStr);
    }
}
