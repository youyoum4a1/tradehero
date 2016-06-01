package com.ayondo.academy.fragments.dashboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerLayoutUtil
{
    @NonNull public static View createDrawerItemFromTabType(@NonNull Context context, @NonNull ViewGroup drawerLayout, @NonNull RootFragmentType tabType)
    {
        View created = LayoutInflater.from(context).inflate(tabType.viewResId, drawerLayout, false);
        ImageView image = (ImageView) created.findViewById(android.R.id.icon);
        image.setImageResource(tabType.drawableResId);
        TextView title = (TextView) created.findViewById(android.R.id.text1);
        title.setText(tabType.stringResId);
        created.setTag(tabType);

        //Add the background selector
        //created.setBackgroundResource(R.drawable.basic_transparent_selector);

        return created;
    }
}
