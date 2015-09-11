package com.tradehero.chinabuild.mainTab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.tradehero.th.R;
import java.util.List;

public class MainTabBuyWhatAdapter extends BaseAdapter{
    private List<MainTabBuyWhatDTO> mDto;
     private LayoutInflater inflater;

    public MainTabBuyWhatAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 4;
//        return mDto.size();
    }

    @Override
    public MainTabBuyWhatDTO getItem(int i) {
        return mDto.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.main_tab_buy_what_item, null);
        }
//        MainTabBuyWhatDTO item = getItem(i);
        // ImageView choiceImageView = (ImageView)convertView.findViewById(R.id.choice);
        // if (mSelectedPosition == i) {
        //     choiceImageView.setImageResource(R.drawable.choice);
        // } else {
        //     choiceImageView.setImageResource(R.drawable.no_choice);
        // }
        // TextView stockTitle = (TextView)convertView.findViewById(R.id.stock_name);
        // stockTitle.setText(item.securityName);
        // TextView stockID = (TextView)convertView.findViewById(R.id.stock_id);
        // stockID.setText(item.securityId);
        // TextView operation = (TextView)convertView.findViewById(R.id.operation);
        // operation.setText(item.entrustName);
        // TextView price = (TextView)convertView.findViewById(R.id.price);
        // price.setText(String.valueOf(item.entrustPrice));
        // TextView numbers = (TextView)convertView.findViewById(R.id.numbers);
        // numbers.setText(String.valueOf(item.entrustAmount));
        // TextView timeDate = (TextView)convertView.findViewById(R.id.time_date);
        // TextView timeTime = (TextView) convertView.findViewById(R.id.time_time);
        // timeDate.setText(item.entrustDate);
        // timeTime.setText(item.entrustTime);

        return convertView;
    }

    public void setItems(List<MainTabBuyWhatDTO> dto) {
        mDto = dto;
    }
}
