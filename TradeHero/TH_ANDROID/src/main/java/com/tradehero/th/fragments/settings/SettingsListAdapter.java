package com.tradehero.th.fragments.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;

import java.util.List;
import java.util.ArrayList;

/** Created with IntelliJ IDEA. User: nia Date: 17/10/13 Time: 4:06 PM To change this template use File | Settings | File Templates. */
public class SettingsListAdapter extends BaseAdapter
{
    public static final String TAG = SettingsListAdapter.class.getSimpleName();

    protected final LayoutInflater inflater;
    protected final Context context;
    private final int layoutResourceId;
    public CompoundButton.OnCheckedChangeListener checkboxCheckedListener;

    private List<String> items;

    public List<Boolean> getItemsChecked()
    {
        return itemsChecked;
    }

    public void setItemsChecked(List<Boolean> itemsChecked)
    {
        this.itemsChecked = itemsChecked;
    }

    private List<Boolean> itemsChecked;

    public SettingsListAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super();
        this.items = new ArrayList<String>();
        this.context = context;
        this.inflater = inflater;
        this.layoutResourceId = layoutResourceId;
    }

    public void setItems(List<String> items)
    {
        this.items = items;
    }

    public void addItem(String item)
    {
        if (this.items != null)
        {
            this.items.add(item);
        }
    }

    @Override public int getCount()
    {
        THLog.d(TAG, "getCount");
        return items != null ? items.size() : 0;
    }

    @Override public Object getItem(int i)
    {
        THLog.d(TAG, "getItem " + i);
        return items != null ? items.get(i) : null;
    }

    @Override public long getItemId(int i)
    {
        THLog.d(TAG, "getItemId " + i);
        return i;
    }

    @SuppressWarnings("unchecked")
    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        THLog.d(TAG, "getView " + position);
        if (convertView == null)
        {
            convertView = inflater.inflate(layoutResourceId, viewGroup, false);
        }

        SettingsItemView view = (SettingsItemView) convertView;
        view.display((String) getItem(position));

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.settingsItem_toggle);
        if (checkBox != null && itemsChecked != null)
        {
            checkBox.setChecked((boolean) itemsChecked.get(position));
            checkBox.setOnCheckedChangeListener(checkboxCheckedListener);
        }
        return view;
    }

    @Override public void notifyDataSetChanged()
    {
        THLog.d(TAG, "notifyDataSetChanged");
        super.notifyDataSetChanged();
    }
}
