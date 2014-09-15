package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.tradehero.th2.R;

/**
 * Created by huhaiping on 14-8-19.
 */
public class SpinnerExchangeIconAdapter extends BaseAdapter
{
    private Context context;
    private LayoutInflater inflater;
    private String[] strTextList;
    private int[] logoList;

    public SpinnerExchangeIconAdapter(Context context, String[] textList)
    {
        this.context = context;
        this.strTextList = textList;
        this.logoList = null;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public SpinnerExchangeIconAdapter(Context context, String[] textList, int[] logoList)
    {
        this.context = context;
        this.strTextList = textList;
        this.logoList = logoList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override public int getCount()
    {
        return strTextList == null ? 0 : strTextList.length;
    }

    @Override public Object getItem(int i)
    {
        return null;
    }

    @Override public long getItemId(int i)
    {
        return i;
    }

    @Override public View getView(int i, View convertView, ViewGroup viewGroup)
    {
        View v;
        TextView tvName;
        ImageView imgIcon;
        if (convertView == null)
        {
            v = inflater.inflate(R.layout.spinner_icon_text_item, null);
        }
        else
        {
            v = convertView;
        }
        tvName = (TextView) v.findViewById(R.id.tvSpinnerItemName);
        imgIcon = (ImageView) v.findViewById(R.id.imgSpinnerItemIcon);
        tvName.setText(strTextList[i]);
        if (logoList != null)
        {
            imgIcon.setVisibility(View.VISIBLE);
            imgIcon.setBackgroundResource(logoList[i]);
        }
        else
        {
            imgIcon.setVisibility(View.GONE);
        }

        return v;
    }
}
