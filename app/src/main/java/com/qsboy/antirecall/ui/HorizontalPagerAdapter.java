/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qsboy.antirecall.R;


/**
 * Created by GIGAMOLE on 7/27/16.
 */
public class HorizontalPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private final Entity[] entities = new Entity[]{
            new Entity(
                    R.drawable.ic_strategy,
                    "Strategy"
            ),
            new Entity(
                    R.drawable.ic_design,
                    "Design"
            ),
            new Entity(
                    R.drawable.ic_development,
                    "Development"
            ),
            new Entity(
                    R.drawable.ic_qa,
                    "Quality Assurance"
            )
    };

    static class Entity {
        static void setupItem(final View view, final Entity entity) {
            final TextView txt = view.findViewById(R.id.txt_item);
            txt.setText(entity.getTitle());

            final ImageView img = view.findViewById(R.id.img_item);
            img.setImageResource(entity.getRes());
        }

        String title;
        String desc;
    }

    public HorizontalPagerAdapter(final Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
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
        Entity.setupItem(view, entities[position]);

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
