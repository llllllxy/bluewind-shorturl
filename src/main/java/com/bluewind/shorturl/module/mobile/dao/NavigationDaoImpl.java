package com.bluewind.shorturl.module.mobile.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author liuxingyu01
 * @date 2022-09-07 12:48
 * @description
 **/
@Repository
public class NavigationDaoImpl {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     * 获取菜单列表
     *
     * @return
     */
    public List<Map<String, Object>> listNavigation() {
        StringBuffer sql = new StringBuffer();
        sql.append("select ");
        sql.append("md.module_id, ");
        sql.append("md.module_name, ");
        sql.append("fun.function_id, ");
        sql.append("fun.function_name, ");
        sql.append("fun.function_url, ");
        sql.append("fun.function_icon ");
        sql.append("from s_wx_function fun ");
        sql.append("join s_wx_module md on md.module_id = fun.module_id ");
        sql.append("where fun.status = '0' ");
        sql.append("and md.status = '0' ");
        sql.append("order by md.seq, fun.seq ");

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString());
        return result;
    }

}
