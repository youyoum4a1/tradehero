package com.tradehero.th.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import com.tradehero.th.R;
import com.tradehero.th.utills.Util;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileContentAdapter extends BaseAdapter
{

    private List<Item> items = new ArrayList<Item>();
    private LayoutInflater inflater;
    private Context ctx;

    public ProfileContentAdapter(Context context)
    {
        inflater = LayoutInflater.from(context);

        items.add(new Item("Alok", R.drawable.bhatia_img1, "vshd hggdjdkj hgdjjd hshj", "3 min",
                R.drawable.google_g));
        items.add(new Item("Alok", R.drawable.bhatia_img1, "vshd hggdjdkj hgdjjd hshj", "3 min",
                R.drawable.google_g));
        items.add(new Item("Alok", R.drawable.bhatia_img1, "vshd hggdjdkj hgdjjd hshj", "3 min",
                R.drawable.google_g));
        items.add(new Item("Alok", R.drawable.bhatia_img1, "vshd hggdjdkj hgdjjd hshj", "3 min",
                R.drawable.google_g));
        items.add(new Item("Alok", R.drawable.bhatia_img1, "vshd hggdjdkj hgdjjd hshj", "3 min",
                R.drawable.google_g));
        items.add(new Item("Alok", R.drawable.bhatia_img1, "vshd hggdjdkj hgdjjd hshj", "3 min",
                R.drawable.google_g));
        items.add(new Item("Alok", R.drawable.bhatia_img1, "vshd hggdjdkj hgdjjd hshj", "3 min",
                R.drawable.google_g));
        items.add(new Item("Alok", R.drawable.bhatia_img1, "vshd hggdjdkj hgdjjd hshj", "3 min",
                R.drawable.dbs));
        items.add(new Item("Alok", R.drawable.bhatia_img1, "vshd hggdjdkj hgdjjd hshj", "3 min",
                R.drawable.google_g));
        items.add(new Item("Alok", R.drawable.bhatia_img1, "vshd hggdjdkj hgdjjd hshj", "3 min",
                R.drawable.facebook));

        ctx = context;
    }

    @Override
    public int getCount()
    {
        return items.size();
    }

    @Override
    public Object getItem(int i)
    {
        return items.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return items.get(i).drawableUserImg;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        View v = view;
        ImageView Userpicture, VenderImg;
        TextView name, content, time;

        if (v == null)
        {
            v = inflater.inflate(R.layout.user_profile_timeline_item, viewGroup, false);
            v.setTag(R.id.timeline_user_profile_picture, v.findViewById(R.id.timeline_user_profile_picture));
            v.setTag(R.id.timeline_user_profile_name, v.findViewById(R.id.timeline_user_profile_name));
            v.setTag(R.id.timeline_item_content, v.findViewById(R.id.timeline_item_content));
            v.setTag(R.id.timeline_time, v.findViewById(R.id.timeline_time));
            v.setTag(R.id.timeline_vendor_picture, v.findViewById(R.id.timeline_vendor_picture));
        }

        Userpicture = (ImageView) v.getTag(R.id.timeline_user_profile_picture);
        VenderImg = (ImageView) v.getTag(R.id.timeline_vendor_picture);
        name = (TextView) v.getTag(R.id.timeline_user_profile_name);
        content = (TextView) v.getTag(R.id.timeline_item_content);
        time = (TextView) v.getTag(R.id.timeline_time);
        Item item = (Item) getItem(i);
        Userpicture.setImageBitmap(Util.getRoundedShape(
                BitmapFactory.decodeResource(ctx.getResources(), item.drawableUserImg)));
        VenderImg.setImageResource(item.drawablecompanyImg);
        name.setText(item.name);
        content.setText(item.content);
        time.setText(item.time);

        return v;
    }

    private class Item
    {
        final String name;
        final int drawableUserImg;
        final String content;
        final String time;
        final int drawablecompanyImg;

        Item(String name, int drawableUserImg, String content, String time, int drawablecompanyImg)
        {
            this.name = name;
            this.drawableUserImg = drawableUserImg;
            this.content = content;
            this.time = time;
            this.drawablecompanyImg = drawablecompanyImg;
        }
    }
}
