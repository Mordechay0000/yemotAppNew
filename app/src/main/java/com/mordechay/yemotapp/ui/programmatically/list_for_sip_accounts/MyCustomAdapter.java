package com.mordechay.yemotapp.ui.programmatically.list_for_sip_accounts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.interfaces.OnItemActionClickListener;

import java.util.List;

public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.MyCustomViewHolder> {

    private List<MyItem> itemList;
    private OnItemActionClickListener listener;

    public MyCustomAdapter(List<MyItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public MyCustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new MyCustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCustomViewHolder holder, int position) {
        MyItem item = itemList.get(position);
        holder.txtUsername.setText(item.getUsername());
        holder.txtPassword.setText(item.getPassword());
        holder.txtNumExtension.setText(item.getNumExtension());
        holder.txtProtocol.setText(item.getProtocol());
        holder.txtDate.setText(item.getDate());
        holder.txtCommittedSystem.setText(item.getCommittedSystem());
        holder.txtSpecialCallerID.setText(item.getSpecialCallerID());
        if (listener != null) {
            holder.setOnItemActionClickListener(listener);
        }
    }


    public void setOnItemActionClickListener(OnItemActionClickListener listener) {
        this.listener = listener;
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class MyCustomViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView txtUsername;
        MaterialTextView txtPassword;
        MaterialTextView txtNumExtension;
        MaterialTextView txtProtocol;
        MaterialTextView txtDate;
        MaterialTextView txtCommittedSystem;
        MaterialTextView txtSpecialCallerID;




        Button btnProtocol;
        Button btnOutboundIde;
        Button btnDeleteAccounts;
        OnItemActionClickListener listener;



        public MyCustomViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.textView15);
            txtPassword = itemView.findViewById(R.id.textView21);
            txtNumExtension = itemView.findViewById(R.id.textView25);
            txtProtocol = itemView.findViewById(R.id.textView19);
            txtDate = itemView.findViewById(R.id.textView17);
            txtCommittedSystem = itemView.findViewById(R.id.textView27);
            txtSpecialCallerID = itemView.findViewById(R.id.textView29);


            btnProtocol = itemView.findViewById(R.id.button4);
            btnOutboundIde = itemView.findViewById(R.id.button6);
            btnDeleteAccounts = itemView.findViewById(R.id.button7);

            btnProtocol.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onActionClick(0, getAdapterPosition());
                }
            });

            btnOutboundIde.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onActionClick(1, getAdapterPosition());
                }
            });

            btnDeleteAccounts.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onActionClick(2, getAdapterPosition());
                }
            });
        }

        public void setOnItemActionClickListener(OnItemActionClickListener listener) {
            this.listener = listener;
        }
    }

}

