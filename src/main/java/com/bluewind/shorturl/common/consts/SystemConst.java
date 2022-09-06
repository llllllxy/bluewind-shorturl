package com.bluewind.shorturl.common.consts;

/**
 * @author liuxingyu01
 * @date 2022-05-18 18:26
 * @description 系统常量类
 **/
public class SystemConst {
    /**
     * 项目名称
     */
    public static final String SYSTEM_NAME = "bluewind-shorturl";

    /**
     * 管理员 会话信息缓存的 redis key
     */
    public static final String ADMIN_ADMIN_KEY = "bluewind-shorturl:admin";

    /**
     * 租户 会话信息缓存的 redis key
     */
    public static final String SYSTEM_TENANT_KEY = "bluewind-shorturl:tenant";

    /**
     * 租户 header 和 cookie-key
     */
    public static final String SYSTEM_TENANT_TOKEN = "Authorization";

    /**
     * 图片验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "bluewind-shorturl:captcha_codes";

    /**
     * 手机验证码 redis key
     */
    public static final String SMS_CODE_KEY = "bluewind-shorturl:sms_codes";
}
