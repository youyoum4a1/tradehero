package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.tradehero.chinabuild.data.NewsDTO;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by palmer on 15/1/16.
 */
public class NewsItemAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private List<NewsDTO> newsDTOList = new ArrayList<>();

    public NewsItemAdapter(Context context, List<NewsDTO> newsDTOList){
        inflater = LayoutInflater.from(context);
        this.newsDTOList.addAll(newsDTOList);
    }

    @Override
    public int getCount() {
        return newsDTOList.size();
    }

    @Override
    public Object getItem(int i) {
        return newsDTOList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(convertView==null) {
            convertView = inflater.inflate(R.layout.discovery_news_item, null);
            viewHolder = new ViewHolder();
            viewHolder.titleTextView = (TextView)convertView.findViewById(R.id.textview_discovery_news_item_title);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        NewsDTO newsDTO = newsDTOList.get(i);
        THLog.d(newsDTO.toString());
        viewHolder.titleTextView.setText(newsDTO.title);
        return convertView;
    }

    public void addNewsDTOSet(List<NewsDTO> newsDTOList){
        this.newsDTOList.clear();
        this.newsDTOList.addAll(newsDTOList);
    }


    public final class ViewHolder{
        public TextView titleTextView;
    }
}
