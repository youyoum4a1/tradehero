package com.tradehero.th.fragments.base;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.tradehero.common.utils.SDKUtils;
import com.tradehero.th.R;
import com.tradehero.th.utils.GraphicUtil;
import java.util.List;

public class LollipopArrayAdapter<T> extends ArrayAdapter<T>
{
    public LollipopArrayAdapter(Context context, List<T> objects)
    {
        this(context, R.layout.sign_up_dropdown_item_selected, R.layout.sign_up_dropdown_item, objects);
    }

    public LollipopArrayAdapter(Context context, @LayoutRes int layoutResId, @LayoutRes int dropDownViewResId, List<T> objects)
    {
        super(context, layoutResId, objects);
        setDropDownViewResource(dropDownViewResId);
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
