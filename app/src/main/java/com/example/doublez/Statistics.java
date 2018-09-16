package com.example.doublez;

import android.util.Log;
/**
 * 用于音频匹配
 *
 * */
public class Statistics
{
    public static double avg(double[] x) //求平均
    {
        double sum=0;
        for(int i=0;i<x.length;i++)
            sum+=x[i];
        return sum/x.length;
    }

    public static double div(double[] x) //平方的均值减去均值的平方
    {
        double sqsum=0;
        for(int i=0;i<x.length;i++)
            sqsum+=Math.pow(x[i],2);
        return sqsum/x.length-Math.pow(avg(x),2);
    }

    public static double[] norm(double[] x)
    {
        double[] y=new double[12];
        double abs=0;
        for(int i=0;i<12;i++)
            abs+=Math.pow(x[i],2);
        abs=Math.sqrt(abs);
        for(int i=0;i<12;i++)
            y[i]=(x[i])/abs;
        return y;
    }
    public static double dotProduct(double[] x,double[] y)
    {
        double sum=0;
        for(int i=0;i<x.length;i++)
            sum+=x[i]*y[i];
        return sum;
    }

    public static double Odistance(double[] x,double[] y)
    {
        double distance=0;
        for(int i=0;i<x.length;i++)
        {
            distance+=Math.pow((x[i]-y[i]),2);
        }
        return Math.sqrt(distance);
    }

    public static double loss(double[] x,double[] y)
    {
        double sum=0;
        for(int i=0;i<12;i++)
        {
            sum+=Math.abs(x[i]-y[i]);
        }
        return sum;
    }

    public static double Score1(double[] x,double[] y)
    {
        return 50+(dotProduct(norm(x),norm(y)))*50;

    }
    public static double Score2(double[] x,double[] y)
    {
        return (2 - Odistance(norm(x), norm(y))) * 50;
    }
    public static double Score3(double[] x,double[] y)
    {
        return Math.exp(-0.1*loss(norm(x),norm(y)))*100;
    }
}
