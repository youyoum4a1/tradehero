package com.tradehero.chinabuild.data.sp;

import android.content.Context;
import android.content.SharedPreferences;
import com.tradehero.th.utils.StringUtils;
import java.util.ArrayList;

/**
 * Created by palmer on 15/4/1.
 */
public class QuestionsSharePreferenceManager
{

    //SharePreference Name
    private final static String QUESTION_SP_NAME = "questions_sp";
    private final static String DIVIDE_TAG = "&&"; //？？"|"和"*" 都会出Syntax error in regexp pattern near index 1: 错误。
    private final static String WRONG_TAG = "_failed";
    private final static String CURRENT_QUES = "_curr";

    public static void updateOneWrongAnswer(Context context, String questionSetId, int wrongNumber)
    {
        if (wrongNumber <= 0)
        {
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(QUESTION_SP_NAME, Context.MODE_PRIVATE);
        String key = questionSetId + WRONG_TAG;
        String wrongQuestionsStr = addOneWrongQuestion(sp.getString(key, ""), wrongNumber);
        sp.edit().putString(key, wrongQuestionsStr).commit();
    }

    public static ArrayList<Integer> getWrongQuestions(Context context, String questionSetId)
    {
        SharedPreferences sp = context.getSharedPreferences(QUESTION_SP_NAME, Context.MODE_PRIVATE);
        String key = questionSetId + WRONG_TAG;
        String failedQuestionStr = sp.getString(key, "");
        return getWrongQuestions(failedQuestionStr);
    }

    public static void removeOneWrongAnswer(Context context, String questionSetId, int wrongNumber)
    {
        SharedPreferences sp = context.getSharedPreferences(QUESTION_SP_NAME, Context.MODE_PRIVATE);
        String key = questionSetId + WRONG_TAG;
        ArrayList<Integer> failedQuestions = getWrongQuestions(sp.getString(key, ""));
        ArrayList<Integer> currentFailedQuestions = new ArrayList();
        for (Integer integerValue : failedQuestions)
        {
            if (integerValue.intValue() != wrongNumber)
            {
                currentFailedQuestions.add(integerValue);
            }
        }
        String currentFailedQuestionStr = toWrongNumberString(currentFailedQuestions);
        sp.edit().putString(key, currentFailedQuestionStr).commit();
    }

    public static int getLatestAnsweredQuestion(Context context, String questionSetId)
    {
        SharedPreferences sp = context.getSharedPreferences(QUESTION_SP_NAME, Context.MODE_PRIVATE);
        String key = questionSetId + CURRENT_QUES;
        return sp.getInt(key, 0);
    }

    public static void setLatestAnsweredQuestion(Context context, String questionSetId, int questionNum)
    {
        SharedPreferences sp = context.getSharedPreferences(QUESTION_SP_NAME, Context.MODE_PRIVATE);
        String key = questionSetId + CURRENT_QUES;
        sp.edit().putInt(key, questionNum).commit();
    }

    private static String toWrongNumberString(ArrayList<Integer> x)
    {
        int length = x.size();
        if (length <= 0)
        {
            return "";
        }
        if (length == 1)
        {
            return String.valueOf(x.get(0));
        }
        int temp = length - 1;
        String result = "";
        for (int num = 0; num < temp; num++)
        {
            result = result + x.get(num) + DIVIDE_TAG;
        }
        result = result + x.get(length - 1);
        return result;
    }

    private static String addOneWrongQuestion(String olds, int newOne)
    {
        if (newOne <= 0)
        {
            return olds;
        }
        else if (StringUtils.isNullOrEmpty(olds))
        {
            return String.valueOf(newOne);
        }
        else if(isContainNewOne(olds,newOne))
        {
            return olds;
        }
        return olds + DIVIDE_TAG + String.valueOf(newOne);
    }

    private static boolean isContainNewOne(String olds, int newOne)
    {
        ArrayList<Integer> arrayList = getWrongQuestions(olds);
        if (arrayList.contains(newOne))
        {
            return true;
        }
        return false;
    }

    private static ArrayList<Integer> getWrongQuestions(String str)
    {
        ArrayList<Integer> wrongQuestions = new ArrayList();
        if (StringUtils.isNullOrEmpty(str))
        {
            return wrongQuestions;
        }
        if (!str.contains(DIVIDE_TAG))
        {
            try
            {
                Integer value = Integer.valueOf(str);
                wrongQuestions.add(value);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return wrongQuestions;
        }
        String[] strs = str.split(DIVIDE_TAG);
        int length = strs.length;
        for (int i = 0; i < length; i++)
        {
            try
            {
                Integer value = Integer.valueOf(strs[i]);
                wrongQuestions.add(value);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return wrongQuestions;
    }
}
