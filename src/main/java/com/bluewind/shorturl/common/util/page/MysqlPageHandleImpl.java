package com.bluewind.shorturl.common.util.page;

import org.springframework.stereotype.Component;

/**
 * @author liuxingyu01
 * @date 2022-05-10 8:32
 * @description
 **/
@Component
public class MysqlPageHandleImpl implements IPageHandle {

    @Override
    public String handlerPagingSQL(String oldSQL, int pageNo, int pageSize) {
        StringBuilder sql = new StringBuilder(oldSQL);
        if (pageSize > 0) {
            int firstResult = (pageNo - 1) * pageSize;
            if (firstResult <= 0) {
                sql.append(" limit ").append(pageSize);
            } else {
                sql.append(" limit ").append(firstResult).append(",")
                        .append(pageSize);
            }
        }
        return sql.toString();
    }


    @Override
    public String handlerCountSQL(String oldSQL) {
        StringBuilder newSql = new StringBuilder();
        newSql.append("select count(0) from ( ");
        newSql.append(oldSQL);
        newSql.append(" ) temp");
        return newSql.toString();
    }

}
