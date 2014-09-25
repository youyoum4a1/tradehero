package com.tradehero.th.fragments.share;

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
import java.util.Set;
import java.util.TreeSet;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShareDestinationSetAdapter extends DTOSetAdapter<ShareDestination>
{
    public static final int VIEW_RES_ID = R.layout.common_dialog_item_layout;

    @NotNull @Inject Comparator<ShareDestination> shareDestinationIndexResComparator;

    //<editor-fold desc="Constructors">
    public ShareDestinationSetAdapter(@NotNull Context context)
    {
        super(context);
    }

    public ShareDestinationSetAdapter(@NotNull Context context, @Nullable Collection<ShareDestination> objects)
    {
        super(context, objects);
    }
    //</editor-fold>

    @Override @NotNull protected Set<ShareDestination> createSet(@Nullable Collection<ShareDestination> objects)
    {
        Set<ShareDestination> set = new TreeSet<>(shareDestinationIndexResComparator);
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
