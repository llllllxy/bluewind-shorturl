package com.bluewind.shorturl.module.mobile.entity;

import java.io.Serializable;

/**
 * @author liuxingyu01
 * @date 2022-09-17 11:14
 * @description 钉钉配置实体类
 **/
public class DdConfig  implements Serializable {
    private static final long serialVersionUID = -1;

    private String configId;

    private String corpId;				//企业ID,企业ID，在开发者后台中企业视图下开发者账号设置里面可以看到

    private String appKey;				//钉钉应用的唯一标识key

    private String appSecret;		   //钉钉企业内部应用的密钥

    private String agentId;				//应用的标识,编辑企业应用可以看到

    private String name;

    private String note;

    private String status;

    private String createTime;

    private String updateTime;

    private String accessToken;		    //当前accessToken，一般7200秒后失效

    private String accessTokenExpires;	 //当前accessToken失效时间

    private String jsapiTicket;			//当前jsapiTicket，一般7200秒后失效

    private String jsapiTicketExpires;	//当前jsapiTicket失效时间

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessTokenExpires() {
        return accessTokenExpires;
    }

    public void setAccessTokenExpires(String accessTokenExpires) {
        this.accessTokenExpires = accessTokenExpires;
    }

    public String getJsapiTicket() {
        return jsapiTicket;
    }

    public void setJsapiTicket(String jsapiTicket) {
        this.jsapiTicket = jsapiTicket;
    }

    public String getJsapiTicketExpires() {
        return jsapiTicketExpires;
    }

    public void setJsapiTicketExpires(String jsapiTicketExpires) {
        this.jsapiTicketExpires = jsapiTicketExpires;
    }

    @Override
    public String toString() {
        return "DdConfig{" +
                "configId='" + configId + '\'' +
                ", corpId='" + corpId + '\'' +
                ", appKey='" + appKey + '\'' +
                ", appSecret='" + appSecret + '\'' +
                ", agentId='" + agentId + '\'' +
                ", name='" + name + '\'' +
                ", note='" + note + '\'' +
                ", status='" + status + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", accessTokenExpires='" + accessTokenExpires + '\'' +
                ", jsapiTicket='" + jsapiTicket + '\'' +
                ", jsapiTicketExpires='" + jsapiTicketExpires + '\'' +
                '}';
    }
}
