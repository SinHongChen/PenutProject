package com.example.penutproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.navigation.NavigationView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Parameter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.example.penutproject.GlobalVariable;
import com.example.penutproject.SocketClient;



public class Robot extends AppCompatActivity {

    GlobalVariable global = new GlobalVariable();
    SocketClient socketClient;

    private RecyclerView recyclerView;
    private DrawerLayout dra_layout;
    private NavigationView nav_view;
    private Toolbar tool_bar;
    private ImageView signal;
    private Button btn_right_detail,btn_left_detail,btn_dirpath; //上傳圖片按鈕
    private EditText ed_host,ed_port;
    private TextView tv_socket;
    private Switch sw_socket;
    private int viewpager1_position = 0,viewpager2_position = 0;
    private RecycleAdapter myAdapter;
    private boolean firstSwitch = true,socket_state = false;
    Timer timer = null;
    TimerTask task = null;
    private ViewPager vp_main_viewpager1,vp_main_viewpager2;
    int tt = 15; // 計算socket是否連線逾時 超過5秒就跳掉
    private boolean btn_dirpath_flag = true; //判斷是否打開recycleview false是尚未按下不打開
    //convert pixel to dp function
    private List<View> views1=new ArrayList<>();
    private List<View> views2=new ArrayList<>();
    //img_path1 讀取手機的圖片
    private List<String> img_path1 = new ArrayList<>();
    private List<String> img_path2 = new ArrayList<>();
    private int img1_index = 3,img2_index = 3;
    private ViewPagerAdapter view1PagerAdapter,view2PagerAdapter;
    private String Save_dirpath = "/storage/emulated/0/DCIM/Peanut/Tmp";
    private String nvidia_host;
    private int nvidia_port;
    private Button btn_go;

    private Runnable open_client=new Runnable () {
        public void run() {
            Looper.prepare();
            btn_go = findViewById(R.id.btn_robot_go);
            socketClient = new SocketClient(Save_dirpath,nvidia_host,nvidia_port);
            Log.d("Socket Client Thread :","open socket client");
            Looper.loop();
        }
    };

    private Handler handler = new Handler(Looper.myLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot);
        timer = new Timer();
        //File file = new File("/storage/emulated/0/DCIM/peanutImage");
        //建立儲存圖片的資料夾
        CreatDir("/storage/emulated/0/DCIM/Peanut");
        CreatDir("/storage/emulated/0/DCIM/Peanut/Tmp");
        setDirPath(); //設定recycle item值


        //------------------照片detail start

        btn_right_detail = findViewById(R.id.btn_right_detail);
        btn_right_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Robot.this,DetailCamera.class);
                String filename = img_path1.get(viewpager1_position);
                Long fileTime = (new File(filename)).lastModified();

                Date dt = new Date(fileTime);
                Log.i("wtt","照片拍摄日期为dateTime: " + dt);
                int[] counter = new int[3];
                counter[2] = Integer.valueOf(filename.split("_")[1]);
                counter[1] = Integer.valueOf(filename.split("_")[2]);
                counter[0] = Integer.valueOf(filename.split("_")[3].replace(".jpg",""));

                intent.putExtra("counter",counter);

                intent.putExtra("img_filename",filename);
                intent.putExtra("date",dt.toString());
                startActivity(intent);
            }
        });


        btn_left_detail = findViewById(R.id.btn_left_detail);
        btn_left_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Robot.this,DetailCamera.class);
                String filename = img_path2.get(viewpager2_position);
                Long fileTime = (new File(filename)).lastModified();
                Date dt = new Date(fileTime);
                Log.i("wtt","照片拍摄日期为dateTime: " + dt);

                int[] counter = new int[3];
                counter[2] = Integer.valueOf(filename.split("_")[1]);
                counter[1] = Integer.valueOf(filename.split("_")[2]);
                counter[0] = Integer.valueOf(filename.split("_")[3].replace(".jpg",""));
                intent.putExtra("counter",counter);

                intent.putExtra("img_filename",filename);
                intent.putExtra("date",dt.toString());
                startActivity(intent);
            }
        });
        //------------------照片detail 跳出視窗 end





        //預先設定host跟port
        /*
        SharedPreferences socket = getSharedPreferences("socket", MODE_PRIVATE);
        socket.edit()
                .putString("nvidia_host", "192.168.1.2")
                .putInt("nvidia_port", 8000)
                .putString("robot_host", "192.168.1.3")
                .putInt("robot_port", 8000)
                .commit();
        */
        dra_layout = findViewById(R.id.dra_layout);
        nav_view = findViewById(R.id.nav_view);
        tool_bar = findViewById(R.id.tool_bar);
        ed_host = (EditText) findViewById(R.id.ed_host);
        ed_port = findViewById(R.id.ed_port);
        tv_socket = findViewById(R.id.tv_socket);



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
                    Toast.makeText(Robot.this, "機台操作", Toast.LENGTH_SHORT);
                    return true;
                } else if (id == R.id.action_local) {
                    Toast.makeText(Robot.this, "歷史資訊", Toast.LENGTH_SHORT);
                    Intent intent = new Intent(Robot.this,MainActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        //---------------------------右側選單區塊 end


        //---------------------switch 判斷 socket 是否要連線 start

        sw_socket = findViewById(R.id.sw_socket);
        sw_socket.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    TextView textView = findViewById(R.id.tv_dirpath);
                    Save_dirpath = "/storage/emulated/0/DCIM/Peanut/" + textView.getText().toString().replaceAll("地點資料夾 : ","");
                    Log.d("path",Save_dirpath);
                    if(socketClient == null)
                    {
                        Log.d("socket","null");
                    }else{
                        socketClient.SetDirPath(Save_dirpath);

                    }
                    //---------------重新設定計時器 start
                    if(firstSwitch)
                    {
                        firstSwitch = false;
                    }else {
                        timer.cancel();
                        timer = null;
                        task.cancel();
                        task = null;
                    }
                    if(timer == null) {
                        timer = new Timer();
                    }
                    if(task == null)
                    {
                        task = new TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Log.d("hello",String.valueOf(global.CHECK_CLIENT_ROBOT));
                                        if(!socketClient.socket_state)
                                        {
                                            if(tt < 0) {
                                                tv_socket.setText("Socket 連線失敗");
                                                sw_socket.setChecked(false);
                                                sw_socket.setEnabled(true);

                                            }if(!socketClient.GetbtnClose()){
                                                tv_socket.setText("連線中.......");
                                                sw_socket.setEnabled(false);
                                                tt--;
                                            }
                                        }else if(!socketClient.GetbtnClose()){
                                            tv_socket.setText("連線成功");
                                            sw_socket.setEnabled(true);
                                        }
                                    }
                                });
                            }
                        };
                    }
                    tt = 15;
                    //---------------重新設定計時器 end




                    //取得 Socket資料
                    SharedPreferences socket = getSharedPreferences("socket", MODE_PRIVATE);
                    nvidia_host = getSharedPreferences("socket", MODE_PRIVATE)
                            .getString("nvidia_host", "");
                    nvidia_port = getSharedPreferences("socket", MODE_PRIVATE)
                            .getInt("nvidia_port", 0);

                    //判斷host port 存在後進行連線
                    if(nvidia_host != "" && nvidia_port != 0)
                    {
                        timer.schedule(task,0,1000);
                        //開啟 client socket
                        Thread t=new Thread(open_client);
                        t.start();
                    }else{
                        tv_socket.setText("Nvidia socket 尚未設定，請點選nvidia setting進行設定");
                        sw_socket.setChecked(false);
                    }

                }else {
                    //socketClient.onDestroy();
                    socketClient.Setbtnclose();
                    tv_socket.setText("socket尚未連線");
                    //設定signal信號變成紅色
                    signal = findViewById(R.id.img_signal);
                    GradientDrawable gdDefault = new GradientDrawable();
                    gdDefault.setColor(Color.RED);
                    gdDefault.setCornerRadius(50);
                    signal.setBackground(gdDefault);

                    //設定signal訊號旁邊的文字
                    TextView tv_check = findViewById(R.id.tv_check_socket);
                    tv_check.setText("未進行拍攝");
                }
            }
        });
        //---------------------switch 判斷 socket 是否要連線 end



    }

    //按下 go 之後選擇圖片存取資料夾
    public void setDirPath(){
        recyclerView = (RecyclerView) findViewById(R.id.re_dirpath);
        TextView tv_dirpath;
        tv_dirpath = findViewById(R.id.tv_dirpath);
        ArrayList<String> Dataset = new ArrayList<String>();
        File folder1 = new File("/storage/emulated/0/DCIM/Peanut");
        String [] list1 = folder1.list();
        for(int i = 0; i < list1.length; i++)
        {
            Dataset.add(list1[i]);
        }
        myAdapter = new RecycleAdapter(Dataset,recyclerView,tv_dirpath);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this); //設定此 layoutManager 為 linearlayout (類似ListView)
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL); //設定此 layoutManager 為垂直堆疊

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)); //設定分割線
        recyclerView.setLayoutManager(layoutManager); //設定 LayoutManager
        recyclerView.setAdapter(myAdapter); //設定 Adapter
        recyclerView.setVisibility(View.GONE);

        btn_dirpath = findViewById(R.id.btn_dirpath);
        btn_dirpath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myAdapter.visibility) {
                    btn_dirpath_flag = true;
                    myAdapter.visibility = false;
                }
                if(btn_dirpath_flag)
                    recyclerView.setVisibility(View.VISIBLE);
                else
                    recyclerView.setVisibility(View.GONE);
                btn_dirpath_flag = !btn_dirpath_flag;

            }
        });

    }


    public void dialog_nvidia(View v1){
        InputDialog dialog = new InputDialog(Robot.this, new InputDialog.OnEditInputFinishedListener() {
            @Override
            public void editInputFinished(String getdata) {
                String gethost = getdata.split("/")[0];
                int getport = Integer.valueOf(getdata.split("/")[1]);

                SharedPreferences socket = getSharedPreferences("socket", MODE_PRIVATE);
                socket.edit()
                        .putString("nvidia_host", gethost)
                        .putInt("nvidia_port", getport)
                        .commit();
            }
        });
        dialog.setContentView(R.layout.dialog_socket);
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

    }

    public void clear_socket(View v1){
        SharedPreferences socket = getSharedPreferences("socket", MODE_PRIVATE);
        socket.edit().clear().commit();
    }



    //設定 btn_robot_go button
    public void robot_go(View v){
        TextView tv_dirpath;
        tv_dirpath = findViewById(R.id.tv_dirpath);
        if(socketClient != null){
            if(socketClient.socket_state){
                socketClient.Setbtngo();
                //設定signal信號變成綠色
                signal = findViewById(R.id.img_signal);
                signal.setBackgroundColor(Color.GREEN);
                GradientDrawable gdDefault = new GradientDrawable();
                gdDefault.setColor(Color.GREEN);
                gdDefault.setCornerRadius(50);
                signal.setBackground(gdDefault);
                Save_dirpath = "/storage/emulated/0/DCIM/Peanut/" + tv_dirpath.getText().toString().replaceAll("地點資料夾 : ","");
                //-----------------設定viewpager adapter start
                vp_main_viewpager1 = (ViewPager)findViewById(R.id.vp_main_viewpager1);
                vp_main_viewpager2 = (ViewPager)findViewById(R.id.vp_main_viewpager2);


                img_path1 = SortList(Check(ChangeImageView(Save_dirpath),0));
                views1 = SetViews(img_path1);
                view1PagerAdapter = new ViewPagerAdapter(views1);
                vp_main_viewpager1.setAdapter(view1PagerAdapter);

                img_path2 = SortList(Check(ChangeImageView(Save_dirpath),1));
                views2 = SetViews(img_path2);
                view2PagerAdapter = new ViewPagerAdapter(views2);
                vp_main_viewpager2.setAdapter(view2PagerAdapter);
                //-----------------設定viewpager adapter end


                //---------------------設定MPChart圖表 start
                BarChart stackedChart = (BarChart)findViewById(R.id.chart_bar);
                int[] colorClassArray = new int[]{Color.GREEN,Color.YELLOW,Color.RED};
                BarDataSet barDataSet = new BarDataSet(SetBarValue(img_path1,img_path2),"Bar set");
                barDataSet.setColors(colorClassArray);
                barDataSet.setStackLabels(getStackLabels());
                BarData barData = new BarData(barDataSet);
                stackedChart.setData(barData);
                //---------------------設定MPChart圖表 end
                //設定signal訊號旁邊的文字
                TextView tv_check = findViewById(R.id.tv_check_socket);
                tv_check.setText("拍攝辨識中......");
            }
        }

    }

    //設定 btn_robot_stop button
    public void robot_stop(View v){
        //設定signal信號變成紅色
        socketClient.Setbtnstop();
        signal = findViewById(R.id.img_signal);
        GradientDrawable gdDefault = new GradientDrawable();
        gdDefault.setColor(Color.RED);
        gdDefault.setCornerRadius(50);
        signal.setBackground(gdDefault);

        //設定signal訊號旁邊的文字
        TextView tv_check = findViewById(R.id.tv_check_socket);
        tv_check.setText("未進行拍攝");
    }





    //-------------建立 MPChar 的一些function start

    public ArrayList<BarEntry> SetBarValue(List<String> img_path1,List<String> img_path2){
        int[] counter = {0,0,0};
        ArrayList<BarEntry> dataValues = new ArrayList<BarEntry>();
        for(int i = 0;i < img_path1.size();i++)
        {
            counter[2] += Integer.valueOf(img_path1.get(i).split("_")[1]);
            counter[1] += Integer.valueOf(img_path1.get(i).split("_")[2]);
            counter[0] += Integer.valueOf(img_path1.get(i).split("_")[3].replace(".jpg",""));
        }
        dataValues.add(new BarEntry(0,new float[]{counter[0],counter[1],counter[2]}));
        counter[2] = 0;
        counter[1] = 0;
        counter[0] = 0;
        for(int i = 0;i < img_path2.size();i++)
        {
            counter[2] += Integer.valueOf(img_path2.get(i).split("_")[1]);
            counter[1] += Integer.valueOf(img_path2.get(i).split("_")[2]);
            counter[0] += Integer.valueOf(img_path2.get(i).split("_")[3].replace(".jpg",""));
        }
        dataValues.add(new BarEntry(1,new float[]{counter[0],counter[1],counter[2]}));
        return dataValues;
    }


    private String[] getStackLabels(){
        return new String[]{getString(R.string.MPChart_1),
                getString(R.string.MPChart_2),
                getString(R.string.MPChart_3)};
    }

    //-------------建立 MPChar 的一些function end



    //ViewPager 設定
    class ViewPagerAdapter extends PagerAdapter {

        private List<View> views=new ArrayList<>();

        public ViewPagerAdapter(List<View> views)
        {
            this.views = views;
        }
        @Override
        public int getCount() {
            return views.size();
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
        //每次滑動都會觸發 可以利用這個方法取得目前position
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if(views == views1){
                //左滑
                viewpager1_position = position;
                if(position >= img1_index-1)
                {
                    img_path1 = SortList(Check(ChangeImageView(Save_dirpath),0));

                    if(img1_index < img_path1.size()) {
                        img1_index += 1;
                        AddPagerView(img1_index-1,img_path1,views1,view1PagerAdapter);
                    }
                }
            }else{
                viewpager2_position = position;
                if(position >= img2_index-1)
                {
                    img_path2 = SortList(Check(ChangeImageView(Save_dirpath),1));

                    if(img2_index < img_path2.size()) {
                        img2_index += 1;
                        AddPagerView(img2_index-1,img_path2,views2,view2PagerAdapter);
                    }
                }
            }
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v=views.get(position);
            container.addView(v);
            return v;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View v=views.get(position);
            container.removeView(v);
        }
    }

    //------------------------縮小圖片
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    public static Bitmap decodeSampledBitmapFromFile(String filepath,int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filepath,options);
    }


    public void CreatDir(String dirpath){
        File folder=new File(dirpath);
        if(!folder.exists()){
            folder.mkdir();
            Log.d("Create","create success");
        }else{
            Log.d("Create","folder is exit");
        }

    }

    //更改img_path陣列的圖片
    public List<View> SetViews(List<String> img_path){
        List<View> views = new ArrayList<>();
        int size;
        if(views.size() >= 3)
            size = 3;
        else
            size = views.size()+1;
        if(img_path == img_path1)
            img1_index = size;
        else
            img2_index = size;
        for (int i = 0; i < size; i++) {

            ImageView imageView=new ImageView(this);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bitmap = decodeSampledBitmapFromFile(img_path.get(i),300,300);
            //Bitmap bitmap = BitmapFactory.decodeFile(img_path1.get(i));
            imageView.setImageBitmap(bitmap);
            views.add(imageView);
        }
        return views;
    }

    public List<String> SortList(List<String> list){
        String tmp1,tmp2,c;
        List<String> tmp = list;
        for(int i = 0;i < tmp.size();i++){
            for(int j=i+1;j< tmp.size();j++)
            {
                tmp1 = tmp.get(i).split("/")[7].replaceAll("right","").replaceAll("left","").split("_")[0];
                tmp2 = tmp.get(j).split("/")[7].replaceAll("right","").replaceAll("left","").split("_")[0];
                if(Integer.valueOf(tmp1) > Integer.valueOf(tmp2)) {
                    c = tmp.get(i);
                    tmp.set(i, list.get(j));
                    tmp.set(j, c);
                }
            }
        }
        for(int i = 0;i < tmp.size();i++)
            Log.d("list: ",tmp.get(i));
        return tmp;
    }

    public List<String> Check(List<String> img_path,int number){
        String ch;
        List<String> CheckPath = new ArrayList<>();
        if(number == 0)
            ch = "r";
        else
            ch = "l";
        for(int i = 0;i < img_path.size();i++) {
            if (img_path.get(i).split("/")[7].contains(ch)) {
                CheckPath.add(img_path.get(i));
            }else {
            }
        }
        Log.d("break","ok");

        return CheckPath;
    }

    //更改img_path陣列的圖片
    public List<String> ChangeImageView(String read_dirpath){
        //-------------------讀取資料夾圖片路徑
        File folder1 = new File(read_dirpath);
        String [] list1 = folder1.list();
        List<String> img_path = new ArrayList<>();
        for(int i = 0 ;i < list1.length ;i++)
        {
            img_path.add(read_dirpath+"/"+list1[i]);
        }
        return img_path;
    }

    public void AddPagerView(int img_index,List<String> img_path,List<View> views,ViewPagerAdapter viewPagerAdapter){
        ImageView imageView=new ImageView(this);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = decodeSampledBitmapFromFile(img_path.get(img_index),300,300);
        //Bitmap bitmap = BitmapFactory.decodeFile(img_path1.get(i));
        imageView.setImageBitmap(bitmap);
        views.add(imageView);
        viewPagerAdapter.notifyDataSetChanged();
    }

}
