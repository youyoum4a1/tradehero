package com.tradehero.chinabuild.fragment.stocklearning;

import java.util.ArrayList;

/**
 * Created by palmer on 15/3/30.
 */
public class StockLearningQuestionsItem {

    private int id;
    private int totalNumber;
    private String name;
    private ArrayList<StockLearningQuestionItem> questionItems = new ArrayList();

    public ArrayList<StockLearningQuestionItem> getQuestionItems() {
        return questionItems;
    }

    public void setQuestionItems(ArrayList<StockLearningQuestionItem> questionItems) {
        this.questionItems = questionItems;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
