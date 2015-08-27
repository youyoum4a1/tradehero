package com.tradehero.livetrade;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tradehero.th.R;

import java.util.ArrayList;

/**
 * Created by palmer on 15/7/19.
 */
public class SearchSecurityListAdapter  extends BaseAdapter {

    private LayoutInflater inflater;

    private ArrayList<ActualSecurityDTO> actualSecurityDTOs = new ArrayList();

    public SearchSecurityListAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<ActualSecurityDTO> actualSecurityDTOs){
        this.actualSecurityDTOs.clear();
        if(actualSecurityDTOs!=null){
            this.actualSecurityDTOs.addAll(actualSecurityDTOs);
        }
        notifyDataSetChanged();
    }

    public void addData(ArrayList<ActualSecurityDTO> actualSecurityDTOs){
        if(actualSecurityDTOs!=null){
            this.actualSecurityDTOs.addAll(actualSecurityDTOs);
        }
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return actualSecurityDTOs.size();
    }

    @Override
    public ActualSecurityDTO getItem(int i) {
        return actualSecurityDTOs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Holder holder = null;
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.security_search_list_item, viewGroup, false);
            holder = new Holder();
            holder.tvSecurityName = (TextView) convertView.findViewById(R.id.tvSecurityName);
            holder.tvSecurityRate = (TextView) convertView.findViewById(R.id.tvSecurityRate);
            holder.tvSecurityExchange = (TextView) convertView.findViewById(R.id.tvSecurityExchange);
            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }
        ActualSecurityDTO actualSecurityDTO = getItem(position);
        holder.tvSecurityRate.setVisibility(View.INVISIBLE);
        if(actualSecurityDTO != null) {
            holder.tvSecurityName.setText(actualSecurityDTO.name);
            holder.tvSecurityExchange.setText(actualSecurityDTO.exchange + "-" + actualSecurityDTO.symbol);
        }
        return convertView;
    }

    class Holder {
        public TextView tvSecurityRate;
        public TextView tvSecurityName;
        public TextView tvSecurityExchange;
    }
}
