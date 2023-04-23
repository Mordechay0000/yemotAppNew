package com.mordechay.yemotapp.data;

import java.util.ArrayList;

public class DataTransfer {

    private static boolean errorNoInternetShowing;

    private static boolean progressViewShowing;

    /*
    server login
     */
    private static String username;
    private static String uid;




    /*
    system information and login
     */

    //login
    private static String token;
    private static String infoNumber;
    private static String infoPassword;

    //home activity, information
    private static String infoName;
    private static String infoOrganization;
    private static String infoContactName;
    private static String infoPhones;
    private static String infoInvoiceName;
    private static String infoInvoiceAddress;
    private static String infoFax;
    private static String infoEmail;
    private static String infoCreditFile;
    //password's
    private static String infoAccessPassword;
    private static String infoRecordPassword;
    //other data information
    private static String infoUnits;
    private static String infoUnitsExpireDate;


    /*
    url open file
     */
    private static String fileUrl;
    private static String fileName;
    private static String filePath;
    private static String fileType;

    /*
    url transfer information for fragment
     */

    private static String thisWhat;


    public static boolean isErrorNoInternetShowing() {
        return errorNoInternetShowing;
    }

    public static void setErrorNoInternetShowing(boolean errorNoInternetShowing) {
        DataTransfer.errorNoInternetShowing = errorNoInternetShowing;
    }

    public static boolean isProgressViewShowing() {
        return progressViewShowing;
    }

    public static void setProgressViewShowing(boolean progressViewShowing) {
        DataTransfer.progressViewShowing = progressViewShowing;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        DataTransfer.username = username;
    }

    public static String getUid() {
        return uid;
    }

    public static void setUid(String uid) {
        DataTransfer.uid = uid;
    }

    public static String getInfoName() {
        return infoName;
    }

    public static void setInfoName(String infoName) {
        DataTransfer.infoName = infoName;
    }

    public static String getInfoOrganization() {
        return infoOrganization;
    }

    public static void setInfoOrganization(String infoOrganization) {
        DataTransfer.infoOrganization = infoOrganization;
    }

    public static String getInfoContactName() {
        return infoContactName;
    }

    public static void setInfoContactName(String infoContactName) {
        DataTransfer.infoContactName = infoContactName;
    }

    public static String getInfoPhones() {
        return infoPhones;
    }

    public static void setInfoPhones(String infoPhones) {
        DataTransfer.infoPhones = infoPhones;
    }

    public static String getInfoInvoiceName() {
        return infoInvoiceName;
    }

    public static void setInfoInvoiceName(String infoInvoiceName) {
        DataTransfer.infoInvoiceName = infoInvoiceName;
    }

    public static String getInfoInvoiceAddress() {
        return infoInvoiceAddress;
    }

    public static void setInfoInvoiceAddress(String infoInvoiceAddress) {
        DataTransfer.infoInvoiceAddress = infoInvoiceAddress;
    }

    public static String getInfoFax() {
        return infoFax;
    }

    public static void setInfoFax(String infoFax) {
        DataTransfer.infoFax = infoFax;
    }

    public static String getInfoEmail() {
        return infoEmail;
    }

    public static void setInfoEmail(String infoEmail) {
        DataTransfer.infoEmail = infoEmail;
    }

    public static String getInfoCreditFile() {
        return infoCreditFile;
    }

    public static void setInfoCreditFile(String infoCreditFile) {
        DataTransfer.infoCreditFile = infoCreditFile;
    }

    public static String getInfoAccessPassword() {
        return infoAccessPassword;
    }

    public static void setInfoAccessPassword(String infoAccessPassword) {
        DataTransfer.infoAccessPassword = infoAccessPassword;
    }

    public static String getInfoRecordPassword() {
        return infoRecordPassword;
    }

    public static void setInfoRecordPassword(String infoRecordPassword) {
        DataTransfer.infoRecordPassword = infoRecordPassword;
    }

    public static String getInfoUnits() {
        return infoUnits;
    }

    public static void setInfoUnits(String infoUnits) {
        DataTransfer.infoUnits = infoUnits;
    }

    public static String getInfoUnitsExpireDate() {
        return infoUnitsExpireDate;
    }

    public static void setInfoUnitsExpireDate(String infoUnitsExpireDate) {
        DataTransfer.infoUnitsExpireDate = infoUnitsExpireDate;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        DataTransfer.token = token;
    }

    public static String getInfoNumber() {
        return infoNumber;
    }

    public static void setInfoNumber(String infoNumber) {
        DataTransfer.infoNumber = infoNumber;
    }

    public static String getInfoPassword() {
        return infoPassword;
    }

    public static void setInfoPassword(String infoPassword) {
        DataTransfer.infoPassword = infoPassword;
    }

    public static String getFileUrl() {
        return DataTransfer.fileUrl;
    }

public static void setFileUrl(String fileUrl) {
        DataTransfer.fileUrl = fileUrl;
    }

    public static String getFileName() {
        return DataTransfer.fileName;
    }

    public static void setFileName(String fileName) {
        DataTransfer.fileName = fileName;
    }

    public static String getFilePath() {
        return filePath;
    }

    public static void setFilePath(String filePath) {
        DataTransfer.filePath = filePath;
    }

    public static String getFileType() {
        return DataTransfer.fileType;
    }

    public static void setFileType(String fileType) {
        DataTransfer.fileType = fileType;
    }

    public static String getThisWhat() {
        return thisWhat;
    }

    public static void setThisWhat(String thisWhat) {
        DataTransfer.thisWhat = thisWhat;
    }

    public static void reset(){
        setErrorNoInternetShowing(false);
        setProgressViewShowing(false);
        setUsername(null);
        setUid(null);
        setToken(null);
        setInfoNumber(null);
        setInfoPassword(null);
        setInfoName(null);
        setInfoOrganization(null);
        setInfoContactName(null);
        setInfoPhones(null);
        setInfoInvoiceName(null);
        setInfoInvoiceAddress(null);
        setInfoFax(null);
        setInfoEmail(null);
        setInfoCreditFile(null);
        setInfoAccessPassword(null);
        setInfoRecordPassword(null);
        setInfoUnits(null);
        setInfoUnitsExpireDate(null);
        setFileUrl(null);
        setFileName(null);
        setFilePath(null);
        setFileType(null);
        setThisWhat(null);
    }
}

