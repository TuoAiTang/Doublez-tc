package com.example.doublez;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Recent extends AppCompatActivity
{
    private List<RecentItem> recentitemList = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recent);

        //Toolbar
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar_r);
        setSupportActionBar(toolbar);

        //RecyclerView
        recyclerView=(RecyclerView)findViewById(R.id.recycler_recent);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));  // 分割线

        initRecentItem();
        RecentItemAdapter adapter=new RecentItemAdapter(recentitemList,Recent.this);
        recyclerView.setAdapter(adapter);
    }


    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.recent_toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            //菜单栏监听
            case R.id.play:
                DataSupport.deleteAll(RecentItem.class);
                recentitemList.clear();
                initRecentItem();
                RecentItemAdapter adapter=new RecentItemAdapter(recentitemList,Recent.this);
                recyclerView.setAdapter(adapter);
            default:
        }
        return true;
    }

    public void initRecentItem()
    {
        //查询配音记录
        //代码未写完？
        List<RecentItem> recentitems = DataSupport.findAll(RecentItem.class);
        Collections.reverse(recentitems); // 倒序排列
        for(RecentItem recentitem:recentitems)
        {
            recentitemList.add(recentitem);
        }
    }

    /**
     * 作用？
     * */
    public void onBackPressed()
    {
        finish();
    }

}
