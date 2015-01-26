package com.tradehero.th.fragments.social.follower;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.tradehero.th.api.discussion.MessageType;

public class MessageTypeAdapter extends ArrayAdapter<MessageType>
{
    @IdRes private final int textViewResourceId;

    //<editor-fold desc="Constructors">
    public MessageTypeAdapter(@NonNull Context context,
            @LayoutRes int resource,
            @IdRes int textViewResourceId,
            @NonNull MessageType[] objects)
    {
        super(context, resource, textViewResourceId, objects);
        this.textViewResourceId = textViewResourceId;
    }
    //</editor-fold>

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = super.getView(position, convertView, parent);
        TextView text = (TextView) view.findViewById(textViewResourceId);
        text.setText(getItem(position).titleResource);
        return view;
    }
}
