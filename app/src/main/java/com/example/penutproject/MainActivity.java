package com.example.penutproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Parameter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.example.penutproject.GlobalVariable;

public class MainActivity extends AppCompatActivity {

    GlobalVariable global = new GlobalVariable();


    private LinearLayout img_layout;
    private DrawerLayout dra_layout;
    private NavigationView nav_view;
    private Toolbar tool_bar;
    private RecyclerView recyclerView,ImgrecyclerView;
    private StoryAdapter storyAdapter;
    //convert pixel to dp function
    String currentPhotoPath;
    private String read_dirpath = "/storage/emulated/0/DCIM/Peanut";
    private boolean add_dir_flag = true;
    private ImageButton btn_add_dir;
    private Button btn_create_dir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        add_dir();
        dra_layout = findViewById(R.id.dra_layout);
        nav_view = findViewById(R.id.nav_view);
        tool_bar = findViewById(R.id.tool_bar);
        btn_add_dir = findViewById(R.id.btn_add_dir);
        btn_create_dir = findViewById(R.id.btn_create_dir);


        btn_add_dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_dir();
            }
        });


        btn_create_dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.ed_create_dir);
                String dirpath = "/storage/emulated/0/DCIM/Peanut/" + editText.getText().toString();
                File folder=new File(dirpath);
                if(!folder.exists()){
                    folder.mkdir();
                    Toast.makeText(getApplicationContext(), "資料夾創建成功", Toast.LENGTH_SHORT).show();
                    SetDirList();
                }else{
                    Toast.makeText(getApplicationContext(), "資料夾已經存在", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //--------------------------------右側選單區塊 start
        setSupportActionBar(tool_bar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dra_layout, tool_bar, R.string.drawer_open, R.string.drawer_close);
        dra_layout.addDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                dra_layout.closeDrawer(GravityCompat.START);
                int id = item.getItemId();

                if (id == R.id.action_robot) {
                    Toast.makeText(MainActivity.this, "機台操作", Toast.LENGTH_SHORT);
                    Intent intent = new Intent(MainActivity.this,Robot.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.action_local) {
                    Toast.makeText(MainActivity.this, "歷史資訊", Toast.LENGTH_SHORT);

                    return true;
                }
                return false;
            }
        });
        //---------------------------右側選單區塊 end
        SetDirList();


    }

    public void OpenStoryImg(String dirpath,RecyclerView recyclerView){
        ArrayList<String> Dataset = new ArrayList<String>();
        File folder1 = new File(dirpath);
        String [] list1 = folder1.list();
        for(int i = 0; i < list1.length; i++)
        {
            Dataset.add(dirpath + "/" + list1[i]);
        }
        StoryImgAdapter storyImgAdapter = new StoryImgAdapter(Dataset,recyclerView,dirpath);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this); //設定此 layoutManager 為 linearlayout (類似ListView)
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL); //設定此 layoutManager 為垂直堆疊

        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)); //設定分割線
        recyclerView.setLayoutManager(layoutManager); //設定 LayoutManager
        recyclerView.setAdapter(storyImgAdapter); //設定 Adapter
        //recyclerView.setVisibility(View.GONE);
    }

    public void SetDirList(){
        recyclerView = (RecyclerView) findViewById(R.id.re_story);
        TextView tv_dirpath;
        ArrayList<String> Dataset = new ArrayList<String>();
        File folder1 = new File(read_dirpath);
        String [] list1 = folder1.list();

        for(int i = 0; i < list1.length; i++)
        {
            Dataset.add(list1[i]);
        }
        ImgrecyclerView = (RecyclerView) findViewById(R.id.re_story_img);
        storyAdapter = new StoryAdapter(Dataset,recyclerView,ImgrecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this); //設定此 layoutManager 為 linearlayout (類似ListView)
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL); //設定此 layoutManager 為垂直堆疊

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)); //設定分割線
        recyclerView.setLayoutManager(layoutManager); //設定 LayoutManager
        recyclerView.setAdapter(storyAdapter); //設定 Adapter
        //recyclerView.setVisibility(View.GONE);
    }

    public void add_dir(){
        EditText editText = findViewById(R.id.ed_create_dir);
        Button button = findViewById(R.id.btn_create_dir);
        if(add_dir_flag){
            button.setVisibility(View.GONE);
            editText.setVisibility(View.GONE);
        }else {
            button.setVisibility(View.VISIBLE);
            editText.setVisibility(View.VISIBLE);
        }
        add_dir_flag = !add_dir_flag;
    }


}
