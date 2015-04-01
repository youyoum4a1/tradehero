package com.tradehero.chinabuild.fragment.stocklearning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.tradehero.th.R;

/**
 * Created by palmer on 15/3/31.
 */
public class StockLearningAnswersHistoryAdapter extends BaseAdapter{

//    private ArrayList<StockLearningQuestionItem> questionItems = new ArrayList();
    private LayoutInflater inflater;

    public StockLearningAnswersHistoryAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

//    public void setQuestionItems(ArrayList<StockLearningQuestionItem> questionItems){
//        if(questionItems!=null){
//            this.questionItems.clear();
//            this.questionItems.addAll(questionItems);
//        }
//    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if(convertView==null) {
            convertView = inflater.inflate(R.layout.stock_learning_question_item, null);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        return convertView;
    }

    public final class ViewHolder{
        public TextView quesDescTV;

    }
}
