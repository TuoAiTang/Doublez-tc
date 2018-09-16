package com.example.doublez;

import org.litepal.crud.DataSupport;

/**
 * 不知道用于干什么
 * */

public class RecentItem extends DataSupport
{
    private String date;
    private String imageid;
    private String content;
    private String score;

    public RecentItem(String date, String imageid, String content, String score)
    {
        this.date=date;
        this.imageid=imageid;
        this.content=content;
        this.score=score;
    }
    public String getDate()
    {
        return date;
    }
    public String getImageId()
    {
        return imageid;
    }
    public String getContent()
    {
        return content;
    }
    public String getScore()
    {
        return score;
    }
}
