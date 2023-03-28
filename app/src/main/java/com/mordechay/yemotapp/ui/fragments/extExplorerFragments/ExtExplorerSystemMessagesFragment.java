package com.mordechay.yemotapp.ui.fragments.extExplorerFragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.data.filter;
import com.mordechay.yemotapp.network.OnRespondsYmtListener;
import com.mordechay.yemotapp.network.SendRequestForYemotServer;
import com.mordechay.yemotapp.ui.activitys.EditExtFileActivity;
import com.mordechay.yemotapp.ui.programmatically.list.CustomAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ExtExplorerSystemMessagesFragment extends Fragment implements AbsListView.MultiChoiceModeListener, OnRespondsYmtListener, SwipeRefreshLayout.OnRefreshListener, CustomAdapter.ViewHolder.ClickListener {
    private filter flt;
    private String token;
    private String thisWhat;
    private SwipeRefreshLayout swprl;
    private String url;
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private final int NAME_POSITION = 0;
    private final int WHAT_POSITION = 4;
    private final int TYPE_FILE_POSITION_INFO = 0;
    private final int IS_EXISTS_POSITION_INFO = 1;
    private SharedPreferences spPref;
    private long downloadID;
    private ActionMode actionMode;
    private MaterialToolbar toolbar;

    public ExtExplorerSystemMessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ext_explorer_system_messages, container, false);

        token = DataTransfer.getToken();

        toolbar = requireActivity().findViewById(R.id.topAppBar);

        flt = new filter(getActivity());

        swprl = v.findViewById(R.id.ExtExplorerSystemMessages_SwipeRefresh);
        swprl.setOnRefreshListener(this);
        swprl.setRefreshing(true);

        thisWhat = DataTransfer.getThisWhat();
        if(thisWhat == null){
            thisWhat = "ivr2:/";
        }

        url = Constants.URL_GET_EXTENSION_CONTENT+ token + "&orderBy=name&orderDir=asc&path=" + thisWhat;

        recyclerView = v.findViewById(R.id.ExtExplorerSystemMessages_ext_recycler_view);
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
                    adapter = new CustomAdapter(this, R.layout.item_file_explorer_system_messages, new int[]{R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4, R.id.textView5});


                    JSONObject jsonObject = new JSONObject(result);

                    if (!jsonObject.isNull("messages")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("messages");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            Drawable image = flt.getTypeImage("wav");
                            String exists = "false";
                            if (!jsonObject1.isNull("exists")) {
                                exists = jsonObject1.getString("exists");
                            }
                            String name = "";
                            if (!jsonObject1.isNull("name")) {
                                name = jsonObject1.getString("name");
                            }
                            String size = "";
                            String mtime = "";
                            String durationStr = "";
                            String fileType = "";
                            if(exists.equals("true")) {
                                if (!jsonObject1.isNull("size")) {
                                    size = jsonObject1.getString("size");
                                }
                                if (!jsonObject1.isNull("mtime")) {
                                    mtime = jsonObject1.getString("mtime");
                                }
                                if (!jsonObject1.isNull("durationStr")) {
                                    durationStr = jsonObject1.getString("durationStr");
                                }
                                if (!jsonObject1.isNull("fileType")) {
                                    fileType = jsonObject1.getString("fileType");
                                }
                            }
                            String what = "";
                            if (!jsonObject1.isNull("what")) {
                                what = jsonObject1.getString("what");
                            }
                            adapter.addItem(image, new String[]{name, size, mtime, durationStr, what}, new String[]{fileType, exists});
                        }
                    }
                    if (getActivity() != null)
                        recyclerView.setAdapter(adapter);

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

    private void delete(){
        String whatString = createWhatString();
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

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        // Here you can do something when items are selected/de-selected,
        // such as update the title in the CAB
        ArrayList<Integer> cob = getArrayListSelected();
        mode.setTitle(cob.size() + getString(R.string.selected));
    }


    public void downloadFile(String url, String MIME) {

        DownloadManager manager = (DownloadManager) requireActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri Download_Uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

        if (MIME.equals("text/*")) {
            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            ExtExplorerSystemMessagesFragment.DownloadCompleteReceiver receiver = new DownloadCompleteReceiver();
            requireContext().registerReceiver(receiver, filter);
            request.setDestinationInExternalFilesDir(requireContext(), Environment.DIRECTORY_DOWNLOADS, url.substring(url.lastIndexOf("/") + 1));
        } else {
            if (getActivity() != null)
                Toast.makeText(getActivity(), "ההורדה מתבצעת.", Toast.LENGTH_SHORT).show();

            //Set the title of this download, to be displayed in notifications (if enabled).
            request.setTitle(url.substring(url.lastIndexOf("/") + 1));
            //Set the local destination for the downloaded file to a path within the application's external files directory
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, url.substring(url.lastIndexOf("/") + 1));
            //set then click on notification to open the download file
            request.allowScanningByMediaScanner();
            //set the notification visibility to VISIBILITY_VISIBLE_NOTIFY_COMPLETED. This will ensure that the download shows in the notifications while it's in progress, and that it is automatically removed from the notification drawer once it has been completed.
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            request.setMimeType(MIME);
        }
        downloadID =manager.enqueue(request);
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(actionMode != null){
            actionMode.finish();
        }
        DataTransfer.setThisWhat(thisWhat);
    }












    @Override
    public void onItemClicked(int position) {
            if (actionMode != null) {
                if(adapter.getItem(position).getTxtInfo()[IS_EXISTS_POSITION_INFO].equals("true")) {
                    toggleSelection(position);
                }else{
                    Toast.makeText(getActivity(), "לא נין לבחור קובץ שאינו קיים", Toast.LENGTH_SHORT).show();
                }
            } else {
                if(adapter.getItem(position).getTxtInfo()[IS_EXISTS_POSITION_INFO].equals("true")) {
                    DataTransfer.setFileUrl(Constants.URL_DOWNLOAD_FILE + DataTransfer.getToken() + "&path=" + adapter.getItem(position).getTxt()[WHAT_POSITION]);
                DataTransfer.setFileName(adapter.getItem(position).getTxt()[NAME_POSITION]);
                DataTransfer.setFilePath(thisWhat + "/" + adapter.getItem(position).getTxt()[NAME_POSITION]);
                DataTransfer.setFileType(flt.getTypeMIME(adapter.getItem(position).getTxtInfo()[TYPE_FILE_POSITION_INFO]));
                downloadFile(Constants.URL_DOWNLOAD_FILE + DataTransfer.getToken() + "&path=" + adapter.getItem(position).getTxt()[WHAT_POSITION], flt.getTypeMIME(adapter.getItem(position).getTxtInfo()[TYPE_FILE_POSITION_INFO]));
            }else {
                    Toast.makeText(getActivity(), "לא נין להוריד קובץ שאינו קיים", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if(adapter.getItem(position).getTxtInfo()[IS_EXISTS_POSITION_INFO].equals("true")) {
            if (actionMode == null && getActivity() != null) {
                actionMode = getActivity().startActionMode(this);
            }

            toggleSelection(position);

            return true;
        }else{
            Toast.makeText(getActivity(), "לא נין לבחור קובץ שאינו קיים", Toast.LENGTH_SHORT).show();
            return true;
        }
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
        inflater.inflate(R.menu.menu_system_messages_action_bar_cab, menu);
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
        if(item.getItemId() == R.id.delete) {
            delete();
            mode.finish();
            return true;
        }else{
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