package com.example.doublez;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

/**
 * 用于转化并装载录制音频
 * */
public class Sentence
{
    private int num;
    private String text;
    private ReadAACFileThread audioThread = null;
    private double[] mfcc=null;
    private File recordFile = null;
    private Uri originalFile = null;

    public Sentence(int num, String text, String recordFilepath, String originalFile)
    {
        this.num=num;
        this.text=text;
        //此处文件路径设置是有问题的
        this.recordFile = new File("/mnt/sdcard", recordFilepath);
        this.originalFile = Uri.parse("android.resource://" + originalFile);
//        this.originalFile = new File("file:///android_assets/" + originalFile);
        this.audioThread = new ReadAACFileThread(recordFile.getAbsolutePath());
    }
    //似乎这个num属性没什么用，可以考虑删除
    public int getNum()
    {
        return this.num;
    }

    public String getText()
    {
        return this.text;
    }

    public ReadAACFileThread getThread()
    {
        return this.audioThread;
    }

    public File getRecordFile() {
        return recordFile;
    }

    public Uri getOriginalFile() {
        return originalFile;
    }

    public void thread() {
        this.audioThread.start();
    }

}
