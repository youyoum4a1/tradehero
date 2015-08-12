package com.tradehero.hengsheng;

import android.os.AsyncTask;

/**
 * Created by palmer on 15/8/11.
 */
public class GetAccessTokenTask extends AsyncTask<Integer, Integer, String>  {

    private HengShengCallBack callBack;
    private String account_content;
    private String password;

    public GetAccessTokenTask(String account_content, String password, HengShengCallBack callBack){
        this.callBack = callBack;
        this.account_content = account_content;
        this.password = password;
    }


    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(Integer... integers) {
        return HengShengTrade.getAccessToken(account_content, password);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
    }

    @Override
    protected void onPostExecute(String result) {
        callBack.onCompleted(result);
    }
}
