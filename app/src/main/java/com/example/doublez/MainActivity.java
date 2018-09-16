package com.example.doublez;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity
{
    private List<MainContent> maincontentList = new ArrayList<>();
    private int t1 = 100;
    private int t2 = 100;
    private int backpressed = 0;

    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = (User)getIntent().getSerializableExtra("User");

        //Toolbar
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //DrawerLayout 点击按钮打开菜单
        /**
         * 为什么不使用书上方法？
         * */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //NavigationView
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);

        //设置head部分显示内容
        View headerLayout = navView.inflateHeaderView(R.layout.nav_header);
        TextView nav_username = (TextView) headerLayout.findViewById(R.id.username);
        TextView nav_email = (TextView) headerLayout.findViewById(R.id.mail);
        ImageView nav_avatar = (ImageView) headerLayout.findViewById(R.id.icon_image);

        nav_email.setText(user.getEmail());
        nav_username.setText(user.getUsername());
        Bitmap bitmap = BitmapFactory.decodeByteArray(user.getavatarBMP(), 0, user.getavatarBMP().length);
        nav_avatar.setImageBitmap(bitmap);
        Log.d("MainActivity", "执行到4处");

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem item)
            {
                switch(item.getItemId())
                {
                    /**
                     * 参考nav_menu.xml
                     * */
                    case R.id.nav_1:
                        Toast.makeText(MainActivity.this,"排行榜", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_2:
                        //Toast.makeText(MainActivity.this,"最近配音", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(MainActivity.this, Recent.class);
                        startActivity(intent2);
                        break;
                    case R.id.nav_3:
                        Intent intent = new Intent(MainActivity.this, Login.class);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                }
                //重复了，注释掉？ 注释时把drawer设置成一个属性，在Oncreate方法里赋值就不会报错了
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_main_activity);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));  // 分割线

        initMainContent();
        MainContentAdapter adapter = new MainContentAdapter(maincontentList, MainActivity.this);
        recyclerView.setAdapter(adapter);
    }

    private void initMainContent()
    {
        MainContent a=new MainContent(1, "La Politique", R.drawable.la_politique);
        maincontentList.add(a);

        MainContent b=new MainContent(2,"La Gloire de mon Père 1",R.drawable.la_gloire_de_mon_pere);
        maincontentList.add(b);

        MainContent c=new MainContent(3,"La Gloire de mon Père 2",R.drawable.la_gloire_de_mon_pere);
        maincontentList.add(c);

        MainContent d=new MainContent(4,"Le Château de ma Mère",R.drawable.le_chateau_de_ma_mere);
        maincontentList.add(d);

        MainContent e=new MainContent(5,"Le Petit Nicolas",R.drawable.le_petit_nicolas);
        maincontentList.add(e);

        MainContent f=new MainContent(6,"La belle et la Bête",R.drawable.la_belle_et_la_bete);
        maincontentList.add(f);

        MainContent g=new MainContent(7,"Fanfan",R.drawable.fanfan);
        maincontentList.add(g);
    }

    @Override
    /**
     * 作用不知
     * */
    public void onStart()
    {
        super.onStart();
        backpressed=0;
    }

    @Override
    /**
     * 作用不知
     * */
    public void onBackPressed()
    {
        if(backpressed==0)
        {
            Toast.makeText(MainActivity.this,"再按一次退出Doublez!", Toast.LENGTH_SHORT).show();
            backpressed=1;
            Date date1=new Date();
            SimpleDateFormat df1 = new SimpleDateFormat("ss");
            t1=Integer.parseInt(df1.format(date1));
        }
        else
        {
            Date date2=new Date();
            SimpleDateFormat df2 = new SimpleDateFormat("ss");
            t2=Integer.parseInt(df2.format(date2));
            if(((t2-t1)<=3)||((t2-t1+60)<=3))
            {
                finish();
            }
            else
            {
                t1=100;
                t2=100;
                Toast.makeText(MainActivity.this,"再按一次退出Doublez!", Toast.LENGTH_SHORT).show();
                backpressed=1;
                Date date1=new Date();
                SimpleDateFormat df1 = new SimpleDateFormat("ss");
                t1=Integer.parseInt(df1.format(date1));
            }
        }
    }

}