package com.example.doublez;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.doublez.entity.BaseInfo;
import com.example.doublez.entity.User;
import com.example.doublez.util.APIUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

public class Login extends AppCompatActivity
{
    private EditText email;
    private EditText password;

    private String inputEmail;
    private String inputPassword;

    private User user;

    private BaseInfo baseInfo;

    private Handler getuser_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            user = (User)msg.obj;
            Intent intent = new Intent(Login.this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("User", user);
            intent.putExtras(bundle);
            Log.d("Login", "执行到此处1");
            startActivity(intent);
            finish();  //还不知道为什么要加finish()
        }
    };

    private Handler login_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            baseInfo = (BaseInfo)msg.obj;
            if(baseInfo.getCode() == 0){
                APIUtil.invokeGetUserAPI(getuser_handler, inputEmail);
            }else{
                AlertDialog.Builder dialog = new AlertDialog.Builder(Login.this);
                //增加判断 此账号不存在/密码错误
                dialog.setTitle("Username or Password not correct!");
                dialog.setPositiveButton("好",null);
                dialog.setCancelable(true);
                dialog.show();
            }
        }
    };

    private String TAG = "登录活动";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Log.d("Login", "执行到Login");

        //申请存储权限   为什么不重写onRequestPermissionsResult方法？
        if(ContextCompat.checkSelfPermission(Login.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Login.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        // EditText
        email = (EditText)findViewById(R.id.username_blank);
        password = (EditText)findViewById(R.id.password_blank);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        Button login=(Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                inputEmail = email.getText().toString();
                inputPassword = password.getText().toString();
                Log.d(TAG, "onClick: email" + inputEmail);
                Log.d(TAG, "onClick: password" + inputPassword);
                APIUtil.invokeLoginAPI(login_handler, inputEmail, inputPassword);
            }
        });

        Button signup=(Button)findViewById(R.id.login_signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(Login.this,Signup.class);
                startActivity(intent);
            }
        });

        ImageView doublez = (ImageView)findViewById(R.id.main_logo);
        doublez.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}