package com.mordechay.yemotapp.ui.fragments.extExplorerFragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.data.filter;
import com.mordechay.yemotapp.interfaces.onBackPressedFilesExplorer;
import com.mordechay.yemotapp.interfaces.OnRespondsYmtListener;
import com.mordechay.yemotapp.network.SendRequestForYemotServer;
import com.mordechay.yemotapp.ui.activitys.EditExtFileActivity;
import com.mordechay.yemotapp.ui.programmatically.list.CustomAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

@SuppressLint("NonConstantResourceId")
public class ExtExplorerMangerFilesFragment extends Fragment implements MenuProvider, AbsListView.MultiChoiceModeListener, OnRespondsYmtListener, SwipeRefreshLayout.OnRefreshListener, DialogInterface.OnClickListener, onBackPressedFilesExplorer, CustomAdapter.ViewHolder.ClickListener {

private filter flt;
    String urlHome;
    String token;
    String url;
    String thisWhat;
    String whatList;

    boolean isCopy = false;

    RecyclerView recyclerView;
    
    SwipeRefreshLayout swprl;

    MaterialAlertDialogBuilder dialog;
    EditText edtDialog;


    Menu menu;
    private EditText edtRenameDialog;
    private String renameWhatString;
    private ArrayList<Integer> renameWhatList;

    long downloadID;
    private MaterialToolbar toolbar;
    private SharedPreferences spPref;
    private ActionMode actionMode;
    private CustomAdapter adapter;
    private final int NAME_POSITION = 0;
    private final int EXT_TYPE_POSITION = 1;
    private final int EXT_TITLE_POSITION = 2;
    private final int FILE_TYPE_POSITION = 3;
    private final int WHAT_POSITION = 4;
    private final int TYPE_FILE_POSITION_INFO = 0;

    public ExtExplorerMangerFilesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ext_explorer_manger_files, container, false);

        token = DataTransfer.getToken();

        toolbar = requireActivity().findViewById(R.id.topAppBar);
        requireActivity().addMenuProvider(this);

        flt = new filter(getActivity());

        swprl = v.findViewById(R.id.ExtExplorerMangerFiles_SwipeRefresh);
        swprl.setOnRefreshListener(this);
        swprl.setRefreshing(true);

        thisWhat = DataTransfer.getThisWhat();
        if(thisWhat == null){
            thisWhat = "ivr2:/";
        }

        urlHome =  Constants.URL_GET_EXTENSION_CONTENT+ token + "&orderBy=name&orderDir=asc&path=";
        url = urlHome + thisWhat;

        recyclerView = v.findViewById(R.id.ExtExplorerMangerFiles_ext_recycler_view);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        spPref = PreferenceManager.getDefaultSharedPreferences(requireContext());

        new SendRequestForYemotServer(getActivity(), this, "url", url);


        return v;
    }


    @Override
    public void onRefresh() {
        refresh();
    }
    public void refresh() {
        swprl.setRefreshing(true);
        if(getActivity() != null)
            new SendRequestForYemotServer(getActivity(), this, "url", url);
    }


    @Override
    public void onSuccess(String result, String type) {
        switch (type) {
            case "url":
                if (actionMode != null) {
                    actionMode.finish();
                }
                try {
                    adapter = new CustomAdapter(this, R.layout.item_file_explorer_manger_file, new int[]{R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4, R.id.textView5});


                    JSONObject jsonObject = new JSONObject(result);

                    if (!jsonObject.isNull("dirs") | !jsonObject.isNull("files")) {
                        if (!jsonObject.isNull("dirs")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("dirs");

                            for (int i = 1; i <= jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i - 1);
                                Drawable image = ResourcesCompat.getDrawable(getActivity().getResources(), R.drawable.ic_baseline_folder_open_24, getActivity().getTheme());
                                String name = "";
                                if (!jsonObject1.isNull("name")) {
                                    name = jsonObject1.getString("name");
                                }
                                String extType = "";
                                if (!jsonObject1.isNull("extType")) {
                                    extType = jsonObject1.getString("extType");
                                }
                                String extTitle = "";
                                if (!jsonObject1.isNull("extTitle")) {
                                    extTitle = jsonObject1.getString("extTitle");
                                }
                                String fileType = "";
                                if (!jsonObject1.isNull("fileType")) {
                                    fileType = jsonObject1.getString("fileType");
                                }
                                String what = "";
                                if (!jsonObject1.isNull("what")) {
                                    what = jsonObject1.getString("what");
                                }
                                String typeFile = "DIR";
                                adapter.addItem(image, new String[]{name, extType, extTitle, fileType, what}, new String[]{typeFile});
                            }
                        }
                        if (!jsonObject.isNull("files")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("files");

                            for (int i = 1; i <= jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i - 1);

                                String name = "";
                                if (!jsonObject1.isNull("name")) {
                                    name = jsonObject1.getString("name");
                                }
                                String extType = "";
                                if (!jsonObject1.isNull("fileType")) {
                                    extType = jsonObject1.getString("fileType");
                                }
                                String what = "";
                                if (!jsonObject1.isNull("what")) {
                                    what = jsonObject1.getString("what");
                                }
                                String typeFile = "";
                                if (extType.isEmpty()) {
                                    typeFile = "FILE";
                                } else {
                                    typeFile = extType;
                                }

                                Drawable image = flt.getTypeImage(name.substring(name.lastIndexOf(".") + 1));
                                String extTitle = "";
                                String fileType = "";
                                adapter.addItem(image, new String[]{name, extType, extTitle, fileType, what}, new String[]{typeFile});
                            }
                        }
                        if (!jsonObject.isNull("ini")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("ini");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                String name = "";
                                if (!jsonObject1.isNull("name")) {
                                    name = jsonObject1.getString("name");
                                }
                                String extType = "";
                                if (!jsonObject1.isNull("fileType")) {
                                    extType = jsonObject1.getString("fileType");
                                }
                                String what = "";
                                if (!jsonObject1.isNull("what")) {
                                    what = jsonObject1.getString("what");
                                }

                                String typeFile = "";
                                if (extType.isEmpty()) {
                                    typeFile = "ini";
                                } else {
                                    typeFile = extType;
                                }

                                Drawable image =
                                        flt.getTypeImage(name.
                                                substring(name.
                                                        lastIndexOf(".") + 1));
                                String extTitle = "";
                                String fileType = "";
                                adapter.addItem(image, new String[]{name, extType, extTitle, fileType, what}, new String[]{typeFile});
                            }
                        }
                        if(getActivity() != null) {
                            recyclerView.setAdapter(adapter);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "action":
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (!jsonObject.isNull("success") && jsonObject.getBoolean("success")) {
                        if(getActivity() != null)
                            Toast.makeText(getActivity(), "הפעולה בוצעה בהצלחה", Toast.LENGTH_LONG).show();
                        refresh();
                    }else if (!jsonObject.isNull("message") && jsonObject.getString("meddsge").equals("simultaneous file operation rejected")){
                        if(getActivity() != null)
                            Toast.makeText(getActivity(), "פעולת קובץ בו-זמנית נדחתה", Toast.LENGTH_LONG).show();
                    }else if(!jsonObject.isNull("message")){
                        if(getActivity() != null)
                            Toast.makeText(getActivity(), "שגיאה: \n \n " + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }else{
                        if(getActivity() != null)
                            Toast.makeText(getActivity(), "שגיאה לא ידועה: \n \n " + jsonObject.getString("responseStatus"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                refresh();
                break;


            case "urlDownloadFile":

                break;
        }
        swprl.setRefreshing(false);
    }

    @Override
    public void onFailure(String url, int responseCode, String responseMessage) {
        Log.e(String.valueOf(responseCode), responseMessage);
        swprl.setRefreshing(false);
    }


    public void action(String action) {
        String whatString = createWhatString();
        if (!whatString.isEmpty()) {
            switch (action) {
                case "delete":
                    if (spPref.getBoolean("delete", true)){
                        // Create the AlertDialog.Builder
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("פעולה");
                        builder.setMessage("האם אתה בטוח?");

                        // Add the buttons
                        builder.setPositiveButton("אישור", (dialog, id) -> {
                            // User clicked confirm button
                            String _urlAction = Constants.URL_FILE_ACTION + token + "&action=delete" + whatString;
                            Log.e("urlAction", _urlAction);
                            swprl.setRefreshing(true);
                            if(getActivity() != null)
                                new SendRequestForYemotServer(getActivity(), this, "action", _urlAction);
                        });
                        builder.setNegativeButton("ביטול", null);
                        // Create and show the AlertDialog
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }else {
                        String _urlAction = Constants.URL_FILE_ACTION + token + "&action=delete" + whatString;
                        Log.e("urlAction", _urlAction);
                        swprl.setRefreshing(true);
                        if(getActivity() != null)
                            new SendRequestForYemotServer(getActivity(), this, "action", _urlAction);
                    }
                    break;
                case "move":
                        whatList = whatString;
                    isCopy = false;
                    menu.getItem(2).setVisible(true);
                    break;
                case "copy":
                    whatList = whatString;
                    isCopy = true;
                    menu.getItem(2).setVisible(true);
                    break;
                case "rename":
                    rename(getArrayListSelected());
                    break;
            }
        }
        if (action.equals("paste")) {
            if (spPref.getBoolean("paste", false)){
                // Create the AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("פעולה");
                builder.setMessage("האם אתה בטוח?");

                // Add the buttons
                builder.setPositiveButton("אישור", (dialog, id) -> {
                    Log.e("testtttttt", whatList);
                    String act;
                    if (isCopy) {
                        act = "copy";
                    } else {
                        act = "move";
                    }
                    swprl.setRefreshing(true);
                    String _urlAction = Constants.URL_FILE_ACTION + token + "&action=" + act + whatList + "&target=" + thisWhat;
                    Log.e("urlAction", _urlAction);
                    if(getActivity() != null)
                        new SendRequestForYemotServer(getActivity(), this, "action", _urlAction);
                    isCopy = false;
                    menu.getItem(2).setVisible(false);
                });
                builder.setNegativeButton("ביטול", null);
                // Create and show the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
            Log.e("testtttttt", whatList);
            String act;
            if (isCopy) {
                act = "copy";
            } else {
                act = "move";
            }
            swprl.setRefreshing(true);
            String _urlAction = Constants.URL_FILE_ACTION + token + "&action=" + act + whatList + "&target=" + thisWhat;
            Log.e("urlAction", _urlAction);
                if(getActivity() != null)
                    new SendRequestForYemotServer(getActivity(), this, "action", _urlAction);
            isCopy = false;
            menu.getItem(2).setVisible(false);}
        } else if (action.equals("createFolder")) {
            swprl.setRefreshing(true);
            String _urlCreatedFolder = Constants.URL_UPDATE_EXTENSION + token + "&path=" + thisWhat + "/" + edtDialog.getText();
            if(getActivity() != null)
                new SendRequestForYemotServer(getActivity(), this, "action", _urlCreatedFolder);
        }
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
            String urlUpload = Constants.URL_UPLOAD_FILE + token + "&path=" + "ivr2:"+thisWhat + "abc.txt";
            Log.e("urlUpload", urlUpload);
            /*
            File fl = new File(selectedFilePath);
            try {
                uploadFile.uploadFile(token, thisWhat, new File(selectedFilePath));
            } catch (IOException e) {
                e.printStackTrace();
            }

             */
        }
    }


    public ArrayList<Integer> getArrayListSelected() {
        ArrayList<Integer> listArray = new ArrayList<>();


        SparseBooleanArray checked = adapter.getCheckedItemPositions();
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
        ArrayList<Integer> listArray = getArrayListSelected();
        StringBuilder whatListString = new StringBuilder();
        for (int i = 0; i < listArray.size(); i++) {

            whatListString.append("&what").append(i).append("=").append(adapter.getItem(listArray.get(i)).getTxt()[WHAT_POSITION]);

        }
        Log.e("list", whatListString.toString());
        return whatListString.toString();
    }


    public void createFolderDialog() {
        edtDialog = new EditText(getActivity());
        dialog = new MaterialAlertDialogBuilder(requireActivity())
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
        edtRenameDialog.setText(String.valueOf(adapter.getItem(what.get(0)).getTxt()[NAME_POSITION]));
        MaterialAlertDialogBuilder rnmDialog = new MaterialAlertDialogBuilder(requireActivity())
                .setTitle("שינוי שם")
                .setMessage("נבחרו " + what.size() + " קבצים לשינוי שם." + "\n" + "\n" + "אנא הזן שם:")
                .setView(edtRenameDialog)
                .setPositiveButton("אישור", (dialogInterface, i) -> applyRename())
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
            String _urlAction = Constants.URL_FILE_ACTION + token + "&action=" + "move" + renameWhatString + "&target=" + thisWhat + edtRenameWhatText;
            Log.e("urlAction", _urlAction);
            if(getActivity() != null)
                new SendRequestForYemotServer(getActivity(), this, "action", _urlAction);
        }else{
            for(int i = 0; i < renameWhatList.size(); i++){
                String edtRenameWhatText = edtRenameDialog.getText().toString() + " (" + (i+1) + ")";
                String _urlAction = Constants.URL_FILE_ACTION + token + "&action=" + "move" + renameWhatString + "&target=" + thisWhat + edtRenameWhatText;
                Log.e("urlAction", _urlAction);
                if(getActivity() != null)
                    new SendRequestForYemotServer(getActivity(), this, "action", _urlAction);
            }
        }
    }


    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        // Here you can do something when items are selected/de-selected,
        // such as update the title in the CAB
        ArrayList<Integer> cob = getArrayListSelected();
        mode.setTitle(cob.size() + getString(R.string.selected));
    }




    public boolean onBackPressedFilesExplorer() {
        String[] parts = thisWhat.split("/"); // פיצול המחרוזת למערך תתי מחרוזות על פי התו /
        String prefix = "ivr2:/";
        for (int i = 1; i < parts.length; i++) {
            parts[i] = prefix + String.join("/", Arrays.copyOfRange(parts, 1, i+1)) + "/";
        }
        if (parts.length <= 1) {
return false;
        } else {
            thisWhat = parts[parts.length - 2];
            url = urlHome + thisWhat;
            Log.e("test", url);
            refresh();
return true;
        }
    }



    public void downloadFile(String url, String MIME) {

        DownloadManager manager = (DownloadManager) requireActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri Download_Uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

        if(MIME.equals("text/*")) {
            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            DownloadCompleteReceiver receiver = new DownloadCompleteReceiver();
            requireContext().registerReceiver(receiver, filter);
            request.setDestinationInExternalFilesDir(requireContext(), Environment.DIRECTORY_DOWNLOADS,url.substring(url.lastIndexOf("/")+1));
        }else {
            if(getActivity() != null)
                Toast.makeText(getActivity(), "ההורדה מתבצעת.", Toast.LENGTH_SHORT).show();

            //Set the title of this download, to be displayed in notifications (if enabled).
            request.setTitle(url.substring(url.lastIndexOf("/") + 1));
            //Set the local destination for the downloaded file to a path within the application's external files directory
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,url.substring(url.lastIndexOf("/")+1));
            //set then click on notification to open the download file
            request.allowScanningByMediaScanner();
            //set the notification visibility to VISIBILITY_VISIBLE_NOTIFY_COMPLETED. This will ensure that the download shows in the notifications while it's in progress, and that it is automatically removed from the notification drawer once it has been completed.
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            request.setMimeType(MIME);
        }

        downloadID =manager.enqueue(request);
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menu.getItem(0).setVisible(true);
        menu.getItem(1).setVisible(true);
        this.menu = menu;
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.upload_file:
                uploadFile();
                return true;
            case R.id.created_folder:
                createFolderDialog();
                return true;
            case R.id.paste:
                action("paste");
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(actionMode != null){
            actionMode.finish();
        }
        DataTransfer.setThisWhat(thisWhat);
        requireActivity().removeMenuProvider(this);
    }












    @Override
    public void onItemClicked(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        } else {
            if(adapter.getItem(position).getTxtInfo()[TYPE_FILE_POSITION_INFO].equalsIgnoreCase("DIR")) {
                thisWhat = adapter.getItem(position).getTxt()[WHAT_POSITION];
                url = urlHome + thisWhat;
                refresh();
            } else {
                DataTransfer.setFileUrl(Constants.URL_DOWNLOAD_FILE + DataTransfer.getToken()  +"&path="+ adapter.getItem(position).getTxt()[WHAT_POSITION]);
                DataTransfer.setFileName(adapter.getItem(position).getTxt()[NAME_POSITION]);
                DataTransfer.setFilePath(thisWhat + "/"+ adapter.getItem(position).getTxt()[NAME_POSITION]);
                DataTransfer.setFileType(flt.getTypeMIME(adapter.getItem(position).getTxtInfo()[TYPE_FILE_POSITION_INFO]));
                downloadFile(Constants.URL_DOWNLOAD_FILE + DataTransfer.getToken() +"&path=" + adapter.getItem(position).getTxt()[WHAT_POSITION], flt.getTypeMIME(adapter.getItem(position).getTxtInfo()[TYPE_FILE_POSITION_INFO]));
            }
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (actionMode == null && getActivity() != null) {
            actionMode = getActivity().startActionMode(this);
        }

        toggleSelection(position);

        return true;
    }

    /**
     * Toggle the selection state of an item.
     *
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(count + getString(R.string.selected));
            actionMode.invalidate();
        }
    }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate the menu for the CAB
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_manger_file_action_bar_cab, menu);
            actionMode = mode;
            toolbar.setVisibility(View.GONE);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
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
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelection();
            actionMode = null;
            toolbar.setVisibility(View.VISIBLE);
        }





    public class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if(downloadId == downloadID)
            {
                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor cursor = manager.query(query);
                if(cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columnIndex)) {
                        @SuppressLint("Range") String uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        Uri downloadedUri = Uri.parse(uriString);
                        startActivity(new Intent(requireContext(), EditExtFileActivity.class).setAction(Intent.ACTION_EDIT).setData(downloadedUri));

                    }
                }
            }
        }

    }
    
}

