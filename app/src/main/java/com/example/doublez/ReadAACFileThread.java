package com.example.doublez;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;


import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

/**
 * Created by ZhangHao on 2017/4/18.
 * 播放aac音频文件
 */

public class ReadAACFileThread extends Thread {
/**
 * 用于处理录制的音频
 *
 * */

    //音频解码器
    private AACDecoderUtil audioUtil;
    //文件路径
    private String filePath;
    //文件读取完成标识
    private boolean isFinish = false;
    //这个值用于找到第一个帧头后，寻找第二个帧头时避免无意义的搜索
    private int FRAME_MIN_LEN = 50;
    //一般AAC帧大小不超过200k,如果解码失败可以尝试增大这个值
    private static int FRAME_MAX_LEN = 100 * 1024;
    //根据帧率获取的解码每帧需要休眠的时间,根据实际帧率进行操作
    private int PRE_FRAME_TIME = 10;
    //记录获取的帧数
    private int count = 0;


    public ReadAACFileThread() {
        this.audioUtil = new AACDecoderUtil();
        this.filePath = Environment.getExternalStorageDirectory().getPath() +"/test.aac";
        this.audioUtil.start();
    }

    public ReadAACFileThread(String filename) {
        this.audioUtil = new AACDecoderUtil();
        this.filePath = Environment.getExternalStorageDirectory().getPath() + "/"+filename;
        this.audioUtil.start();
    }

    @Override
    public void run() {
        super.run();
        File file = new File(filePath);
        //判断文件是否存在
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                //保存完整数据帧
                byte[] frame = new byte[FRAME_MAX_LEN];
                //当前帧长度
                int frameLen = 0;
                //每次从文件读取的数据
                byte[] readData = new byte[10*1024];
                //开始时间
                long startTime = System.currentTimeMillis();
                //循环读取数据
                while (!isFinish)
                {
                    if (fis.available() > 0)
                    {
                        //从流中将数据读入readData，返回读入的字节数，一次处理一部分字节，直至全部处理完
                        int readLen = fis.read(readData);
                        //当前长度小于最大值
                        if (frameLen + readLen < FRAME_MAX_LEN)
                        {
                            //将readData拷贝到frame frameLen的作用是接着上一次处理的字节
                            System.arraycopy(readData, 0, frame, frameLen, readLen); //已经解码部分不要
                            //修改frameLen 因为0到frameLen-1处已经装入了数据
                            frameLen += readLen;
                            //寻找第一个帧头
                            int headFirstIndex = findHead(frame, 0, frameLen);
                            while (headFirstIndex >= 0 && isHead(frame, headFirstIndex)) // 后一个条件多余？
                            {
                                //寻找第二个帧头
                                int headSecondIndex = findHead(frame, headFirstIndex + FRAME_MIN_LEN, frameLen);
                                //如果第二个帧头存在，则两个帧头之间的就是一帧完整的数据
                                if (headSecondIndex > 0 && isHead(frame, headSecondIndex)) // 后一个条件多余?
                                {
                                    //视频解码
                                    count++;
                                    Log.e("ReadAACFileThread", "Length : " + (headSecondIndex - headFirstIndex));
                                    audioUtil.decode(frame, headFirstIndex, headSecondIndex - headFirstIndex);
                                    //截取frame中headSecondIndex之后的有效数据,并放到frame最前面
                                    byte[] temp = Arrays.copyOfRange(frame, headSecondIndex, frameLen);
                                    System.arraycopy(temp, 0, frame, 0, temp.length);
                                    //修改frameLen的值 改为第二帧帧头到末尾所包含长度
                                    frameLen = temp.length;
                                    //线程休眠
                                    sleepThread(startTime, System.currentTimeMillis());
                                    //重置开始时间
                                    startTime = System.currentTimeMillis();
                                    //继续寻找数据帧
                                    headFirstIndex = findHead(frame, 0, frameLen);
                                }
                                else
                                {
                                    //找不到第二个帧头
                                    headFirstIndex = -1;
                                }
                            }
                        }
                        else // 如果frameLen的90+k中，最后10k有帧头怎么办，不应该减去80K吗？
                        {
                            //如果长度超过最大值，frameLen置0
                            frameLen = 0;
                        }
                    }
                    else
                    {
                        //文件读取结束
                        isFinish = true;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("ReadAACFileThread", "AllCount: " + count + "   Error Count: " + audioUtil.getCount());
        } else {
            Log.e("ReadH264FileThread", "File not found");
        }
        audioUtil.stop();

    }

    /**
     * 寻找指定buffer中AAC帧头的开始位置
     *
     * @param startIndex 开始的位置
     * @param data       数据
     * @param max        需要检测的最大值
     * @return
     */
    private int findHead(byte[] data, int startIndex, int max) {
        int i;
        for (i = startIndex; i <= max; i++) {
            //发现帧头
            if (isHead(data, i))
                break;
        }
        //检测到最大值，未发现帧头
        if (i == max) {
            i = -1;
        }
        return i;
    }

    /**
     * 判断aac帧头
     */
    private boolean isHead(byte[] data, int offset) {
        boolean result = false;
        if (data[offset] == (byte) 0xFF && data[offset + 1] == (byte) 0xF1 && data[offset + 3] == (byte) 0x40) {
            result = true;
        }
        return result;
    }

    //修眠
    private void sleepThread(long startTime, long endTime) {
        //根据读文件和解码耗时，计算需要休眠的时间
        long time = PRE_FRAME_TIME - (endTime - startTime);
        if (time > 0) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public double[] getCeps()
    {
        double[] x = audioUtil.getCeps();
        double[] y=new double[12];  // 第2-13项系数
        for (int i = 0; i < 12; i++)
        {
            y[i]=x[i+1];
            Log.d("MFCC",Double.toString(y[i]));
        }
        Log.d("MFCC","-------------------------------");

        return y;
    }
}
