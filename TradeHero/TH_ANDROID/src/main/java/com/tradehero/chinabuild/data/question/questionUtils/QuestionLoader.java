package com.tradehero.chinabuild.data.question.questionUtils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class QuestionLoader
{

    private static QuestionLoader instance;
    private static Context mContext;

    private final String LEVEL_ONE = "ONE";
    private final String LEVEL_TWO = "TWO";
    private final String LEVEL_THREE = "THREE";

    public static synchronized QuestionLoader getInstance(Context context)
    {
        mContext = context;
        if (instance == null)
        {
            instance = new QuestionLoader();
        }
        return instance;
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
            return "question_1.json";
        }

        else if (level.equals(LEVEL_TWO))
        {
            return "question_2.json";
        }

        else if (level.equals(LEVEL_THREE))
        {
            return "question_3.json";
        }

        return null;
    }
}
