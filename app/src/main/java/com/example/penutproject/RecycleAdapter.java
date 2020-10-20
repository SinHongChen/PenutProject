package com.example.penutproject;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {
    private List<String> mDataSet;
    private RecyclerView recyclerView;
    private TextView tv_dirpath;
    public boolean visibility = false;

    public RecycleAdapter(List<String> data,RecyclerView recyclerView,TextView tv_dirpath)
    {
        mDataSet = data;
        this.recyclerView = recyclerView;
        this.tv_dirpath = tv_dirpath;
    }

    @Override
    public RecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item, parent, false);
        ViewHolder viewholder = new ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        holder.txtItem.setText(mDataSet.get(position));
    }

    @Override
    public int getItemCount()
    {
        return mDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView txtItem;
        public ViewHolder(View v)
        {
            super(v);
            txtItem = (TextView) v.findViewById(R.id.dirpath_item);
            txtItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("item_counter",txtItem.getText().toString());
                    tv_dirpath.setText("地點資料夾 : " + txtItem.getText());
                    recyclerView.setVisibility(View.GONE);
                    visibility = true;
                }
            });
        }
    }
}
