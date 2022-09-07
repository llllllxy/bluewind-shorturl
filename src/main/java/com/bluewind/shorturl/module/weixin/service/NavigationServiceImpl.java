package com.bluewind.shorturl.module.weixin.service;

import com.bluewind.shorturl.module.weixin.dao.NavigationDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author liuxingyu01
 * @date 2022-09-07 12:48
 * @description
 **/
@Service
public class NavigationServiceImpl {

    @Autowired
    private NavigationDaoImpl navigationDao;


    public List<Map<String, Object>> listNavigation() {
        return navigationDao.listNavigation();
    }
}
