package com.tradehero.chinabuild.fragment.stocklearning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.tradehero.chinabuild.data.question.questionUtils.Question;
import com.tradehero.th.R;

import java.util.ArrayList;

/**
 * Created by palmer on 15/3/31.
 */
public class StockLearningAnswersHistoryAdapter extends BaseAdapter{

    private ArrayList<Question> questionItems = new ArrayList();
    private LayoutInflater inflater;

    private ArrayList<Integer> failedQuestions = new ArrayList();
    private int rightColor;
    private int wrongColor;

    public StockLearningAnswersHistoryAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    public void setQuestionItems(ArrayList<Question> questionItems){
        if(questionItems!=null){
            this.questionItems.clear();
            this.questionItems.addAll(questionItems);
        }
    }

    @Override
    public int getCount() {
        return questionItems.size();
    }

    @Override
    public Object getItem(int i) {
        return questionItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
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
        Question question = questionItems.get(i);
        viewHolder.quesDescTV.setText(question.getQid() + ":" + question.getQTitle());
        return convertView;
    }

    public final class ViewHolder{
        public TextView quesDescTV;

    }
}
