package com.example.doublez;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class SentenceAdapter extends RecyclerView.Adapter<SentenceAdapter.ViewHolder>
{
    private List<Sentence> sentenceList;
    private Activity activity;
    private VideoView videoView = null;
    private MediaRecorder mediaRecorder = null;
    private MediaPlayer mediaPlayer = null;
    private int video_num;

//    private ReadAACFileThread audioThread;
    //定义内部类
    static class ViewHolder extends RecyclerView.ViewHolder {
//        View sentenceView;
        TextView sentenceText;
        ImageView sentenceRec;
        ImageView sentencePlay;
        TextView scoreText;

        public ViewHolder (View view) {
            super(view);

//            sentenceView=view;
            sentenceText=(TextView)view.findViewById(R.id.sentence_text);
            sentenceRec=(ImageView)view.findViewById(R.id.sentence_rec);
            sentencePlay=(ImageView)view.findViewById(R.id.sentence_play);
            scoreText=(TextView)view.findViewById(R.id.score_text);
        }
    }

    public SentenceAdapter(List<Sentence> sentenceList, Activity activity, VideoView videoview, MediaPlayer mediaPlayer, int video_num)
    {
        this.sentenceList = sentenceList;
        this.activity = activity;
        this.videoView = videoview;
        this.mediaPlayer = mediaPlayer;
        this.video_num = video_num;
    }

    public SentenceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sentence_item,parent,false);
        final SentenceAdapter.ViewHolder holder = new SentenceAdapter.ViewHolder(view);

        //点击文字播放视频
        holder.sentenceText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //停止播放录音
//                if(mediaPlayer.isPlaying()) {
//                    //原代码中没有release()，为什么？？？？？？？？？？
//                    mediaPlayer.stop();
//                    mediaPlayer.reset();
//                }
                if(mediaPlayer != null) mediaPlayer.reset();
                //停止录音
//                if(mediaRecorder!=null)
//                {
//                    mediaRecorder.stop();
//                    mediaRecorder.reset();
//                }

                int position = holder.getAdapterPosition();
                Sentence sentence = sentenceList.get(position);
                videoView.setVideoURI(sentence.getOriginalFile());
                videoView.start();
            }
        });

        //点击录音按钮
        holder.sentenceRec.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int position=holder.getAdapterPosition();
                Sentence sentence = sentenceList.get(position);
                //按下
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    //颜色反转
                    holder.sentenceRec.setImageResource(R.mipmap.record_r);
                    //停止播放录音
                    if(mediaPlayer!=null){
                        mediaPlayer.reset();
                    }
                    //静音播放
                    videoView.setVideoURI(sentence.getOriginalFile());
                    videoView.start();
                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.setVolume(0f, 0f);
                            mp.start();
                        }
                    });

                    //开始录音
                    if (sentence.getRecordFile().exists()) sentence.getRecordFile().delete();

                    mediaRecorder=new MediaRecorder();
                    System.out.println("此处出现问题");
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

                    mediaRecorder.setAudioSamplingRate(44100);
                    mediaRecorder.setAudioEncodingBitRate(96000);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

                    mediaRecorder.setOutputFile(sentence.getRecordFile().getPath());

                    try
                    {
                        mediaRecorder.prepare();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    mediaRecorder.start();
                }

                //抬起录音按钮
                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    holder.sentenceRec.setImageResource(R.mipmap.record);
                    //停止录音
                    videoView.pause();
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                    sentence.thread();
                }
                return true;
            }


        });
        //点击播放按钮 整合录音到视频中
        holder.sentencePlay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = holder.getAdapterPosition();
                Sentence sentence = sentenceList.get(position);
                Uri uri = Uri.fromFile(new File(sentence.getRecordFile().getPath()));
                //停止播放录音
                if(mediaPlayer != null) {
                    mediaPlayer.reset();
                }
                if(!sentence.getRecordFile().exists()){
                    Toast.makeText(activity, "录音不存在", Toast.LENGTH_SHORT).show();
                } else{
                    try {
                        mediaPlayer.setDataSource(sentence.getRecordFile().getPath());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        videoView.setVideoURI(uri);
                        videoView.start();
                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.setVolume(0f, 0f);
                                mp.start();
                            }
                        });
                    }catch (Exception e){
                        throw new RuntimeException(e);
                    }
                    //原代码中为什么播放完毕要暂停mediaPlayer？
                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mediaPlayer.stop();
                        }
                    });
                }

            }
        });

        return holder;
    }

    public void onBindViewHolder(SentenceAdapter.ViewHolder holder, int position)
    {
        Sentence sentence=sentenceList.get(position);
        holder.sentenceText.setText(sentence.getText());
    }

    public int getItemCount()
    {
        return sentenceList.size();
    }


//    public void destroy()
//    {
//        //停止播放录音
//        if(mediaPlayer!=null)
//        {
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//        //停止录音
//        if(mediaRecorder!=null)
//        {
//            mediaRecorder.release();
//            mediaRecorder = null;
//        }
//        if(videoView != null){
//            videoView.release();
//            videoView = null;
//        }
//    }
}
