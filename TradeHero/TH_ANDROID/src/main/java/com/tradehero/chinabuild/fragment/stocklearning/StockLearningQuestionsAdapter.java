package com.tradehero.chinabuild.fragment.stocklearning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.tradehero.th.R;

import java.util.ArrayList;

/**
 * Created by palmer on 15/3/30.
 */
public class StockLearningQuestionsAdapter extends BaseAdapter {

    private ArrayList<StockLearningQuestionsItem> questionsItemDTOs = new ArrayList();
    private LayoutInflater inflater;

    public StockLearningQuestionsAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return questionsItemDTOs.size();
    }

    @Override
    public StockLearningQuestionsItem getItem(int i) {
        return questionsItemDTOs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.stock_learning_questions_item, null);
            viewHolder = new ViewHolder();
            viewHolder.questionsProportionTV = (TextView) convertView.findViewById(R.id.textview_questions_set_proportion);
            viewHolder.questionsBgIV = (ImageView) convertView.findViewById(R.id.imageview_questions_set_bg);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        StockLearningQuestionsItem questionsItemDTO = questionsItemDTOs.get(i);

        if (questionsItemDTO.getTotalNumber() > 0) {
            String proportion = questionsItemDTO.getLastNumber() + "/" + questionsItemDTO.getTotalNumber();
            viewHolder.questionsProportionTV.setText(proportion);
        }
        viewHolder.questionsBgIV.setImageResource(questionsItemDTO.getBgResId());
        return convertView;
    }

    public void setQuestionsItemDTOs(ArrayList<StockLearningQuestionsItem> questionsItemDTOs) {
        if (questionsItemDTOs != null) {
            this.questionsItemDTOs.clear();
            this.questionsItemDTOs.addAll(questionsItemDTOs);
        }
    }

    public final class ViewHolder {
        public TextView questionsProportionTV;
        public ImageView questionsBgIV;
    }
}
