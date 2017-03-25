/*
 * Copyright (c) 2017.
 * qsboy.com 版权所有
 */

package com.qiansheng.messagecapture;


import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.qiansheng.messagecapture.XBitmap.getLocalBitmap;


class XListAdapter extends RecyclerView.Adapter {

    static List<String> MsgList = new ArrayList<>();
    static List<String> TimeList = new ArrayList<>();
    static List<String> NameList = new ArrayList<>();

    private OnItemClickListener mOnItemClickListener;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm\nMM/dd", Locale.CHINA);

    XListAdapter() {

        Date date = new Date();
        String time = sdf.format(date);
        if (MsgList.size() == 0) {
            MsgList.add("被撤回的消息\n将会显示在这里");
            TimeList.add(time);
            NameList.add("钱盛:");
        }

    }

    interface OnItemClickListener {
        void onItemLongClick(View view, int position);
    }

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMessage, tvTime, tvName;
        private ImageView image;

        ViewHolder(View root) {
            super(root);
            tvMessage = (TextView) root.findViewById(R.id.Text_Message);
            tvTime = (TextView) root.findViewById(R.id.Text_Time);
            tvName = (TextView) root.findViewById(R.id.Text_Name);
            image = (ImageView) root.findViewById(R.id.Img_Message);
        }

        TextView getTvName() {
            return tvName;
        }

        TextView getTvMessage() {
            return tvMessage;
        }

        TextView getTvTime() {
            return tvTime;
        }

        ImageView getImage() {
            return image;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list, null));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        ViewHolder vh = (ViewHolder) holder;

        //设置时间
        String time;
        try {
            time = sdf.format(Long.parseLong(TimeList.get(position)));
        } catch (Exception e) {
            //为与4.0.3兼容
            time = TimeList.get(position);
        }

        //4.0.3
        //为了布局 把原先时间的分隔符从空格改为换行
        if (time.contains(" ")) {
            String[] s = time.split(" ");
            time = s[0] + "\n" + s[1];
        }

        vh.getTvTime().setText(time);

        //设置内容
        String text = MsgList.get(position);
        if (text.startsWith("#image")) {
            String imageTime = text.substring(6);
            Bitmap bitmap = getLocalBitmap(imageTime);
            vh.getImage().setImageBitmap(bitmap);
            vh.getTvMessage().setText(" ");
        } else {
            vh.getImage().setImageBitmap(null);
            vh.getTvMessage().setText(text);
        }

        //设置名字
        vh.getTvName().setText(NameList.get(position));

        if (mOnItemClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return TimeList.size();
    }

}
