package com.tradehero.chinabuild.data.question.questionUtils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.util.Log;
import com.tradehero.chinabuild.data.sp.QuestionsSharePreferenceManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class QuestionLoader
{

    private static QuestionLoader instance;
    private static Context mContext;

    public static final String LEVEL_ONE = "ONE";
    public static final String LEVEL_TWO = "TWO";
    public static final String LEVEL_THREE = "THREE";

    public static final String LEVEL_ONE_FAILS = "ONE_FAILS";//LEVEL ONE 的错题集
    public static final String LEVEL_TWO_FAILS = "TWO_FAILS";//
    public static final String LEVEL_THREE_FAILS = "THREE_FAILS";

    public static final int TOTAL_NUM_QA = 125;
    public static final int TOTAL_NUM_QB = 23;
    public static final int TOTAL_NUM_QC = 9;

    public static synchronized QuestionLoader getInstance(Context context)
    {
        mContext = context;
        if (instance == null)
        {
            instance = new QuestionLoader();
        }
        return instance;
    }

    public static String getLevelName(int index)
    {
        if (index == 0)
        {
            return LEVEL_ONE;
        }
        else if (index == 1)
        {
            return LEVEL_TWO;
        }
        else if (index == 2) return LEVEL_THREE;
        return null;
    }

    public static int getLevelMaxNumber(int index)
    {
        if (index == 0)
        {
            return TOTAL_NUM_QA;
        }
        else if (index == 1)
        {
            return TOTAL_NUM_QB;
        }
        else if (index == 2) return TOTAL_NUM_QC;
        return 0;
    }

    public static int getLevelMaxNumber(String level)
    {
        if (level.equals(LEVEL_ONE))
        {
            return TOTAL_NUM_QA;
        }
        else if (level.equals(LEVEL_TWO))
        {
            return TOTAL_NUM_QB;
        }
        else if (level.equals(LEVEL_THREE)) return TOTAL_NUM_QC;
        return 0;
    }

    public static String getLevelFailName(String level)
    {
        if (level.equals(LEVEL_ONE))
        {
            return LEVEL_ONE_FAILS;
        }
        else if (level.equals(LEVEL_TWO))
        {
            return LEVEL_TWO_FAILS;
        }
        else if (level.equals(LEVEL_THREE)) return LEVEL_THREE_FAILS;
        return null;
    }

    public static String getNameOfFailQuestion(String level)
    {
        if (level.equals(LEVEL_ONE_FAILS))
        {
            return LEVEL_ONE;
        }
        else if (level.equals(LEVEL_TWO_FAILS))
        {
            return LEVEL_TWO;
        }
        else if (level.equals(LEVEL_THREE_FAILS))
        {
            return LEVEL_THREE;
        }
        return null;
    }

    //
    public static String getCurrentSharePrefLevelName(String level)
    {
        if (level.equals(LEVEL_ONE_FAILS) || level.equals(LEVEL_ONE))
        {
            return LEVEL_ONE;
        }
        else if (level.equals(LEVEL_TWO_FAILS) || level.equals(LEVEL_TWO))
        {
            return LEVEL_TWO;
        }
        else if (level.equals(LEVEL_THREE_FAILS) || level.equals(LEVEL_THREE))
        {
            return LEVEL_THREE;
        }
        return null;
    }

    public ArrayList<Question> getQuestionLevelOne()
    {
        return getQuestionList(LEVEL_ONE);
    }

    public ArrayList<Question> getQuestionLevelTwo()
    {
        return getQuestionList(LEVEL_TWO);
    }

    public ArrayList<Question> getQuestionLevelThree()
    {
        return getQuestionList(LEVEL_THREE);
    }

    public ArrayList<Question> getQuestionFailsList(String level)
    {
        //得到level对应的原题
        ArrayList<Question> array = getQuestionList(getNameOfFailQuestion(level));
        //
        ArrayList<Integer> arrayList = QuestionsSharePreferenceManager.getWrongQuestions(mContext, getNameOfFailQuestion(level));

        ArrayList<Question> arrayListResult = new ArrayList();
        if (arrayList != null)
        {
            for (int i = 0; i < arrayList.size(); i++)
            {
                arrayListResult.add(array.get(arrayList.get(i) - 1));
            }
        }
        return arrayListResult;
    }

    public ArrayList<Question> getQuestionList(String level)
    {
        String jsonStr = getAssetsFile(level);
        ArrayList array = new ArrayList();
        try
        {
            JSONObject dataJson = new JSONObject(jsonStr);
            JSONArray data = dataJson.getJSONArray("data");

            for (int i = 0; i < data.length(); i++)
            {
                JSONObject info = data.getJSONObject(i);
                String qid = info.getString("id");
                String qtitle = info.getString("题目");
                String qa1 = info.getString("答案一");
                String qa2 = info.getString("答案二");
                String qa3 = info.getString("答案三");
                String qa4 = info.getString("答案四");
                String qac = info.getString("正确答案");
                array.add(new Question(qtitle, qa1, qa2, qa3, qa4, qac, "", qid));
            }
        }
        catch (Exception e)
        {
            Log.e("", e.toString());
        }
        return array;
    }

    private String getAssetsFile(String level)
    {
        Context test_Context = null;
        String test_package = mContext.getPackageName();
        try
        {
            test_Context = mContext.createPackageContext(
                    test_package, Context.CONTEXT_IGNORE_SECURITY);
            AssetManager s = test_Context.getAssets();
            try
            {
                InputStream is = s.open(getJsonFileName(level));
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                String json = new String(buffer, "utf-8");
                is.close();
                return json;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        catch (NameNotFoundException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private String getJsonFileName(String level)
    {
        if (level.equals(LEVEL_ONE))
        {
            return "question_0.json";
        }

        else if (level.equals(LEVEL_TWO))
        {
            return "question_1.json";
        }

        else if (level.equals(LEVEL_THREE))
        {
            return "question_2.json";
        }

        return null;
    }
}
