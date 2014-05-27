package com.tradehero.th.fragments.share;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.adapters.DTOSetAdapter;
import com.tradehero.th.models.share.ShareDestination;
import com.tradehero.th.models.share.ShareDestinationIndexResComparator;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class ShareDestinationSetAdapter extends DTOSetAdapter<ShareDestination>
{
    public static int VIEW_RES_ID = R.layout.common_dialog_item_layout;

    //<editor-fold desc="Constructors">
    public ShareDestinationSetAdapter(Context context)
    {
        super(context);
    }

    public ShareDestinationSetAdapter(Context context, Collection<ShareDestination> objects)
    {
        super(context, objects);
    }
    //</editor-fold>

    @Override protected Set<ShareDestination> createSet(Collection<ShareDestination> objects)
    {
        Set<ShareDestination> set = new TreeSet<>(new ShareDestinationIndexResComparator(context.getResources()));
        if (objects != null)
        {
            set.addAll(objects);
        }
        return set;
    }

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
