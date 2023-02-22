package com.mordechay.yemotapp.ui.programmatically.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mordechay.yemotapp.R;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<DataModel> {

    // invoke the suitable constructor of the ArrayAdapter class
    public CustomAdapter(@NonNull Context context, ArrayList<DataModel> arrayList) {

        // pass the context and arrayList for the super
        // constructor of the ArrayAdapter class
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // convertView which is recyclable view
        LinearLayout currentItemView = (LinearLayout) convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.custom_list_view, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        DataModel currentNumberPosition = getItem(position);

        // then according to the position of the view assign the desired image for the same
        ImageView numbersImage = currentItemView.findViewById(R.id.imageView);
        assert currentNumberPosition != null;
        numbersImage.setImageResource(currentNumberPosition.getImage());
        LinearLayout lnr = currentItemView.findViewById(R.id.lnr_in_list);
        ArrayList<TextView> txtArray = new ArrayList<TextView>();
        txtArray.add(null);

        for (int a = 1; a < currentNumberPosition.ary.size(); a++) {
            if (currentItemView.findViewById(555555 + a) == null) {
                txtArray.add(new TextView(getContext()));
                txtArray.get(a).setId(555555 + a);
                lnr.addView(txtArray.get(a));
            } else {
                txtArray.add(currentItemView.findViewById(555555 + a));
            }
        }


        for (int a = 1; a < currentNumberPosition.ary.size(); a++) {
            txtArray.get(a).setText(currentNumberPosition.ary.get(a).toString());
            txtArray.get(a).setTextSize(18);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            layoutParams.setMargins(10, 10, 10, 10);
            txtArray.get(a).setLayoutParams(layoutParams);
        }
        // then return the recyclable view
        return currentItemView;
    }
}
