package com.example.doublez;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.List;

public class RecentItemAdapter extends RecyclerView.Adapter<RecentItemAdapter.ViewHolder>
{

    private List<RecentItem> recentitemList;
    private Activity activity;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        View recentitemView;
        TextView recentitemDate;
        ImageView recentitemImageId;
        TextView recentitemContent;
        TextView recentitemScore;

        public ViewHolder (View view)
        {
            super(view);
            recentitemView=view;
            recentitemDate=(TextView)view.findViewById(R.id.recent_date);
            recentitemImageId=(ImageView)view.findViewById(R.id.recent_image);
            recentitemContent=(TextView)view.findViewById(R.id.recent_content);
            recentitemScore=(TextView)view.findViewById(R.id.recent_score);
        }
    }

    public RecentItemAdapter(List<RecentItem> recentitemList, Activity activity)
    {
        this.recentitemList=recentitemList;
        this.activity=activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType)
    {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.recentitemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position=holder.getAdapterPosition();
                RecentItem recentitem=recentitemList.get(position);
                // Item的Text响应
                switch(recentitem.getContent())
                {
                    case "1":

                        break;
                    case "2":

                    default:
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position)
    {
        RecentItem recentitem=recentitemList.get(position);
        holder.recentitemDate.setText(recentitem.getDate());
        holder.recentitemImageId.setImageResource(getResource(recentitem.getImageId()));
        holder.recentitemContent.setText(recentitem.getContent());
        holder.recentitemScore.setText(recentitem.getScore());
    }

    @Override
    public int getItemCount()
    {
        return recentitemList.size();
    }

    public int  getResource(String imageName){
        Class drawable = R.drawable.class;
        try {
            Field field = drawable.getField(imageName);
            return field.getInt(imageName);
        } catch (NoSuchFieldException e) {
            return 0;
        } catch (IllegalAccessException e) {
            return 0;
        }
    }
}