package com.tradehero.chinabuild.fragment.stocklearning;

import java.util.ArrayList;

/**
 * Created by palmer on 15/5/7.
 */
public class StockLearningQuestionManager {

    private static StockLearningQuestionManager stockLearningQuestionManager;

    private ArrayList<Question> reAnswerQuestions = new ArrayList();

    private StockLearningQuestionManager() {

    }

    public static StockLearningQuestionManager getInstance() {
        synchronized (StockLearningQuestionManager.class) {
            if (stockLearningQuestionManager == null) {
                stockLearningQuestionManager = new StockLearningQuestionManager();
            }
            return stockLearningQuestionManager;
        }
    }

    public ArrayList<Question> getReAnswerQuestions() {
        return reAnswerQuestions;
    }

    public void setReAnswerQuestions(ArrayList<Question> reAnswerQuestions) {
        this.reAnswerQuestions.addAll(reAnswerQuestions);
    }

    public void removeReAnswerQuestion(int question_id) {
        int size = reAnswerQuestions.size();
        for (int num = 0; num < size; num++) {
            if (reAnswerQuestions.get(num).id == question_id) {
                reAnswerQuestions.remove(num);
                return;
            }
        }
    }

    public void clearReAnswerQuestions() {
        reAnswerQuestions.clear();
    }

    public boolean isNeedReAnswer(int question_id) {
        for (Question question : reAnswerQuestions) {
            if (question.id == question_id) {
                return true;
            }
        }
        return false;
    }


}
