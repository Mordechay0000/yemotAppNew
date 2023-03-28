package com.mordechay.yemotapp.ui.programmatically.list;


import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
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
    private final List<ItemData> items;
    private final int txtParentRes;
    private final ViewHolder.ClickListener clickListener;

    public CustomAdapter(ViewHolder.ClickListener clickListener, int txtParentRes, int[] txtRes) {
        super();

        this.clickListener = clickListener;

        // Create some items
        items = new ArrayList<>();

        this.txtParentRes = txtParentRes;
        ViewHolder.txtRes = txtRes;
    }

    public void addItem(Drawable img, String[] txt){
        items.add(new ItemData(img, txt));
    }
    public void addItem(Drawable img, String[] txt, String[] txtInfo){
        items.add(new ItemData(img, txt, txtInfo));
    }

    public ItemData getItem(int position){
        return items.get(position);
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(txtParentRes, parent, false);
        return new ViewHolder(v, clickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ItemData item = items.get(position);

        holder.img.setImageDrawable(item.getImage());

        for (int i = 0; i < holder.txt.length; i++) {
            holder.txt[i].setText(item.getTxt()[i]);
        }

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
        static int[] txtRes;
        TextView[] txt;
        View selectedOverlay;

        private final ClickListener listener;

        public ViewHolder(View itemView, ClickListener listener) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.item_file_explorer_manger_file_imageView);

            txt = new TextView[txtRes.length];
            for(int i = 0; i < txtRes.length; i++) {
                txt[i] = itemView.findViewById(txtRes[i]);
            }
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