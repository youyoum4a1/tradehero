package com.tradehero.hengsheng;

import android.os.AsyncTask;

/**
 * Created by palmer on 15/8/11.
 */
public class QueryBankAccountTask extends AsyncTask<Integer, Integer, String> {

    private HengShengCallBack callBack;

    public QueryBankAccountTask(HengShengCallBack callBack){
        this.callBack = callBack;
    }


    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(Integer... integers) {
        return HengShengTrade.queryBankAccount();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
    }

    @Override
    protected void onPostExecute(String result) {
        callBack.onCompleted(result);
    }
}
