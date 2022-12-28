package com.mordechay.yemotapp.data;

public class Constants {
    public static final String DEFAULT_SHARED_PREFERENCES = "User";
    public static final String URL_HOME = "https://www.call2all.co.il/ym/api/";
    public static final String URL_GET_UNITS_HISTORY = URL_HOME + "GetTransactions?token="+ DataTransfer.getToken();
    public static final String URL_DOWNLOAD_FILE = URL_HOME + "DownloadFile?token="+ DataTransfer.getToken();
    public static final String URL_SEND_SMS = URL_HOME + "DownloadFile?token="+ DataTransfer.getToken();
    public static final String URL_TRANSFER_UNITS = URL_HOME + "TransferUnits?token="+ DataTransfer.getToken();


    // TODO: send error to server, set url to send error
    public static final String ERROR_URL = "http://yemotapp.com/api/exception.php?"+ "username=" + DataTransfer.getUsername()+ "&uid=" +DataTransfer.getUid() +"&message=";
}
