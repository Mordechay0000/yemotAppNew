package com.mordechay.yemotapp.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.ui.programmatically.list.CustomAdapter;
import com.mordechay.yemotapp.ui.programmatically.list.DataModel;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.network.sendApiRequest;
import com.mordechay.yemotapp.ui.programmatically.list.newList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class CallInSystemFragment extends Fragment implements AbsListView.MultiChoiceModeListener, sendApiRequest.RespondsListener {





    String urlHome;
    String token;
    String urlInfo;
    String urlStart;
    String url;
    String urlAction;
    String urlUpdateExtFolder;

    ArrayList<String> urlStack;
    String thisWhat = "/";
    ArrayList<String> thisWhatStack;

    String whatList;

    boolean isCopy = false;

    ListView list;
    ArrayList<DataModel> adapter;

    ArrayList<String> aryNumTo;
    ArrayList<String> aryNumFrom;
    ArrayList<String> aryNumTrans;
    ArrayList<String> aryExt;
    ArrayList<String> aryCallDur;
    ArrayList<String> aryCallId;

    ActionMode actMode;

    SwipeRefreshLayout swprl;

    MaterialAlertDialogBuilder dialog;
    EditText edtDialog;
String titleApp;

    Menu menu;
    boolean onBack;
    private ArrayList<String> aryImage;


    public CallInSystemFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        titleApp = (String) getActivity().getTitle();
        View v = inflater.inflate(R.layout.fragment_call_in_system, container, false);


        token = DataTransfer.getToken();


        urlHome = "https://www.call2all.co.il/ym/api/GetIncomingCalls?token=" + token;

        url = urlHome;
        list = v.findViewById(R.id.list2222);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list.setMultiChoiceModeListener(this);
        new sendApiRequest(getActivity(), this, "url", url);
        return v;
    }






    @Override
    public void onSuccess(String result, String type) {
        if(type.equals("url")){
            adapter = new ArrayList();
            try {
                JSONObject jsb = new JSONObject(result);
                try {
                    getActivity().setTitle("שיחות פעילות במערכת: " + jsb.getString("callsCount"));
                }catch (NullPointerException e){
                    Log.e("null", e.getMessage());
                }
                JSONArray jsa;
                if (!jsb.isNull("calls")) {
                    jsa = jsb.getJSONArray("calls");
                } else {
                    jsa = new JSONArray();
                }
                aryNumTo = new ArrayList<>();
                aryNumFrom = new ArrayList<>();
                aryNumTrans = new ArrayList<>();
                aryExt = new ArrayList<>();
                aryCallDur = new ArrayList<>();
                aryCallId = new ArrayList<>();
                aryImage = new ArrayList<>();


                for (int i = 0; i < jsa.length(); i++) {
                    JSONObject js = jsa.getJSONObject(i);
                    aryNumTo.add(js.getString("did"));
                    aryNumFrom.add(js.getString("callerIdNum"));
                    aryNumTrans.add(js.getString("transferFrom"));
                    aryExt.add(js.getString("path"));
                    aryCallDur.add(js.getString("duration"));
                    aryCallId.add(js.getString("id"));
                    aryImage.add(String.valueOf(R.drawable.ic_baseline_audio_file_24));
                }


                ArrayList<ArrayList<String>> aryy = new ArrayList<ArrayList<String>>();
                aryy.add(aryImage);
                aryy.add(aryNumTo);
                aryy.add(aryNumFrom);
                aryy.add(aryNumTrans);
                aryy.add(aryExt);
                aryy.add(aryCallDur);
try {
    CustomAdapter csta = new CustomAdapter(this.getContext(), new newList().getAdapter(getActivity(), aryy));
    list.setAdapter(csta);
} catch (Exception e) {
    e.printStackTrace();
}
            } catch (JSONException e) {
                Log.e("error parse json", e.getMessage());
            }
        }



        try {
            new sendApiRequest(getActivity(), this, "url", url);
        }catch (NullPointerException e){
            Log.e("null", e.getMessage());
        }
    }

    @Override
    public void onFailure(int responseCode, String responseMessage) {

    }




    @Override
    public void onDestroy() {
        getActivity().setTitle(titleApp);
        super.onDestroy();
    }




    @Override
    public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {

    }


}