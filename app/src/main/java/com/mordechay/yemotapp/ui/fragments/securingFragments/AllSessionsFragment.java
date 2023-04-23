package com.mordechay.yemotapp.ui.fragments.securingFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.data.DataTransfer;
import com.mordechay.yemotapp.interfaces.OnRespondsYmtListener;
import com.mordechay.yemotapp.interfaces.securingListOnItemActionClickListener;
import com.mordechay.yemotapp.network.Network;
import com.mordechay.yemotapp.network.SendRequestForYemotServer;
import com.mordechay.yemotapp.ui.programmatically.list.CustomAdapter;
import com.mordechay.yemotapp.ui.programmatically.list_for_securing_login_log.SecuringSessionItem;
import com.mordechay.yemotapp.ui.programmatically.list_for_securing_login_log.SessionListCustomAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AllSessionsFragment extends Fragment implements View.OnClickListener, OnRespondsYmtListener, SwipeRefreshLayout.OnRefreshListener, CustomAdapter.ViewHolder.ClickListener, securingListOnItemActionClickListener {
    private SendRequestForYemotServer snd;
    private SwipeRefreshLayout swprl;
    private String token;
    private RecyclerView recyclerView;
    private SessionListCustomAdapter adapter;
    private Button btnKillAllSessions;
    private ArrayList<SecuringSessionItem> lst;

    public AllSessionsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_securing_all_sessions, container, false);

        token = DataTransfer.getToken();

        swprl = v.findViewById(R.id.LoginLog_SwipeRefresh);
        swprl.setOnRefreshListener(this);
        swprl.setRefreshing(true);


        btnKillAllSessions = v.findViewById(R.id.kill_all_session);
        btnKillAllSessions.setOnClickListener(this);

        recyclerView = v.findViewById(R.id.LoginLog_recycler);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        snd = SendRequestForYemotServer.getInstance(getActivity(), this);
        snd.addRequestAndSend(Network.GET_ALL_SESSIONS, Constants.URL_SECURING_GET_SESSION + DataTransfer.getToken());

        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.kill_all_session) {
            snd.addRequestAndSend(Network.KILL_ALL_SESSIONS, Constants.URL_SECURING_KILL_ALL_SESSIONS + DataTransfer.getToken());
        }
    }

    @Override
    public void onSuccess(String result, int type) throws JSONException {
        JSONObject jsb = new JSONObject(result);

        if (jsb.getString("responseStatus").equalsIgnoreCase("OK")) {
            if (type == Network.GET_ALL_SESSIONS) {
                int sessionCount = jsb.getInt("count");
                if (sessionCount == 0) {
                    btnKillAllSessions.setVisibility(View.GONE);
                    swprl.setRefreshing(true);
                    return;
                } else {
                    btnKillAllSessions.setVisibility(View.VISIBLE);
                }
                JSONArray jsa = jsb.getJSONArray("data");
                lst = new ArrayList<>();
                for (int i = 0; i < jsa.length(); i++) {
                    JSONObject jsb2 = jsa.getJSONObject(i);
                    lst.add(new SecuringSessionItem(
                            jsb2.isNull("id") ? "לא נמצא מידע" : jsb2.getString("id"),
                            jsb2.isNull("token") ? "לא נמצא מידע" : jsb2.getString("token"),
                            jsb2.isNull("active") ? "לא נמצא מידע" : jsb2.getString("active"),
                            jsb2.isNull("remoteIP") ? "לא נמצא מידע" : jsb2.getString("remoteIP"),
                            jsb2.isNull("selectedDID") ? "לא נמצא מידע" : jsb2.getString("selectedDID"),
                            jsb2.isNull("sessionType") ? "לא נמצא מידע" : jsb2.getString("sessionType"),
                            jsb2.isNull("createTime") ? "לא נמצא מידע" : jsb2.getString("createTime"),
                            jsb2.isNull("lastRequest") ? "לא נמצא מידע" : jsb2.getString("lastRequest"),
                            jsb2.isNull("doubleAuthStatus") ? "לא נמצא מידע" : jsb2.getString("doubleAuthStatus")
                    ));
                }

                SessionListCustomAdapter myca = new SessionListCustomAdapter(lst);
                myca.setOnItemActionClickListener(this);
                recyclerView.setAdapter(myca);
                swprl.setRefreshing(false);
            } else if (type == Network.KILL_ALL_SESSIONS) {
                swprl.setRefreshing(true);
                onRefresh();
            } else if (type == Network.KILL_SESSION) {
                swprl.setRefreshing(true);
                onRefresh();
            }
        } else {
            btnKillAllSessions.setVisibility(View.GONE);
            swprl.setRefreshing(false);
        }
    }

    @Override
    public void onFailure(String url, int responseCode, String responseMessage) {

    }

    @Override
    public void onRefresh() {
        snd.addRequestAndSend(Network.GET_ALL_SESSIONS, Constants.URL_SECURING_GET_SESSION + DataTransfer.getToken());
    }

    @Override
    public void onItemClicked(int position) {

    }

    @Override
    public boolean onItemLongClicked(int position) {
        return false;
    }

    @Override
    public void onItemActionClick(int position) {
        snd.addRequestAndSend(Network.KILL_SESSION, Constants.URL_SECURING_KILL_SESSION + DataTransfer.getToken() + "&SessionId=" + lst.get(position).getId());
    }
}