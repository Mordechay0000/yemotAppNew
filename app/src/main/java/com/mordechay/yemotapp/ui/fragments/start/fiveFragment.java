package com.mordechay.yemotapp.ui.fragments.start;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.util.SharedPreferencesUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.mordechay.yemotapp.R;
import com.mordechay.yemotapp.data.Constants;
import com.mordechay.yemotapp.ui.activitys.LoginActivity;
import com.mordechay.yemotapp.ui.activitys.StartActivity;
import com.mordechay.yemotapp.ui.activitys.loginToServerActivity;
import com.mordechay.yemotapp.ui.activitys.openActivity;

public class fiveFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private MaterialCheckBox chbAgreeTerms;
    private MaterialCheckBox chbAgreeNotYemot;
    private MaterialButton btnNext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_five, container, false);

        chbAgreeTerms = (MaterialCheckBox) v.findViewById(R.id.five_chb_agree_terms);
        chbAgreeNotYemot = (MaterialCheckBox) v.findViewById(R.id.five_chb_agree_not_yemot);
        btnNext = (MaterialButton) v.findViewById(R.id.five_btn_next_activity);

        chbAgreeTerms.setOnCheckedChangeListener(this);
        chbAgreeNotYemot.setOnCheckedChangeListener(this);
        btnNext.setOnClickListener(this);


        return v;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (chbAgreeTerms.isChecked() && chbAgreeNotYemot.isChecked()) {
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }

    @Override
    public void onClick(View view) {
        if(chbAgreeTerms.isChecked() && chbAgreeNotYemot.isChecked()){
            requireActivity().getSharedPreferences(Constants.DEFAULT_SHARED_PREFERENCES_DATA, 0).edit().putBoolean("agree", true).apply();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else {
            Toast.makeText(getActivity(), "אנא סמן את כל התיבות", Toast.LENGTH_SHORT).show();
        }
    }
}