package com.mordechay.yemotapp.ui.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mordechay.yemotapp.data.data;
import com.mordechay.yemotapp.network.VolleyCallback;
import com.mordechay.yemotapp.network.uploadFile;
import com.mordechay.yemotapp.ui.programmatically.list.CustomAdapter;
import com.mordechay.yemotapp.ui.programmatically.list.DataModel;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.ui.programmatically.list.newList;
import com.mordechay.yemotapp.network.sendApiRequest;
import com.mordechay.yemotapp.interfaces.IOnBackPressed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class filseExplorerFragment extends Fragment implements AdapterView.OnItemClickListener, AbsListView.MultiChoiceModeListener, sendApiRequest.RespondsListener, SwipeRefreshLayout.OnRefreshListener, DialogInterface.OnClickListener, IOnBackPressed {



    String urlHome;
    String token;
    String urlInfo;
    String urlStart;
    String url;
    String urlAction;
    String urlUpdateExtFolder;
    String urlHomeUploadFile;

    ArrayList<String> urlStack;
    String thisWhat = "/";
    ArrayList<String> thisWhatStack;

    String whatList;

    boolean isCopy = false;

    SharedPreferences sp;

    ListView list;
    ArrayList<DataModel> adapter;

    ArrayList<String> aryImage;
    ArrayList<String> aryName;
    ArrayList<String> aryExtType;
    ArrayList<String> aryExtTitle;
    ArrayList<String> aryFileType;
    ArrayList<String> aryWhat;
    ArrayList<String> aryTypeFile;

    ActionMode actMode;

    SwipeRefreshLayout swprl;

    MaterialAlertDialogBuilder dialog;
    EditText edtDialog;


    Menu menu;
    boolean onBack;
    private MaterialAlertDialogBuilder rnmDialog;
    private EditText edtRenameDialog;
    private String renameWhatString;
    private ArrayList<Integer> renameWhatList;

    long downloadID;


    public filseExplorerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_filse_explorer, container, false);

        setHasOptionsMenu(true);
        sp = getActivity().getSharedPreferences("User", 0);
        token = sp.getString("token", "");

        swprl = v.findViewById(R.id.swipeRefresh);
        swprl.setOnRefreshListener(this);
        swprl.setRefreshing(true);

        urlHome = "https://www.call2all.co.il/ym/api/GetIVR2Dir?token=" + token + "&orderBy=name&orderDir=asc&path=";
        urlStart = urlHome + "/";
        urlAction = "https://www.call2all.co.il/ym/api/FileAction?token=" + token;
        urlUpdateExtFolder = "https://www.call2all.co.il/ym/api/UpdateExtension?token=" + token;
        urlHomeUploadFile = "https://www.call2all.co.il/ym/api/UploadFile?token=" + token;

        url = urlStart;
        urlStack = new ArrayList();
        urlStack.add(url);
        thisWhatStack = new ArrayList();
        thisWhatStack.add(thisWhat);
        list = v.findViewById(R.id.list1111);
        list.setOnItemClickListener(this);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        list.setMultiChoiceModeListener(this);

        new sendApiRequest(getActivity(), this, "url", url);


        return v;
    }


    public void refresh() {
        swprl.setRefreshing(true);
        new sendApiRequest(getActivity(), this, "url", url);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(aryTypeFile.get(i).equals("DIR")) {
            url = urlHome + aryWhat.get(i);
            urlStack.add(url);
            thisWhat = aryWhat.get(i);
            thisWhatStack.add(thisWhat);
            refresh();
        } else {
            downloadFile(data.URL_DOWNLOAD_FILE + aryWhat.get(i));
        }
    }


    @Override
    public void onSuccess(String result, String type) {
        if (type.equals("url")) {
            if (actMode != null) {
                actMode.finish();
            }
            adapter = new ArrayList();
            try {
                aryImage = new ArrayList();
                aryName = new ArrayList();
                aryExtType = new ArrayList();
                aryExtTitle = new ArrayList();
                aryFileType = new ArrayList();
                aryWhat = new ArrayList();
                aryTypeFile = new ArrayList<>();


                JSONObject jsonObject = new JSONObject(result);

                if (!jsonObject.isNull("dirs") | !jsonObject.isNull("files")) {
                    if (!jsonObject.isNull("dirs")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("dirs");

                        for (int i = 1; i <= jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i - 1);
                            aryImage.add(String.valueOf(R.drawable.ic_baseline_folder_open_24));
                            if (!jsonObject1.isNull("name")) {
                                aryName.add(jsonObject1.getString("name"));
                            } else {
                                aryName.add("");
                            }
                            if (!jsonObject1.isNull("extType")) {
                                aryExtType.add(jsonObject1.getString("extType"));
                            } else {
                                aryExtType.add("");
                            }
                            if (!jsonObject1.isNull("extTitle")) {
                                aryExtTitle.add(jsonObject1.getString("extTitle"));
                            } else {
                                aryExtTitle.add("");
                            }
                            if (!jsonObject1.isNull("fileType")) {
                                aryFileType.add(jsonObject1.getString("fileType"));
                            } else {
                                aryFileType.add("");
                            }
                            if (!jsonObject1.isNull("what")) {
                                aryWhat.add(jsonObject1.getString("what"));
                            } else {
                                aryWhat.add("");
                            }
                            aryTypeFile.add("DIR");


                        }
                    }


                    if (!jsonObject.isNull("files")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("files");

                        for (int i = 1; i <= jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i - 1);
                            aryImage.add(String.valueOf(R.drawable.ic_baseline_audio_file_24));


                            if (!jsonObject1.isNull("name")) {
                                aryName.add(jsonObject1.getString("name"));
                            } else {
                                aryName.add("");
                            }
                            if (!jsonObject1.isNull("fileType")) {
                                aryExtType.add(jsonObject1.getString("fileType"));
                            } else {
                                aryExtType.add("");
                            }
                            if (!jsonObject1.isNull("what")) {
                                aryWhat.add(jsonObject1.getString("what"));
                            } else {
                                aryWhat.add("");
                            }

                            if(aryExtType.get(i-1).equals("") || aryExtType.get(i-1).isEmpty()){
                            aryTypeFile.add("FILE");
                            }else{
                                aryTypeFile.add(aryExtType.get(i-1));
                            }
                        }
                    }

                    ArrayList<ArrayList<String>> aryyyyyyy = new ArrayList<ArrayList<String>>();
                    aryyyyyyy.add(aryImage);
                    aryyyyyyy.add(aryName);
                    aryyyyyyy.add(aryExtType);
                    aryyyyyyy.add(aryExtTitle);
                    aryyyyyyy.add(aryFileType);
                    aryyyyyyy.add(aryWhat);




                    CustomAdapter csta = new CustomAdapter(this.getContext(), new newList().getAdapter(getActivity(), aryyyyyyy));
                    list.setAdapter(csta);

                }
            } catch (JSONException e) {
                Log.e("error json parse", result + "|" + urlInfo);
                e.printStackTrace();
            }
        } else if (type.equals("action")) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getBoolean("success")) {
                    Toast.makeText(getActivity(), "הפעולה בוצעה בהצלחה", Toast.LENGTH_LONG).show();
                    refresh();
                }
            } catch (JSONException e) {
                Log.e("error json parse", result + "|" + urlInfo);
                e.printStackTrace();
            }
            refresh();
        }else if(type.equals("urlDownloadFile")){

        }
        swprl.setRefreshing(false);
    }

    @Override
    public void onFailure(int responseCode, String responseMessage) {
        Log.e(String.valueOf(responseCode), responseMessage);
        swprl.setRefreshing(false);
    }


    public void action(String action) {
        String whatString = createWhatString();
        if (!whatString.isEmpty()) {
            if (action.equals("delete")) {
                String _urlAction = urlAction + "&action=delete" + whatString;
                Log.e("urlAction", _urlAction);
                swprl.setRefreshing(true);
                new sendApiRequest(getActivity(), this, "action", _urlAction);
            } else if (action.equals("move")) {
                whatList = whatString;
                isCopy = false;
                menu.getItem(2).setVisible(true);
            } else if (action.equals("copy")) {
                whatList = whatString;
                isCopy = true;
                menu.getItem(2).setVisible(true);
            } else if (action.equals("rename")) {
                rename(getArrayListSelected());
            }
        }
        if (action.equals("paste")) {
            Log.e("testtttttt", whatList);
            String act;
            if (isCopy) {
                act = "copy";
            } else {
                act = "move";
            }
            swprl.setRefreshing(true);
            String _urlAction = urlAction + "&action=" + act + whatList + "&target=" + thisWhat;
            Log.e("urlAction", _urlAction);
            new sendApiRequest(getActivity(), this, "action", _urlAction);
            isCopy = false;
            menu.getItem(2).setVisible(false);
        } else if (action.equals("createFolder")) {
            swprl.setRefreshing(true);
            String _urlCreatedFolder = urlUpdateExtFolder + "&path=" + thisWhat + "/" + edtDialog.getText();
            new sendApiRequest(getActivity(), this, "action", _urlCreatedFolder);
        }
    }




    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.upload_file:
                uploadFile();

                break;
            case R.id.created_folder:
                createFolderDialog();
                break;
            case R.id.paste:
                action("paste");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadFile() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a file"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedFileUri = data.getData();
            String selectedFilePath = selectedFileUri.getPath();
            String urlUpload = urlHomeUploadFile + "&path=" + "ivr2:"+thisWhat + "abc.txt";
            Log.e("urlUpload", urlUpload);
            File fl = new File(selectedFilePath);
            try {
                uploadFile.uploadFile(token, thisWhat, new File(selectedFilePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void onRefresh() {
        refresh();
    }


    public ArrayList<Integer> getArrayListSelected() {
        ArrayList listArray = new ArrayList<Integer>();


        SparseBooleanArray checked = list.getCheckedItemPositions();
        int size = checked.size(); // number of name-value pairs in the array
        for (int i = 0; i < size; i++) {
            int key = checked.keyAt(i);
            boolean value = checked.get(key);
            if (value)
                listArray.add(key);
        }
        return listArray;
    }

    public String createWhatString() {
        ArrayList listArray = getArrayListSelected();
        String whatListString = "";

        for (int i = 0; i < listArray.size(); i++) {

            whatListString = whatListString + "&what" + i + "=" + aryWhat.get((Integer) listArray.get(i));

        }
        Log.e("list", whatListString);
        return whatListString;
    }


    public void createFolderDialog() {
        edtDialog = new EditText(getActivity());
        dialog = new MaterialAlertDialogBuilder(getActivity())
                .setTitle("יצירת תיקייה")
                .setMessage("אנא הזן שם לתיקייה")
                .setView(edtDialog)
                .setPositiveButton("אישור", this)
                .setNegativeButton("ביטול", null);
        dialog.show();
    }


    private void rename(ArrayList<Integer> what) {
        renameWhatList = what;
        renameWhatString = createWhatString();
        edtRenameDialog = new EditText(getActivity());
        edtRenameDialog.setText(String.valueOf(aryName.get(what.get(0))));
        rnmDialog = new MaterialAlertDialogBuilder(getActivity())
                .setTitle("שינוי שם")
                .setMessage("נבחרו " + what.size() + " קבצים לשינוי שם." + "\n" +"\n" + "אנא הזן שם:")
                .setView(edtRenameDialog)
                .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        applyRename();
                    }
                })
                .setNegativeButton("ביטול", null);
        rnmDialog.show();
    }


    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        action("createFolder");
    }

    private void applyRename() {
        if(renameWhatList.size() == 1){
            String edtRenameWhatText = edtRenameDialog.getText().toString();
            String _urlAction = urlAction + "&action=" + "move" + renameWhatString + "&target=ivr2:" + thisWhat + edtRenameWhatText;
            Log.e("urlAction", _urlAction);
            new sendApiRequest(getActivity(), this, "action", _urlAction);
        }else{
            for(int i = 0; i < renameWhatList.size(); i++){
                String edtRenameWhatText = edtRenameDialog.getText().toString() + " (" + (i+1) + ")";
                String _urlAction = urlAction + "&action=" + "move" + renameWhatString + "&target=ivr2:" + thisWhat + edtRenameWhatText;
                Log.e("urlAction", _urlAction);
                new sendApiRequest(getActivity(), this, "action", _urlAction);
            }
        }
    }


    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        // Here you can do something when items are selected/de-selected,
        // such as update the title in the CAB
        ArrayList<Integer> cob = getArrayListSelected();
        mode.setTitle(String.valueOf(cob.size()) + " נבחרו");
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        // Respond to clicks on the actions in the CAB
        switch (item.getItemId()) {
            case R.id.delete:
                action("delete");
                mode.finish();
                return true;
            case R.id.move:
                action("move");
                mode.finish();
                return true;
            case R.id.copy:
                action("copy");
                mode.finish();
                return true;
            case R.id.rename:
                action("rename");
                mode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Inflate the menu for the CAB
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_action_bar_cab, menu);
        actMode = mode;
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        // Here you can make any necessary updates to the activity when
        // the CAB is removed. By default, selected items are deselected/unchecked.
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        // Here you can perform updates to the CAB due to
        // an <code><a href="/reference/android/view/ActionMode.html#invalidate()">invalidate()</a></code> request
        return false;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.getItem(0).setVisible(true);
        menu.getItem(1).setVisible(true);
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }


    public boolean onBackPressed() {
        if (urlStack.size() <= 1) {
return false;
        } else {
            url = urlStack.get(urlStack.size() - 2);
            thisWhat = thisWhatStack.get(urlStack.size() - 2);
            Log.e("test", url);
            urlStack.remove(urlStack.size() - 1);
            thisWhatStack.remove(thisWhatStack.size() - 1);
            refresh();
return true;
        }
    }


    public void downloadFile(String url) {
        Toast.makeText(getActivity(), "ההורדה מתבצעת.", Toast.LENGTH_SHORT).show();
        DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri Download_Uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);


        //Set the title of this download, to be displayed in notifications (if enabled).
        request.setTitle("מוריד קובץ: " + url.substring(url.lastIndexOf("/") + 1));
        //Set a description of this download, to be displayed in notifications (if enabled)

//Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,url.substring(url.lastIndexOf("/")+1));


        //set then click on notification to open the download file
        request.allowScanningByMediaScanner();

        //set the notification visibility to VISIBILITY_VISIBLE_NOTIFY_COMPLETED. This will ensure that the download shows in the notifications while it's in progress, and that it is automatically removed from the notification drawer once it has been completed.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setShowRunningNotification(true);

        request.setMimeType("audio/*");

        downloadID =manager.enqueue(request);
}
}