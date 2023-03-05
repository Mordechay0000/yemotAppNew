package com.mordechay.yemotapp.ui.programmatically.list;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mordechay.yemotapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CustomAdapter extends SelectableAdapter<CustomAdapter.ViewHolder> {
    @SuppressWarnings("unused")
    private static final String TAG = Adapter.class.getSimpleName();
    private List<ItemData> items;

    private ViewHolder.ClickListener clickListener;

    public CustomAdapter(ViewHolder.ClickListener clickListener) {
        super();

        this.clickListener = clickListener;

        // Create some items
        items = new ArrayList<>();
    }

    public void addItem(int img, String txt1, String txt2, String txt3, String txt4, String txt5){
        items.add(new ItemData(img, txt1, txt2, txt3, txt4, txt5));
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void removeItems(List<Integer> positions) {
        // Reverse-sort the list
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        // Split the list in ranges
        while (!positions.isEmpty()) {
            if (positions.size() == 1) {
                removeItem(positions.get(0));
                positions.remove(0);
            } else {
                int count = 1;
                while (positions.size() > count && positions.get(count).equals(positions.get(count - 1) - 1)) {
                    ++count;
                }

                if (count == 1) {
                    removeItem(positions.get(0));
                } else {
                    removeRange(positions.get(count - 1), count);
                }

                for (int i = 0; i < count; ++i) {
                    positions.remove(0);
                }
            }
        }
    }

    private void removeRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i) {
            items.remove(positionStart);
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final int layout = R.layout.item_file_explorer_manger_file;

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(v, clickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ItemData item = items.get(position);

        holder.img.setImageResource(item.getImage());
        holder.txt1.setText(item.getTxt1());
        holder.txt2.setText(item.getTxt2());
        holder.txt3.setText(item.getTxt3());
        holder.txt4.setText(item.getTxt4());
        holder.txt5.setText(item.getTxt5());

        // Highlight the item if it's selected
        holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {
        @SuppressWarnings("unused")
        private static final String TAG = ViewHolder.class.getSimpleName();

        ImageView img;
        TextView txt1;
        TextView txt2;
        TextView txt3;
        TextView txt4;
        TextView txt5;
        View selectedOverlay;

        private ClickListener listener;

        public ViewHolder(View itemView, ClickListener listener) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.item_file_explorer_manger_file_imageView);
            txt1 = (TextView) itemView.findViewById(R.id.textView1);
            txt2 = (TextView) itemView.findViewById(R.id.textView2);
            txt3 = (TextView) itemView.findViewById(R.id.textView3);
            txt4 = (TextView) itemView.findViewById(R.id.textView4);
            txt5 = (TextView) itemView.findViewById(R.id.textView5);
            selectedOverlay = itemView.findViewById(R.id.selected_overlay);

            this.listener = listener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClicked(getLayoutPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (listener != null) {
                return listener.onItemLongClicked(getLayoutPosition());
            }

            return false;
        }

        public interface ClickListener {
            void onItemClicked(int position);
            boolean onItemLongClicked(int position);
        }
    }
}