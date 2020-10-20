package com.example.penutproject;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;

import com.example.penutproject.GlobalVariable;

//socket client to nvidia
//recieve image then save in dirpath

public class SocketClient extends Activity {
    GlobalVariable global = new GlobalVariable();
    public Thread thread;
    private Socket clientSocket;//客戶端的socket
    private BufferedWriter bw;  //取得網路輸出串流
    private BufferedReader br;  //取得網路輸入串流
    private String tmp;         //做為接收時的緩存
    private JSONObject jsonWrite, jsonRead; //從java伺服器傳遞與接收資料的json
    public boolean socket_state = false;
    PrintWriter printWriter;
    private String dirpath;
    private String host;
    private int port;
    private boolean btn_go = false,btn_stop = false,btn_close = false;

    public void Setbtngo(){
        btn_go = true;
    }

    public void Setbtnstop(){
        btn_stop = true;
    }

    public void Setbtnclose() {
        btn_close = true;
        socket_state = false;
    }
    public boolean GetbtnClose(){
        return btn_close;
    }

    public void SetDirPath(String dirpath){
        this.dirpath = dirpath;
        Log.d("path",dirpath);
    }

    public SocketClient(String dirpath,String host,int port){
        this.dirpath = dirpath;
        thread=new Thread(Connection);
        thread.start();
        this.host = host;
        this.port = port;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot);
    }

    public boolean get_socket_state(){
        return socket_state;
    }

    //連結socket伺服器做傳送與接收
    private Runnable Connection=new Runnable(){
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try{
                //輸入 Server 端的 IP
                InetAddress serverIp = InetAddress.getByName(host);
                //建立連線
                clientSocket = new Socket(serverIp, port);
                //取得網路輸出串流
                bw = new BufferedWriter( new OutputStreamWriter(clientSocket.getOutputStream()));
                //取得網路輸入串流
                br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                //檢查是否已連線
                String line;
                OutputStream outputStream = clientSocket.getOutputStream();

                while (clientSocket.isConnected()) {
                    Log.d("hello","client connect ok");
                    socket_state = true;
                    GradientDrawable gdDefault = new GradientDrawable();
                    gdDefault.setColor(Color.GREEN);

                    //Log.d("hello",String.valueOf(global.CHECK_CLIENT_ROBOT));
                    //宣告一個緩衝,從br串流讀取 Server 端傳來的訊息
                    //tmp = br.readLine();
                    InputStream inputStream;
                    printWriter = new PrintWriter(outputStream, true);

                    //if(tmp!=null){

                        //------------------陳明毅接收圖片部分 start
                        line = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
                        if(line.equals(("checkbtn")))
                        {
                            if(btn_go)
                            {
                                printWriter.println("go");
                                Log.d("btn","go");
                                btn_go = false;
                            }
                            if(btn_stop)
                            {
                                printWriter.println("stop");
                                Log.d("btn","stop");
                                btn_stop = false;
                            }
                            if(btn_close)
                            {
                                printWriter.println("close");
                                Log.d("btn","close");
                                btn_stop = false;
                            }
                        }
                        if(line.equals("sendFile"))
                        {
                            printWriter.println("startReceived");
                            String fileName = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
                            printWriter.println("fileNameOk");
                            long fileSize = Long.parseLong(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine());
                            printWriter.println("fileSizeOk");

                            String pathAddFileName = dirpath + "/" + fileName;

                            Log.d("filename",pathAddFileName);


                            inputStream = clientSocket.getInputStream();
                            FileOutputStream fileOutputStream = new FileOutputStream(pathAddFileName);
                            byte[] buff = new byte[1024];
                            int length = 0;
                            int nowSize = 0;
                            while ((length = inputStream.read(buff)) != -1) {
                                fileOutputStream.write(buff, 0, length);
                                nowSize+=length;
                                if(nowSize==fileSize){
                                    printWriter.println("finish");
                                    break;
                                }
                            }
                            Log.d("state:","accept finish");
                        }
                        //------------------陳明毅接收圖片部分 end

                        //將取到的String抓取{}範圍資料
                        //tmp=tmp.substring(tmp.indexOf("{"), tmp.lastIndexOf("}") + 1);
                        //JSONObject json_read = new JSONObject(tmp);
                        //從java伺服器取得值後做拆解,可使用switch做不同動作的處理
                    }

                //}
            }catch(Exception e){
                //當斷線時會跳到 catch,可以在這裡處理斷開連線後的邏輯
                e.printStackTrace();
                Log.e("text","Socket連線="+e.toString());
                socket_state = false;
                finish();    //當斷線時自動關閉 Socket
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            //傳送離線 Action 給 Server 端
            jsonWrite = new JSONObject();
            jsonWrite.put("action","離線");

            //寫入
            bw.write(jsonWrite + "\n");
            //立即發送
            bw.flush();

            //關閉輸出入串流後,關閉Socket
            bw.close();
            br.close();
            //clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}