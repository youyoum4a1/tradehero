package com.tradehero.th.fragments.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.adapters.DTOSetAdapter;
import com.tradehero.th.models.share.ShareDestination;
import java.util.Collection;
import java.util.Comparator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
