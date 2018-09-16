package com.example.doublez;

import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.VideoView;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class Content extends AppCompatActivity
{
    private VideoView videoView=null;
    private List<Sentence> sentenceList=new ArrayList<>();
    private SentenceAdapter adapter;
    private MediaPlayer mediaPlayer = null;
    int video_num;
    private static String[][] textOfSentence = null;
    private static int[] sumSentence = null;


    private String imageid;
    private String content;
    private String score;

    static //使用静态代码块初始化textOfSentence
    {
        sumSentence = new int[3];//代表每个content最多有多少个分段
        sumSentence[1] = 7;//第一个视频有7个分段
        sumSentence[2] = 8;

        textOfSentence = new String[3][8];//假定每个视频最多有8个分段，从1开始
        textOfSentence[1][1] = "1. Un homme attend dans un café, un autre arrive, cela fait deux hommes. ";
        textOfSentence[1][2] = "2. Salut, mec. ";
        textOfSentence[1][3] = "3. Une serveuse s'approche des deux hommes. ";
        textOfSentence[1][4] = "4. Bojour. ";
        textOfSentence[1][5] = "5. Bonjour, je vais prendre un déca ";
        textOfSentence[1][6] = "6. Euh deux, s'il vous plaît. ";
        textOfSentence[1][7] = "7. Ils commandent de façon directive à la serveuse. ";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.content);


        video_num = getIntent().getIntExtra("video_num", 0);
        if (video_num == 0){
            throw new RuntimeException("向content传参错误");
        }

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 自动播放
        Log.d("Content", "执行到content_" + video_num);
        videoView = (VideoView) findViewById(R.id.video_view);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" +
                getResources().getIdentifier("v" + video_num + "_0", "raw", getPackageName())));

        //videoView.setMediaController(new MediaController(this));  // 播放器控制按钮
        //Uri rawUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.a_0);
        //videoView.setVideoURI(rawUri);
        videoView.start();

        // 此处有修改，改成了点击屏幕，视频会暂停
        videoView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                Log.d("Content", "点击屏幕事件");
                if(videoView.isPlaying()){
                    videoView.pause();
                } else{
                    videoView.start();
                }
                return false;
            }
        });

        //设置Content中的滑动条内容
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_content);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        initSententce();

        //需要传入本活动中的多媒体播放器
        //似乎这么搞不是很正规
        adapter = new SentenceAdapter(sentenceList, Content.this, videoView, mediaPlayer, video_num);
        recyclerView.setAdapter(adapter);

    }


    /**
     * 对应content中右上角那个按钮
     * 当点击时，把自己的录音组合到原视频中播放出来
     * */
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.content_toolbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //停止其他播放
        if(mediaPlayer != null) {
            mediaPlayer.reset();
        }
//        videoView.suspend();
        //"file:///android_assets/" + video_num + "_0.mp4"
        Uri uri = Uri.fromFile(new File(getPackageName() + "/" +
                getResources().getIdentifier(video_num + "_0", "raw", getPackageName())));

        videoView.setVideoURI(uri);
        videoView.start();
        //播放视频
        //原代码中，a_1s是特制的没有声音的a_1视频，这里改动，直接静音
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVolume(0f, 0f);
                mp.start();
            }
        });
        int i = 1;
        //改动 怎么把多个文件整合成一个来播放?
        while(i <= 7) //这个7肯定要换成一个变量，根据传入的字符串而改变
        {
            //播放录音
            try{
                //不清楚该用getPath()还是getabsolutepath()?
                mediaPlayer.setDataSource(sentenceList.get(i - 1).getRecordFile().getPath());
                mediaPlayer.prepare();
            }catch (Exception e){
                throw new RuntimeException(e);
            }
            mediaPlayer.start();
            i++;
        }
        return true;
    }


    private void initSententce()
    {
        Sentence s = null;
        //7可以改变
        AssetManager assetManager = getAssets();
        try{
            for (int i = 1; i <= sumSentence[video_num]; i++){
                s = new Sentence(i, textOfSentence[video_num][i],
                        video_num + "_" + i + ".acc",
                        getPackageName() + "/" + getResources()
                                .getIdentifier("v" + video_num + "_" + i, "raw", getPackageName()));
                sentenceList.add(s);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

//    public int  getResource(String imageName){
//        Class raw = R.raw.class;
//        try {
//            Field field = raw.getField(imageName);
//            return field.getInt(imageName);
//        } catch (NoSuchFieldException e) {
//            return 0;
//        } catch (IllegalAccessException e) {
//            return 0;
//        }
//    }

    protected void onDestory(){
        super.onDestroy();
        if(videoView != null) videoView.suspend();
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}