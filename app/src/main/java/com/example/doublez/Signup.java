package com.example.doublez;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.io.ByteArrayOutputStream;
import java.util.List;

public class Signup extends AppCompatActivity
{
    //对应布局signup.xml
    public static final int CHOOSE_PHOTO=2;

    private ImageView avatarImage;
    private Bitmap avatarBMP;
    private byte[] avatar;
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText repassword;

    private BaseInfo baseInfo;

    private Handler register_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            baseInfo = (BaseInfo)msg.obj;
            if(baseInfo.getCode() == 0){
                Toast.makeText(Signup.this, "注册成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Signup.this, Login.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(Signup.this, baseInfo.getMsg(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState)
    {
        //也可不使用bitmap 直接使用setImageResource()方法
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        Log.d("Signup", "执行到Signup");

        Resources res = Signup.this.getResources();
        avatarBMP = BitmapFactory.decodeResource(res, R.mipmap.ic_launcher);

        //Toolbar
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar_r);
        setSupportActionBar(toolbar);

        avatarImage=(ImageView)findViewById(R.id.signup_avatar);
        avatarImage.setImageBitmap(avatarBMP);

        //选择头像键
        Button chooseAvatar=(Button)findViewById(R.id.signup_choose);
        chooseAvatar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            /**
            * 为什么还需要申请授权一次？
            * */
            public void onClick(View v)
            {
                if(ContextCompat.checkSelfPermission(Signup.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(Signup.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                else
                    openAlbum();
            }
        });

        username=(EditText)findViewById(R.id.signup_username);
        email=(EditText)findViewById(R.id.signup_email);
        password=(EditText)findViewById(R.id.signup_password);
        repassword=(EditText)findViewById(R.id.signup_repassword);
        // 密码星号化
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        repassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        //Sign up键
        Button signup=(Button)findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(username.getText().toString().equals("") || email.getText().toString().equals("") || password.getText().toString().equals("") || repassword.getText().toString().equals(""))
                {
                    // 弹出对话框
                    AlertDialog.Builder dialog=new AlertDialog.Builder(Signup.this);
                    dialog.setTitle("有信息未填完整");
//                    dialog.setMessage("请填写完整");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("好",null);
                    dialog.show();
                }
                else
                {
                    if(!password.getText().toString().equals(repassword.getText().toString()))
                    {
                        // AlertDialog
                        AlertDialog.Builder dialog=new AlertDialog.Builder(Signup.this);
                        dialog.setTitle("密码不一致请重新输入");
//                        dialog.setMessage("请重新输入");
                        dialog.setCancelable(true);
                        dialog.setPositiveButton("好",new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                password.getText().clear();
                                repassword.getText().clear();
                            }
                        });
                        dialog.show();
                    }
                    else
                    {
                        List<User> users = DataSupport.where("email = ?", email.getText().toString())
                                .find(User.class);
                        if(users.size() == 1){
                            AlertDialog.Builder dialog=new AlertDialog.Builder(Signup.this);
                            dialog.setTitle("此邮箱已经存在");
                            dialog.setCancelable(true);
                            dialog.setPositiveButton("好",new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    email.getText().clear();
                                }
                            });
                            dialog.show();
                        } else {
                            User user = new User(email.getText().toString(), password.getText().toString(), img(avatarBMP));
//                            user.save();
                            APIUtil.invokeRegisterAPI(register_handler,
                                    user.getEmail(),user.getPassword(),user.getAvatar().toString());
                        }
                    }
                }
            }
        });
    }

    //下面都是书上打开相册部分的代码
    private void openAlbum()
    {
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[]grantResults)
    {
        switch(requestCode)
        {
            case 1:
                if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    openAlbum();
                break;
            default:
        }
    }

    public void onActivityResult(int RequestCode,int ResultCode,Intent data){
        switch(RequestCode){
            case 2:
                if(ResultCode==RESULT_OK){
                    if(Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                    }else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];
                String selection= MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath=getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath=uri.getPath();
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data){
        String imagePath=null;
        Uri uri=data.getData();
        imagePath=getImagePath(uri,null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri,String selection){
        String Path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                Path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return Path;
    }

    private void displayImage(String Path){
        Bitmap bm= BitmapFactory.decodeFile(Path);
        avatarImage.setImageBitmap(bm);
        avatarBMP = bm;
    }

    private byte[] img(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}
