package com.tradehero.th.fragments.social.friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.tradehero.th.R;
import java.util.List;

public class SocalTypeListAdapter extends ArrayAdapter<SocialTypeItem>{

    private LayoutInflater mInflater;
    private Context mContext;

    public SocalTypeListAdapter(Context context, int resource, List<SocialTypeItem> objects) {
        super(context, resource, objects);
        mContext= context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewGroup viewGroup;
        if (convertView == null)
        {
             viewGroup = (ViewGroup)mInflater.inflate(R.layout.invite_friends_item, (ViewGroup) convertView, false);
        }
        else
        {
            viewGroup = (ViewGroup)convertView;
        }
        displayItem(position,viewGroup);
        return viewGroup;
    }

    private void displayItem(int position,ViewGroup viewGroup)
    {
        SocialTypeItem item = getItem(position);

        ImageView logoView = (ImageView)viewGroup.findViewById(R.id.social_item_logo);
        TextView titleView = (TextView)viewGroup.findViewById(R.id.social_item_title);

        logoView.setImageResource(item.imageResource);
        titleView.setText(getContext().getString(item.titleResource));

        int pL = viewGroup.getPaddingLeft();
        int pR = viewGroup.getPaddingRight();
        int pT = viewGroup.getPaddingTop();
        int pB = viewGroup.getPaddingBottom();

        viewGroup.setBackgroundResource(item.backgroundResource);
        viewGroup.setPadding(pL, pT, pR, pB);
    }
}
