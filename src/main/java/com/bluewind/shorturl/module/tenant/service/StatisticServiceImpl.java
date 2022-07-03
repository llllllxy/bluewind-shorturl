package com.bluewind.shorturl.module.tenant.service;

import com.bluewind.shorturl.common.util.page.Page;
import com.bluewind.shorturl.module.tenant.dao.StatisticDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticServiceImpl {

    @Autowired
    private StatisticDaoImpl statisticDao;

    public Page getPage(Integer pageSize, Integer pageNumber, String surl, String sortName, String sortOrder) {
        return statisticDao.getPage(pageSize, pageNumber, surl, sortName, sortOrder);
    }

}
