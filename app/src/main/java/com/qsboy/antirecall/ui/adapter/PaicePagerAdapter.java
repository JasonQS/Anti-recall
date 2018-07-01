/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.qsboy.antirecall.R;
import com.qsboy.antirecall.pay.Pay;


/**
 * Created by GIGAMOLE on 7/27/16.
 */
public class PaicePagerAdapter extends PagerAdapter {

    private Activity activity;
    private LayoutInflater mLayoutInflater;

    private final Entity[] entities = new Entity[]{
            new Entity(
                    "普通用户",
                    "每天免费查看2次撤回消息\n" +
                            "超出两次每次1元\n" +
                            "超出两次每次1元\n" +
                            "超出两次每次1元\n" +
                            "超出两次每次1元\n" +
                            "超出两次每次1元\n" +
                            "超出两次每次1元\n" +
                            "持续维护",
                    "1"
            ),
            new Entity(
                    "普通会员",
                    "每天无限次数查看撤回消息\n" +
                            "支持微信自动登录\n" +
                            "持续维护",
                    "2"
            ),
            new Entity(
                    "高级会员",
                    "每天无限次数查看撤回消息\n" +
                            "支持微信自动登录\n" +
                            "人工支持服务\n" +
                            "持续维护",
                    "3"
            ),
            new Entity(
                    "超级会员",
                    "每天无限次数查看撤回消息\n" +
                            "支持微信自动登录\n" +
                            "最先用上新feature\n" +
                            "人工支持服务\n" +
                            "持续维护",
                    "4"
            )
    };

    static class Entity {
        static void setupItem(final View view, final Entity entity, Activity activity) {
            final TextView text = view.findViewById(R.id.text_title);
            text.setText(entity.getTitle());

            final TextView desc = view.findViewById(R.id.text_desc);
            desc.setText(entity.getDesc());

            final Button btn = view.findViewById(R.id.btn_pay);
            btn.setOnClickListener(v -> new Pay(activity).pay(entity.getType()));
        }

        public Entity(String title, String desc, String type) {
            this.title = title;
            this.desc = desc;
            this.type = type;
        }

        String title;
        String desc;
        String type;

        public String getTitle() {
            return title;
        }

        public String getDesc() {
            return desc;
        }

        public String getType() {
            return type;
        }
    }

    public PaicePagerAdapter(Activity activity) {
        this.activity = activity;
        mLayoutInflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public int getItemPosition(final Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final View view;
        view = mLayoutInflater.inflate(R.layout.item_price, container, false);
        Entity.setupItem(view, entities[position], activity);

        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }
}
