package com.mordechay.yemotapp.ui.programmatically.list_for_securing_login_log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.interfaces.securingListOnItemActionClickListener;

import java.util.List;

public class SessionListCustomAdapter extends RecyclerView.Adapter<SessionListCustomAdapter.MyCustomViewHolder> {

    private List<SecuringSessionItem> itemList;
    private securingListOnItemActionClickListener listener;

    public SessionListCustomAdapter(List<SecuringSessionItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public MyCustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_securing_session, parent, false);
        return new MyCustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCustomViewHolder holder, int position) {
        SecuringSessionItem item = itemList.get(position);
        holder.txtToken.setText(item.getToken());
        holder.txtActive.setText(item.getActive());
        holder.txtSelectedDID.setText(item.getSelectedDID());
        holder.txtRemoteIP.setText(item.getRemoteIP());
        holder.txtSessionType.setText(item.getSessionType());
        holder.txtCreateTime.setText(item.getCreateTime());
        holder.txtLastRequest.setText(item.getLastRequest());
        holder.txtDoubleAuthStatus.setText(item.getDoubleAuthStatus());


        if (listener != null) {
            holder.setOnItemActionClickListener(listener);
        }
    }


    public void setOnItemActionClickListener(securingListOnItemActionClickListener listener) {
        this.listener = listener;
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class MyCustomViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView txtToken;
        MaterialTextView txtActive;
        MaterialTextView txtSelectedDID;
        MaterialTextView txtRemoteIP;
        MaterialTextView txtSessionType;
        MaterialTextView txtCreateTime;
        MaterialTextView txtLastRequest;
        MaterialTextView txtDoubleAuthStatus;

        Button btnDelete;

        securingListOnItemActionClickListener listener;



        public MyCustomViewHolder(@NonNull View itemView) {
            super(itemView);
            txtToken = itemView.findViewById(R.id.txtTokenHint);
            txtActive = itemView.findViewById(R.id.txtActive);
            txtSelectedDID = itemView.findViewById(R.id.txtSelectedDID);
            txtRemoteIP = itemView.findViewById(R.id.txtRemoteIP);
            txtSessionType = itemView.findViewById(R.id.txtSessionType);
            txtCreateTime = itemView.findViewById(R.id.txtCreateTime);
            txtLastRequest = itemView.findViewById(R.id.txtLastRequest);
            txtDoubleAuthStatus = itemView.findViewById(R.id.txtDoubleAuthStatus);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemActionClick(getAdapterPosition());
                    }
                }
            });
        }

        public void setOnItemActionClickListener(securingListOnItemActionClickListener listener) {
            this.listener = listener;
        }
    }

}

