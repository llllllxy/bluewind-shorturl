package com.bluewind.shorturl.common.config;

import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;

/**
 * @author liuxingyu01
 * @date 2022-05-14 22:39
 * @description
 **/
@Configuration
public class ActiveRecordConfiguration {
    @Autowired
    private DataSource datasource;

    @Bean
    public ActiveRecordPlugin activeRecordPlugin(){
        ActiveRecordPlugin activeRecordPlugin = new ActiveRecordPlugin(this.datasource);
        activeRecordPlugin.setShowSql(true);
        // 配置Mysql方言
        activeRecordPlugin.setDialect(new MysqlDialect());
        activeRecordPlugin.getEngine().setSourceFactory(new ClassPathSourceFactory());
        activeRecordPlugin.addSqlTemplate("sql/all_sqls.sql");
        // _MappingKit.mapping(activeRecordPlugin);
        activeRecordPlugin.start();
        return activeRecordPlugin;
    }
}
