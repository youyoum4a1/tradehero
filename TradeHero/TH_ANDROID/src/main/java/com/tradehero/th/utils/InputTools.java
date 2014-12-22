package com.tradehero.th.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by huhaiping on 14-10-30.
 */

public class InputTools
{

    //强制显示或者关闭系统键盘
    public static void KeyBoard(final EditText txtSearchKey, final String status)
    {

        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    InputMethodManager m = (InputMethodManager)
                            txtSearchKey.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (status.equals("open"))
                    {
                        m.showSoftInput(txtSearchKey, InputMethodManager.SHOW_FORCED);
                    }
                    else
                    {
                        m.hideSoftInputFromWindow(txtSearchKey.getWindowToken(), 0);
                    }
                } catch (Exception e)
                {

                }
            }
        }, 300);
    }

    public static void dismissKeyBoard(Activity activity){
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputManger = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
