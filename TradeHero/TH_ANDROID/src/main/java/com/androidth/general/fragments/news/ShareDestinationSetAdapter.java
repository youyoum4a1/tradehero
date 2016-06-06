package com.androidth.general.fragments.news;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.androidth.general.R;
import com.androidth.general.adapters.DTOSetAdapter;
import com.androidth.general.models.share.ShareDestination;
import java.util.Collection;
import java.util.Comparator;

public class ShareDestinationSetAdapter extends DTOSetAdapter<ShareDestination>
{
    public static final int VIEW_RES_ID = R.layout.common_dialog_item_layout;

    //<editor-fold desc="Constructors">
    public ShareDestinationSetAdapter(
            @NonNull Context context,
            @Nullable Comparator<ShareDestination> comparator,
            @Nullable Collection<ShareDestination> objects)
    {
        super(context, comparator, objects);
    }
    //</editor-fold>

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(VIEW_RES_ID, null);
        }

        TextView popupText = (TextView) convertView.findViewById(R.id.popup_text);
        popupText.setText(getItem(position).getNameResId());

        return convertView;
    }
}
