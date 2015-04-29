package com.tradehero.chinabuild.fragment.stocklearning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.tradehero.th.R;

import java.util.ArrayList;

/**
 * Created by palmer on 15/3/30.
 */
public class StockLearningQuestionsAdapter extends BaseExpandableListAdapter {

    private LayoutInflater inflater;

    private String[] parents = new String[]{"LEVEL_A","LEVEL_B","LEVEL_C"};

    private ArrayList<StockLearningSubGroup> levelAItems = new ArrayList();
    private ArrayList<StockLearningSubGroup> levelBItems = new ArrayList();
    private ArrayList<StockLearningSubGroup> levelCItems = new ArrayList();

    public StockLearningQuestionsAdapter(Context context, ArrayList<StockLearningSubGroup> levelAItems,
                                         ArrayList<StockLearningSubGroup> levelBItems, ArrayList<StockLearningSubGroup> levelCItems) {
        inflater = LayoutInflater.from(context);
        if (levelAItems != null) {
            this.levelAItems.addAll(levelAItems);
        }
        if (levelBItems != null) {
            this.levelBItems.addAll(levelBItems);
        }
        if (levelCItems != null) {
            this.levelCItems.addAll(levelCItems);
        }
    }

    @Override
    public int getGroupCount() {
        return parents.length;
    }

    @Override
    public int getChildrenCount(int i) {
        if (i == 0) {
            return levelAItems.size();
        }
        if (i == 1) {
            return levelBItems.size();
        }
        if (i == 2) {
            return levelCItems.size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int i) {
        return parents[i];
    }

    @Override
    public Object getChild(int i, int i1) {
        if (i == 0) {
            return levelAItems.get(i1);
        }
        if (i == 1) {
            return levelBItems.get(i1);
        }
        if (i == 2) {
            return levelCItems.get(i1);
        }
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int position, boolean isExpand, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.stock_learning_questions_item, parent, false);
        RelativeLayout questionGroupLayout = (RelativeLayout)convertView.findViewById(R.id.relativelayout_questions_set_bg);
        ImageView questionGroupIV = (ImageView)convertView.findViewById(R.id.imageview_questions_set);
        View divider = convertView.findViewById(R.id.view_divider);
        if(isExpand){
            divider.setVisibility(View.GONE);
        }else{
            divider.setVisibility(View.VISIBLE);
        }
        if(position==0){
            questionGroupLayout.setBackgroundResource(R.drawable.learning_a_bg);
            questionGroupIV.setImageResource(R.drawable.learning_a_font);
        }
        if(position==1){
            questionGroupLayout.setBackgroundResource(R.drawable.learning_b_bg);
            questionGroupIV.setImageResource(R.drawable.learning_b_font);
        }
        if(position==2){
            questionGroupLayout.setBackgroundResource(R.drawable.learning_c_bg);
            questionGroupIV.setImageResource(R.drawable.learning_c_font);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.stock_learning_questions_subitem, parent, false);
        TextView nameTV = (TextView)convertView.findViewById(R.id.textview_question_group_name);
        TextView progressTV = (TextView)convertView.findViewById(R.id.textview_question_group_progress);
        StockLearningSubGroup stockLearningSubGroup = null;
        if(groupPosition==0){
            stockLearningSubGroup = levelAItems.get(childPosition);
        }
        if(groupPosition==1){
            stockLearningSubGroup = levelBItems.get(childPosition);
        }
        if(groupPosition==2){
            stockLearningSubGroup = levelCItems.get(childPosition);
        }
        if(stockLearningSubGroup !=null){
            nameTV.setText(stockLearningSubGroup.getName());
            int current = stockLearningSubGroup.getLastNumber()+1;
            progressTV.setText(current + "/" + stockLearningSubGroup.getTotalNumber());
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
