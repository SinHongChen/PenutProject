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
import com.example.penutproject.MainActivity;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
    private List<String> mDataSet;
    private RecyclerView recyclerView;
    private RecyclerView ImgrecyclerView;
    public boolean visibility = false;
    private String dirpath = "";
    public String flag = "";

    public String GetDirPath(){
        return dirpath;
    }

    public StoryAdapter(List<String> data,RecyclerView recyclerView,RecyclerView imgrecyclerView)
    {
        mDataSet = data;
        this.recyclerView = recyclerView;
        this.ImgrecyclerView = imgrecyclerView;
    }

    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_item, parent, false);
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
            txtItem = (TextView) v.findViewById(R.id.story_item);
            if(flag == txtItem.getText())
                txtItem.setTextColor(Color.RED);
            else
                txtItem.setTextColor(Color.BLACK);

            txtItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("item_counter",txtItem.getText().toString());
                    //recyclerView.setVisibility(View.GONE);
                    dirpath = txtItem.getText().toString();
                    flag = txtItem.getText().toString();
                    MainActivity ma = new MainActivity();
                    ma.OpenStoryImg("/storage/emulated/0/DCIM/Peanut/" + dirpath,ImgrecyclerView);
                    //visibility = true;
                }
            });
        }
    }
}
