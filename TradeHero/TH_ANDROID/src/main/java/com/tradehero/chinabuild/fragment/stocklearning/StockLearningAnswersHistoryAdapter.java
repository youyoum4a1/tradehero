package com.tradehero.chinabuild.fragment.stocklearning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.tradehero.th.R;

import java.util.ArrayList;

/**
 * Created by palmer on 15/3/31.
 */
public class StockLearningAnswersHistoryAdapter extends BaseAdapter {

    private ArrayList<Question> questionItems = new ArrayList();
    private ArrayList<QuestionStatusRecord> questionStatusRecords = new ArrayList();
    private LayoutInflater inflater;

    private int successColor;
    private int failedColor;

    public StockLearningAnswersHistoryAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        successColor = context.getResources().getColor(R.color.stock_learning_summary_success_color);
        failedColor = context.getResources().getColor(R.color.stock_learning_summary_failed_color);
    }

    public void setQuestionItems(ArrayList<Question> questionItems, ArrayList<QuestionStatusRecord> records) {
        if (questionItems != null) {
            this.questionItems.clear();
            this.questionItems.addAll(questionItems);
        }
        if (records != null) {
            this.questionStatusRecords.clear();
            this.questionStatusRecords.addAll(records);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return questionItems.size();
    }

    @Override
    public Question getItem(int i) {
        return questionItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.stock_learning_question_item, null);
            viewHolder = new ViewHolder();
            viewHolder.quesDescTV = (TextView) convertView.findViewById(R.id.textview_question_desc);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Question question = questionItems.get(i);
        if (isQuestionError(question)) {
            viewHolder.quesDescTV.setTextColor(failedColor);
        } else {
            viewHolder.quesDescTV.setTextColor(successColor);
        }
        int index = i + 1;
        viewHolder.quesDescTV.setText(index + ": " + question.content);
        return convertView;
    }

    private boolean isQuestionError(Question question) {
        for (QuestionStatusRecord record : questionStatusRecords) {
            if (record.question_id == question.id) {
                if (record.question_choice.toLowerCase().equals(question.answer.toLowerCase())) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return true;
    }

    public final class ViewHolder {
        public TextView quesDescTV;
    }
}
