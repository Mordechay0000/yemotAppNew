package com.mordechay.yemotapp.data;

import com.mordechay.yemotapp.R;

public class Constants {
    public static final int DEFAULT_VIEW_CHECKING_ACCOUNT_DETAILS = R.layout.view_for_activity_checking_account_details;
    public static final String DEFAULT_SHARED_PREFERENCES = "User";
    public static final String DEFAULT_SHARED_PREFERENCES_DATA = "data";
    public static final String DEFAULT_SHARED_PREFERENCES_THIS_SYSTEM = "ThisSystem";
    public static final String DEFAULT_SHARED_PREFERENCES_FILTER = "filter";
    //TODO: CHANGE URL AND PATH
    public static final String URL_DOWNLOAD_RESOURCES = "http://app.y-fox.com/api/download/getInfo.php";
    public static final String RESOURCES_PATH = "resources";
    public static final String URL_IS_USER_EXIT = "https://run.mocky.io/v3/1cee674f-94fe-4a2a-8cc4-868cc2eadf1f?";
    public static final String URL_DOWNLOAD_UPDATE_APP = "http://app.y-fox.com/api/download/app/apk";
    
    public static final String URL_HOME = "https://www.call2all.co.il/ym/api/";
    public static final String URL_LOGIN = URL_HOME + "Login?";
    public static final String URL_GET_UNITS_HISTORY = URL_HOME + "GetTransactions?token=";
    public static final String URL_GET_EXTENSION_CONTENT = URL_HOME + "GetIVR2Dir?token=";
    public static final String URL_FILE_ACTION = URL_HOME + "FileAction?token=";
    public static final String URL_UPDATE_EXTENSION = URL_HOME + "UpdateExtension?token=";
    public static final String URL_UPLOAD_FILE = URL_HOME + "UploadFile?token=";
    public static final String URL_DOWNLOAD_FILE = URL_HOME + "DownloadFile?token=";
    public static final String URL_UPLOAD_TEXT_FILE = URL_HOME + "UploadTextFile?token=";
    public static final String URL_SEND_SMS = URL_HOME + "SendSms?token=";
    public static final String URL_TRANSFER_UNITS = URL_HOME + "TransferUnits?token=";
    public static final String URL_GET_CALLS = "https://www.call2all.co.il/ym/api/GetIncomingCalls?token=";
    public static final String URL_SIP_NEW_ACCOUNT = "https://private.call2all.co.il/ym/api/CreateSipAccount?token=";
    public static final String URL_SIP_GET_ACCOUNTS = "https://private.call2all.co.il/ym/api/GetSipAccountsInCustomer?token=" ;
    public static final String URL_SIP_PROTOCOL_TO_UDP = "https://private.call2all.co.il/ym/api/SipToUdp?token=";
    public static final String URL_SIP_PROTOCOL_TO_WSS = "https://private.call2all.co.il/ym/api/SipToWss?token=";
    public static final String URL_SIP_SETTING_CALLER_ID = "https://private.call2all.co.il/ym/api/EditCallerIdInSipAccount?token=";
    public static final String URL_SIP_REMOVE_ACCOUNTS = "https://private.call2all.co.il/ym/api/DeleteSipAccount?token=";
    public static final String URL_SECURING_GET_TOKEN_INFORMATION = URL_HOME + "ValidationToken?token=";
    public static final String URL_SECURING_DOUBLE_AUTH = URL_HOME + "DoubleAuth?token=";

    public static final String URL_SECURING_LOGIN_LOG = URL_HOME + "GetLoginLog?token=";
    public static final String URL_SECURING_GET_SESSION = URL_HOME + "GetAllSessions?token=";
    public static final String URL_SECURING_KILL_SESSION = URL_HOME + "KillSession?token=";
    public static final String URL_SECURING_KILL_ALL_SESSIONS = URL_HOME + "KillAllSessions?token=";




    public static final String URL_SPECIAL_ID_VALIDATION_CALLER_ID = URL_HOME + "ValidationCallerId?token=";
    public static final String URL_INCOMING_MINUTES = URL_HOME + "GetIncomingSum?token=";



    // TODO: send error to server, set url to send error
    public static final String ERROR_URL = "http://yemotapp.com/api/exception.php?"+ "username=" + DataTransfer.getUsername() + "&uid=" + DataTransfer.getUid() +"&message=";
}
