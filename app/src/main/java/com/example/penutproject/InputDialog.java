package com.example.penutproject;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.view.View.OnClickListener;

public class InputDialog extends AlertDialog implements OnClickListener{
    private EditText host,port;  //编辑框
    private Button btn_connect, btn_cancel;  //确定取消按钮
    private OnEditInputFinishedListener mListener; //接口

    public interface OnEditInputFinishedListener{
        void editInputFinished(String password);
    }

    protected InputDialog(Context context, OnEditInputFinishedListener mListener) {
        super(context);
        this.mListener = mListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_socket);

        //控件
        host = (EditText)findViewById(R.id.ed_host);
        port = (EditText)findViewById(R.id.ed_port);
        btn_connect = (Button)findViewById(R.id.btn_connect);
        btn_cancel = (Button)findViewById(R.id.btn_cancel);

        btn_connect.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_connect) {
            //确定
            if (mListener != null) {
                String gethost = host.getText().toString();
                String getport = port.getText().toString();
                mListener.editInputFinished(gethost + "/" + getport);
            }
            dismiss();
        }else {
            //取消
            dismiss();
        }
    }

}