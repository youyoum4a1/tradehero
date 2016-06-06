package com.androidth.general.fragments.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.androidth.general.common.utils.SDKUtils;
import com.androidth.general.R;
import com.androidth.general.utils.GraphicUtil;
import java.util.List;

public class LollipopArrayAdapter<T> extends ArrayAdapter<T>
{
    public LollipopArrayAdapter(Context context, List<T> objects)
    {
        super(context, R.layout.sign_up_dropdown_item_selected, objects);
        setDropDownViewResource(R.layout.sign_up_dropdown_item);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = super.getView(position, convertView, parent);
        if (!SDKUtils.isLollipopOrHigher())
        {
            if (v instanceof TextView)
            {
                ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(null, null,
                        GraphicUtil.createStateListDrawableRes(v.getContext(), R.drawable.abc_spinner_mtrl_am_alpha), null);
            }
        }
        return v;
    }
}
