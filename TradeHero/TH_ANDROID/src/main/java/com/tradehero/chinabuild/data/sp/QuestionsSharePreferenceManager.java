package com.tradehero.chinabuild.data.sp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by palmer on 15/4/1.
 */
public class QuestionsSharePreferenceManager {

    //SharePreference Name
    private final static String QUESTION_SP_NAME = "questions_sp";
    private final static String DIVIDE_TAG = "|";
    private final static String WRONG_TAG  = "_failed";
    private final static String CURRENT_QUES = "_curr";

    public static void updateWrongAnswers(Context context,String questionSetId, int wrongNumber){
        if(wrongNumber<=0){
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(QUESTION_SP_NAME, Context.MODE_PRIVATE);
        String key = questionSetId + WRONG_TAG;
        String wrongQuestionsStr = addOneWrongQuestion(sp.getString(key, ""), wrongNumber);
        sp.edit().putString(key, wrongQuestionsStr).commit();
    }

    public static ArrayList<Integer> getWrongQuestions(Context context, String questionSetId){
        SharedPreferences sp = context.getSharedPreferences(QUESTION_SP_NAME, Context.MODE_PRIVATE);
        String key = questionSetId + WRONG_TAG;
        String failedQuestionStr  = sp.getString(key, "");
        return getWrongQuestions(failedQuestionStr);
    }

    public static void removeOneWrongAnswer(Context context, String questionSetId, int wrongNumber){
        SharedPreferences sp = context.getSharedPreferences(QUESTION_SP_NAME, Context.MODE_PRIVATE);
        String key = questionSetId + WRONG_TAG;
        ArrayList<Integer> failedQuestions  = getWrongQuestions(sp.getString(key, ""));
        ArrayList<Integer> currentFailedQuestions = new ArrayList();
        for(Integer integerValue: failedQuestions){
            if(integerValue.intValue() != wrongNumber){
                currentFailedQuestions.add(integerValue);
            }
        }
        String currentFailedQuestionStr = toWrongNumberString(currentFailedQuestions);
        sp.edit().putString(key, currentFailedQuestionStr).commit();
    }


    private static String toWrongNumberString(ArrayList<Integer> x) {
        int length = x.size();
        if (length <= 0) {
            return "";
        }
        if (length == 1) {
            return String.valueOf(x.get(0));
        }
        int temp = length -1;
        String result = "";
        for (int num = 0; num < temp;num++){
            result = result + x.get(num) + DIVIDE_TAG;
        }
        result = result + x.get(length-1);
        return result;
    }

    private static String addOneWrongQuestion(String olds, int newOne){
        if(newOne <= 0){
            return olds;
        }
        if(olds==null || olds.equals("")){
            return String.valueOf(newOne);
        }
        return olds + DIVIDE_TAG + String.valueOf(newOne);
    }


    private static ArrayList<Integer> getWrongQuestions(String str){
        ArrayList<Integer> wrongQuestions = new ArrayList();
        if(str==null || str.equals("")){
            return wrongQuestions;
        }
        if(!str.contains(DIVIDE_TAG)){
            try {
                Integer value = Integer.valueOf(str);
                wrongQuestions.add(value);
            }catch (Exception e){
                e.printStackTrace();
            }
            return wrongQuestions;
        }
        String[] strs = str.split(DIVIDE_TAG);
        int length = strs.length;
        for(int num=0;num<length;num++){
            try {
                Integer value = Integer.valueOf(str);
                wrongQuestions.add(value);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return wrongQuestions;
    }
}
