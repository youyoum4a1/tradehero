package com.tradehero.chinabuild.fragment.security;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tradehero.chinabuild.data.SecurityUserPositionDTO;
import com.tradehero.th.R;

import java.util.ArrayList;

/**
 * Created by palmer on 15/6/10.
 */
public class SecurityPostionAdapter extends BaseAdapter {

    private ArrayList<SecurityUserPositionDTO> opts = new ArrayList();
    private LayoutInflater inflater;

    public SecurityPostionAdapter(Context context, ArrayList<SecurityUserPositionDTO> opts){
        if(opts!=null){
            this.opts.addAll(opts);
        }
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return opts.size();
    }

    @Override
    public Object getItem(int i) {
        return opts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_security_position, null);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    public void setData(ArrayList<SecurityUserPositionDTO> opts){
        if(opts!=null){
            this.opts.clear();
            this.opts.addAll(opts);
            notifyDataSetChanged();
        }
    }

    public void addMoreData(ArrayList<SecurityUserPositionDTO> opts){
        if(opts!=null){
            this.opts.addAll(opts);
            notifyDataSetChanged();
        }
    }

    public final class ViewHolder {
    }
}
