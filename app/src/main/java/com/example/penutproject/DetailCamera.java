package com.example.penutproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DetailCamera extends AppCompatActivity {

    private int img_id;
    private String img_filename,date;
    private ImageView img_main;
    private TextView tv_title,detail_description;
    private int[] peanut_counter;
    Bitmap bm;
    PieChart piechart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_camera);

        Intent intent = this.getIntent();
        img_filename = intent.getStringExtra("img_filename");
        String[] filename = img_filename.split("/");
        Log.d("filename",filename[filename.length-1]);

        detail_description = findViewById(R.id.img_detail_description);
        date = intent.getStringExtra("date");
        Log.d("date",date);
        detail_description.setText(date);

        tv_title = findViewById(R.id.tv_detail_title);
        tv_title.setText(filename[filename.length-1]);

        Bitmap bitmap = BitmapFactory.decodeFile(img_filename);
        img_main = findViewById(R.id.img_detail);
        img_main.setImageBitmap(bitmap);

        peanut_counter= intent.getIntArrayExtra("counter");
        Log.d("counter",String.valueOf(peanut_counter[2]));


        //---------------------設定圓餅圖 start
        piechart = findViewById(R.id.pie_chart);
        List<PieEntry> strings = new ArrayList<>();
        strings.add(new PieEntry(peanut_counter[0],"Leaf"));
        strings.add(new PieEntry(peanut_counter[1],"Sick"));
        strings.add(new PieEntry(peanut_counter[2],"Pest"));

        PieDataSet dataSet = new PieDataSet(strings,"Label");
        dataSet.setValueTextSize(16f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setColor(Color.BLACK);


        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(ContextCompat.getColor(this,R.color.green));
        colors.add(ContextCompat.getColor(this,R.color.yellow));
        colors.add(ContextCompat.getColor(this,R.color.red));
        dataSet.setColors(colors);

        PieData pieData = new PieData(dataSet);
        pieData.setDrawValues(true);

        //設定是實心還是空心，實心false,空心true
        piechart.setDrawHoleEnabled(true);
        //是否顯示描述
        piechart.getDescription().setEnabled(false);
        piechart.setData(pieData);
        piechart.invalidate();

    }
}