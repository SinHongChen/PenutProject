package com.example.penutproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.example.penutproject.MainActivity;

import java.io.File;
import java.util.Date;
import java.util.List;

public class StoryImgAdapter extends RecyclerView.Adapter<StoryImgAdapter.ViewHolder> {
    private List<String> mDataSet;
    private RecyclerView recyclerView;
    public boolean visibility = false;
    public String dirpath;

    public StoryImgAdapter(List<String> data,RecyclerView recyclerView,String dirpath)
    {
        mDataSet = data;
        this.recyclerView = recyclerView;
        this.dirpath = dirpath;
    }

    @Override
    public StoryImgAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.storyimg_item, parent, false);
        ViewHolder viewholder = new ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = Robot.decodeSampledBitmapFromFile(mDataSet.get(position),100,100);
        //Bitmap bitmap = BitmapFactory.decodeFile(img_path1.get(i));
        holder.imageView.setImageBitmap(bitmap);
        holder.textView.setText(mDataSet.get(position).replaceAll(dirpath+"/",""));
    }

    @Override
    public int getItemCount()
    {
        return mDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView imageView;
        public TextView textView;
        public ViewHolder(View v)
        {
            super(v);
            imageView = v.findViewById(R.id.img_storyimg);
            textView = v.findViewById(R.id.tv_storyimg);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context mContext = v.getContext();
                    Intent intent = new Intent(mContext,DetailCamera.class);
                    String filename = dirpath + "/" + textView.getText();
                    Long fileTime = (new File(filename)).lastModified();

                    Date dt = new Date(fileTime);
                    int[] counter = new int[3];
                    counter[2] = Integer.valueOf(filename.split("_")[1]);
                    counter[1] = Integer.valueOf(filename.split("_")[2]);
                    counter[0] = Integer.valueOf(filename.split("_")[3].replace(".jpg",""));

                    intent.putExtra("counter",counter);

                    intent.putExtra("img_filename",filename);
                    intent.putExtra("date",dt.toString());
                    mContext.startActivity(intent);
                }
            });

        }
    }

}
