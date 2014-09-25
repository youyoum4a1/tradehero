package com.tradehero.th.fragments.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.tradehero.th.R;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

public class SettingsListAdapter extends BaseAdapter
{
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
        this.items = new ArrayList<>();
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
        Timber.d("getCount");
        return items != null ? items.size() : 0;
    }

    @Override public Object getItem(int i)
    {
        Timber.d("getItem %d", i);
        return items != null ? items.get(i) : null;
    }

    @Override public long getItemId(int i)
    {
        Timber.d("getItemId %d", i);
        return i;
    }

    @SuppressWarnings("unchecked")
    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        Timber.d("getView %d", position);
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
        Timber.d("notifyDataSetChanged");
        super.notifyDataSetChanged();
    }
}
