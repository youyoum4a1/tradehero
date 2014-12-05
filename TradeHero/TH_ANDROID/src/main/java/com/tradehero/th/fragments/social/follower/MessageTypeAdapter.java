package com.tradehero.th.fragments.social.follower;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.tradehero.th.api.discussion.MessageType;

public class MessageTypeAdapter extends ArrayAdapter<MessageType>
{
    int textViewResourceId;

    public MessageTypeAdapter(Context context, int resource, int textViewResourceId, MessageType[] objects)
    {
        super(context, resource, textViewResourceId, objects);
        this.textViewResourceId = textViewResourceId;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = super.getView(position, convertView, parent);
        TextView text = (TextView) view.findViewById(textViewResourceId);
        text.setText(getItem(position).titleResource);
        return view;
    }
}
