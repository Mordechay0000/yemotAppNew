package com.mordechay.yemotapp.ui.programmatically.list_for_securing_login_log;

public class SecuringSessionItem {
    private String id;
    private String token;
    private String active;
    private String selectedDID;
    private String remoteIP;
    private String sessionType;
    private String createTime;
    private String lastRequest;
    private String doubleAuthStatus;


    public SecuringSessionItem(String id, String token, String active, String remoteIP, String selectedDID, String sessionType, String createTime, String lastRequest, String doubleAuthStatus) {
        this.id = id;
        this.token = token;
        this.active = active;
        this.remoteIP = remoteIP;
        this.selectedDID = selectedDID;
        this.sessionType = sessionType;
        this.createTime = createTime;
        this.lastRequest = lastRequest;
        this.doubleAuthStatus = doubleAuthStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }

    public String getSelectedDID() {
        return selectedDID;
    }

    public void setSelectedDID(String selectedDID) {
        this.selectedDID = selectedDID;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(String lastRequest) {
        this.lastRequest = lastRequest;
    }

    public String getDoubleAuthStatus() {
        return doubleAuthStatus;
    }

    public void setDoubleAuthStatus(String doubleAuthStatus) {this.doubleAuthStatus = doubleAuthStatus;}
}