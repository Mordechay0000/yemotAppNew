package com.mordechay.yemotapp.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.interfaces.OnRespondsYmtListener;
import com.mordechay.yemotapp.network.Network;
import com.mordechay.yemotapp.network.SendRequestForYemotServer;
import com.mordechay.yemotapp.ui.programmatically.list.CustomAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class CallInSystemFragment extends Fragment implements OnRespondsYmtListener, SwipeRefreshLayout.OnRefreshListener, CustomAdapter.ViewHolder.ClickListener {


    private SendRequestForYemotServer snd;
    private String token;
    private String url;
    private RecyclerView recyclerView;
    private CustomAdapter adapter;

    private ArrayList<String> aryNumTo;
    private ArrayList<String> aryNumFrom;
    private ArrayList<String> aryNumTrans;
    private ArrayList<String> aryExt;
    private ArrayList<String> aryCallDur;
    private ArrayList<String> aryCallId;
    private String titleApp;
    private SwipeRefreshLayout swprl;


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

        titleApp = (String) requireActivity().getTitle();
        View v = inflater.inflate(R.layout.fragment_call_in_system, container, false);

        swprl = v.findViewById(R.id.CallInSystemSwipe);
        swprl.setOnRefreshListener(this);
        swprl.setRefreshing(true);

        token = DataTransfer.getToken();

        url = Constants.URL_GET_CALLS + token;

        recyclerView = v.findViewById(R.id.CallInSystemRecyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        snd = SendRequestForYemotServer.getInstance(getActivity(), this);
        snd.addRequestAndSend(Network.GET_CALLS, url);
        return v;
    }






    @Override
    public void onSuccess(String result, int type) {
        if(type == Network.GET_CALLS){
            /*if (actionMode != null) {
                actionMode.finish();
            }
             */
            try {
                adapter = new CustomAdapter(this, R.layout.item_file_explorer_manger_file, new int[]{R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4, R.id.textView5});

                JSONObject jsb = new JSONObject(result);
                try {
                    if(getActivity() != null) {
                        getActivity().setTitle(getString(R.string.active_calls_in_the_system) + "" + jsb.getString("callsCount"));
                    }
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
                ArrayList<String> aryImage = new ArrayList<>();


                for (int i = 0; i < jsa.length(); i++) {
                    JSONObject js = jsa.getJSONObject(i);
                    aryNumTo.add(js.getString("did"));
                    aryNumFrom.add(js.getString("callerIdNum"));
                    aryNumTrans.add(js.getString("transferFrom"));
                    aryExt.add(js.getString("path"));
                    aryCallDur.add(js.getString("duration"));
                    aryCallId.add(js.getString("id"));
                    aryImage.add(String.valueOf(R.drawable.ic_baseline_account_circle_70));
                }


                ArrayList<ArrayList<String>> aryy = new ArrayList<>();
                aryy.add(aryImage);
                aryy.add(aryNumTo);
                aryy.add(aryNumFrom);
                aryy.add(aryNumTrans);
                aryy.add(aryExt);
                aryy.add(aryCallDur);
try {
    //CustomAdapter csta = new CustomAdapter(this.getContext(), new newList().getAdapter(getActivity(), aryy));
    //list.setAdapter(csta);
} catch (Exception e) {
    e.printStackTrace();
}
            } catch (JSONException e) {
                Log.e("error parse json", e.getMessage());
            }
        }



        try {
            snd.addRequestAndSend(Network.GET_CALLS, url);
        }catch (NullPointerException e){
            Log.e("null", e.getMessage());
        }
    }

    @Override
    public void onFailure(String url, int responseCode, String responseMessage) {

    }




    @Override
    public void onDestroy() {
        requireActivity().setTitle(titleApp);
        super.onDestroy();
    }


    @Override
    public void onRefresh() {
        swprl.setRefreshing(true);
        if (getActivity() != null)
            snd.addRequestAndSend(Network.GET_CALLS, url);
    }

    @Override
    public void onItemClicked(int position) {

    }

    @Override
    public boolean onItemLongClicked(int position) {
        return false;
    }




    private void fooooooo(){

    }
}