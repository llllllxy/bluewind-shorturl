package com.bluewind.shorturl.module.weixin.entity;

import java.io.Serializable;

/**
 * @author liuxingyu01
 * @date 2022-09-05 19:59
 * @description
 **/
public class WxConfig implements Serializable {
    private static final long serialVersionUID = -8940196742313994740L;

    private String configId;

    private String cropId;

    private String agentId;

    private String secret;

    private String name;

    private String note;

    private String status;

    private String createTime;

    private String updateTime;

    /**
     * 当前accessToken，一般7200秒后失效
     */
    private String accessToken;

    /**
     * 当前accessToken失效时间（格式yyyyMMddHHmmss）
     */
    private String accessTokenExpires;

    /**
     * 当前jsapiTicket，一般7200秒后失效
     */
    private String jsapiTicket;

    /**
     * 当前jsapiTicket失效时间（格式yyyyMMddHHmmss）
     */
    private String jsapiTicketExpires;

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getCropId() {
        return cropId;
    }

    public void setCropId(String cropId) {
        this.cropId = cropId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
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
        return "WxConfig{" +
                "configId='" + configId + '\'' +
                ", cropId='" + cropId + '\'' +
                ", agentId='" + agentId + '\'' +
                ", secret='" + secret + '\'' +
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
