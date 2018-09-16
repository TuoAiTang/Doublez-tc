package com.example.doublez;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Login extends AppCompatActivity
{
    private EditText email;
    private EditText password;

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
        email = (EditText)findViewById(R.id.email_blank);
        password = (EditText)findViewById(R.id.password_blank);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        Button login=(Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                String inputEmail = email.getText().toString();
                String inputPassword = password.getText().toString();


                List<User> users = DataSupport.where("email = ? and password = ?", inputEmail, inputPassword)
                                            .find(User.class);

                if(users.size() >= 1){
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    Log.d("Login", "执行到此处1");
                    bundle.putSerializable("User", users.get(0));
                    intent.putExtras(bundle);
                    Log.d("Login", "执行到此处2");
                    startActivity(intent);
                    finish();  //还不知道为什么要加finish()
                } else{
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Login.this);
                    //增加判断 此账号不存在/密码错误
                    dialog.setTitle("Username or Password not correct!");
                    dialog.setPositiveButton("好",null);
                    dialog.setCancelable(true);
                    dialog.show();
                }

            }
        });

        Button signup=(Button)findViewById(R.id.signup);
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