package com.mordechay.yemotapp.ui.programmatically.list_for_sip_accounts;

public class sipItem {
    private String accountNumber;
    private String username;
    private String password;
    private String protocol;
    private String numExtension;
    private String date;
    private String committedSystem;
    private String specialCallerID;


    public sipItem(String accountNumber, String username, String password, String numExtension, String protocol, String date, String committedSystem, String specialCallerID) {
        this.accountNumber = accountNumber;
        this.username = username;
        this.password = password;
        this.numExtension = numExtension;
        this.protocol = protocol;
        this.date = date;
        this.committedSystem = committedSystem;
        this.specialCallerID = specialCallerID;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNumExtension() {
        return numExtension;
    }

    public void setNumExtension(String numExtension) {
        this.numExtension = numExtension;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCommittedSystem() {
        return committedSystem;
    }

    public void setCommittedSystem(String committedSystem) {
        this.committedSystem = committedSystem;
    }

    public String getSpecialCallerID() {
        return specialCallerID;
    }

    public void setSpecialCallerID(String specialCallerID) {
        this.specialCallerID = specialCallerID;
    }
}