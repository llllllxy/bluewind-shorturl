package com.bluewind.shorturl.module.mobile.controller;

import com.bluewind.shorturl.module.mobile.service.DdServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuxingyu01
 * @date 2022-09-19 9:00
 * @description 钉钉公共请求Controller
 **/
@RestController
@RequestMapping("/mobile/dd")
public class DdController {
    private final static Logger log = LoggerFactory.getLogger(DdController.class);


    /**
     * 密码加密盐值
     */
    @Value("${hash.salt}")
    private String salt;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private DdServiceImpl ddService;



}
