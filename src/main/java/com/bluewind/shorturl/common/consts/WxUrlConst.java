package com.bluewind.shorturl.common.consts;

/**
 * @author liuxingyu01
 * @date 2022-09-05 19:46
 * @description 企业微信URL汇总
 **/
public class WxUrlConst {

    // 企业微信获取ACCESS_TOKEN URL
    public static String QY_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";

    // 企业微信获取JSAPI_TICKET URL
    public static String QY_TICKET_URL = "https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket";

    // 企业微信根据code获取用户信息（老接口，但是可以用）
    // https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=ACCESS_TOKEN&code=CODE
    public static String QY_USER_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo";

    // 企业微信根据userId获取用户信息（老接口，但是可以用）
    // https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&userid=USERID
    public static String QY_USER_DETAIL_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/get";

    // 企业微信获取临时素材
    // https://qyapi.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID
    public static String QY_GET_MEDIA_URL = "https://qyapi.weixin.qq.com/cgi-bin/media/get";

    // 企业微信发送消息
    // https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN
    public static String QY_SEND_MSG_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send";

    // 企业微信获取部门列表
    // https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=ACCESS_TOKEN&id=ID
    public static String QY_LIST_DEPT_URL = "https://qyapi.weixin.qq.com/cgi-bin/department/list";

    // 企业微信获取部门成员列表
    // https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=ACCESS_TOKEN&department_id=DEPARTMENT_ID&fetch_child=FETCH_CHILD
    public static String QY_LIST_USER_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/list";

    // 企业微信获取获取打卡规则列表
    public static String QY_GET_CHECKIN_OPTION_URL = "https://qyapi.weixin.qq.com/cgi-bin/checkin/getcheckinoption";

    // 企业微信获取获取打卡数据列表
    public static String QY_GET_CHECKIN_DATA_URL = "https://qyapi.weixin.qq.com/cgi-bin/checkin/getcheckindata";
}
