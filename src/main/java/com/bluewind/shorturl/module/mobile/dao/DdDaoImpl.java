package com.bluewind.shorturl.module.mobile.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author liuxingyu01
 * @date 2022-09-19 9:01
 * @description
 **/
@Repository
public class DdDaoImpl {

    @Autowired
    private JdbcTemplate jdbcTemplate;

}
