package com.tradehero.th.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;
import com.tradehero.th.R;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by palmer on 14-10-15.
 */
public class CompetitionCollegesAdapter extends BaseAdapter{

    private String[] colleges = new String[]{};
    private String[] all_colleges = new String[]{};
    private LayoutInflater inflater;
    private CollegeFilter filter;

    public CompetitionCollegesAdapter(Context context, String[] colleges){
        inflater = LayoutInflater.from(context);
        if(colleges!=null){
            this.colleges = colleges;
            this.all_colleges = colleges;
        }
    }

    @Override
    public int getCount() {
        return colleges.length;
    }

    @Override
    public Object getItem(int i) {
        return colleges[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        Holder holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.competition_college_item, null);
            holder = new Holder();
            holder.tvCollegeName = (TextView)convertView.findViewById(R.id.textview_competition_college_name);
            convertView.setTag(holder);
        }else{
            holder = (Holder)convertView.getTag();
        }
        holder.tvCollegeName.setText(colleges[i]);
        return convertView;
    }

    public void setColleges(String[] colleges){
        if(colleges==null){
            this.colleges = new String[]{};
            return;
        }
        this.colleges = colleges;
    }

    public CollegeFilter getFilter(){
        if(filter==null){
            filter = new CollegeFilter();
        }
        return filter;
    }


    private class Holder {
        public TextView tvCollegeName;
    }

    public class CollegeFilter extends Filter{
        @Override
        public FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults=new FilterResults();
            ArrayList<String> collegesTemp = new ArrayList<String>();
            String inputStr = charSequence.toString();
            for(String college: all_colleges){
                if(college.contains(inputStr)){
                    collegesTemp.add(college);
                }
            }
            int size = collegesTemp.size();
            final String[] result = new String[size];

            for(int num=0;num<size;num++){
                result[num] = collegesTemp.get(num);
            }
            filterResults.values=result;
            filterResults.count = result.length;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            String[] finalResult = (String[])filterResults.values;
            setColleges(finalResult);
            notifyDataSetChanged();
        }
    }

}
