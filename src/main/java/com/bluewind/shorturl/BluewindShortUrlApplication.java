package com.bluewind.shorturl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.net.InetAddress;
import java.net.UnknownHostException;


@SpringBootApplication
@EnableTransactionManagement // 简单开启事务管理
@ServletComponentScan // 扫描带@WebFilter、@WebServlet、@WebListener并将帮我们注入bean
public class BluewindShortUrlApplication {
    final static Logger logger = LoggerFactory.getLogger(BluewindShortUrlApplication.class);

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application = SpringApplication.run(BluewindShortUrlApplication.class, args);
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path");

        logger.info("\n----------------------------------------------------------\n\t" +
                "bluewind-shorturl 启动成功！\n\t" +
                "┌─┐┬ ┬┌─┐┌─┐┌─┐┌─┐┌─┐  ┌─┐┌┬┐┌─┐┬─┐┌┬┐┌─┐┌┬┐   ┬\n\t" +
                "└─┐│ ││  │  ├┤ └─┐└─┐  └─┐ │ ├─┤├┬┘ │ ├┤  ││   │\n\t" +
                "└─┘└─┘└─┘└─┘└─┘└─┘└─┘  └─┘ ┴ ┴ ┴┴└─ ┴ └─┘─┴┘   o\n\t" +
                "-------------------------------------------------------------------------\n\t" +
                "Access URLs:\n\t" +
                "Local: \t\thttp://localhost:" + port + "/\n\t" +
                "External: \thttp://" + ip + ":" + port + "/\n" +
                "-------------------------------------------------------------------------");
    }

}
