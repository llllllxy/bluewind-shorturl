package com.bluewind.shorturl.module.tenant.service;

import com.bluewind.shorturl.common.util.page.Page;
import com.bluewind.shorturl.module.tenant.dao.UrlManageDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

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

    public Map<String, Object> findById(String id) {
        return UrlManageDaoImpl.findById(id);
    }

    public int del(String id) {
        return UrlManageDaoImpl.del(id);
    }

    public int batchDel(String idlistStr) {
        return UrlManageDaoImpl.batchDel(idlistStr);
    }

    public int enable(String idlistStr) {
        return UrlManageDaoImpl.enable(idlistStr);
    }

    public int disable(String idlistStr) {
        return UrlManageDaoImpl.disable(idlistStr);
    }

    public int edit(String id, String status, String expireDate, String note) {
        return UrlManageDaoImpl.edit(id, status, expireDate, note);
    }
}
