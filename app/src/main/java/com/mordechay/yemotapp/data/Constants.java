package com.mordechay.yemotapp.data;

public class Constants {
    public static final String DEFAULT_SHARED_PREFERENCES = "User";
    public static final String DEFAULT_SHARED_PREFERENCES_THIS_SYSTEM = "ThisSystem";
    public static final String URL_HOME = "https://www.call2all.co.il/ym/api/";
    public static final String URL_LOGIN = URL_HOME + "Login?";
    public static final String URL_GET_UNITS_HISTORY = URL_HOME + "GetTransactions?token=";
    public static final String URL_DOWNLOAD_FILE = URL_HOME + "DownloadFile?token=";
    public static final String URL_UPLOAD_TEXT_FILE = URL_HOME + "UploadTextFile?token=";
    public static final String URL_SEND_SMS = URL_HOME + "SendSms?token=";
    public static final String URL_TRANSFER_UNITS = URL_HOME + "TransferUnits?token=";
    public static final String URL_SIP_NEW_ACCOUNT = "https://private.call2all.co.il/ym/api/CreateSipAccount?token=";
    public static final String URL_SIP_GET_ACCOUNTS = "https://private.call2all.co.il/ym/api/GetSipAccountsInCustomer?token=" ;
    public static final String URL_SIP_PROTOCOL_TO_UDP = "https://private.call2all.co.il/ym/api/SipToUdp?token=";
    public static final String URL_SIP_PROTOCOL_TO_WSS = "https://private.call2all.co.il/ym/api/SipToWss?token=";
    public static final String URL_SIP_CHANGE_CALLER_ID = "https://private.call2all.co.il/ym/api/EditCallerIdInSipAccount?token=";
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
