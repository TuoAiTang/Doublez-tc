package com.example.doublez;


public class MainContent
{
    //滑动条中存放的对象
    private int Num;
    private String Name;
    private int Id;

    public MainContent(int Num,String Name, int Id)
    {
        this.Num=Num;
        this.Name=Name;
        this.Id=Id;
    }
    //num属性没什么用，在adapter中直接用position代替
    public int getNum()
    {
        return this.Num;
    }

    public String getName()
    {
        return this.Name;
    }

    public int getId()
    {
        return this.Id;
    }

}