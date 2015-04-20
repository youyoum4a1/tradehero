package com.tradehero.th.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Menu;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.fragments.games.popquiz.ForXWalkFragment;
import com.tradehero.th.persistence.prefs.AuthHeader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.inject.Inject;
import javax.inject.Provider;

public class PopQuizActivity extends OneFragmentActivity
{
    private static final String POPQUIZ_HOME_URL = "https://fb.tradehero.mobi/PopQuizWeb/Home";
    private static final String POPQUIZ_ACCESS_TOKEN_KEY = "accessToken";

    @Inject @ForXWalkFragment Provider<Class> xWalkFragmentProvider;

    @Inject @AuthHeader String thAuthHeader;

    @NonNull @Override protected Class<? extends Fragment> getInitialFragment()
    {
        return xWalkFragmentProvider.get();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.popquiz_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @NonNull @Override protected Bundle getInitialBundle()
    {
        Bundle args = super.getInitialBundle();

        if (thAuthHeader.startsWith(SocialNetworkEnum.FB.getAuthHeader()))
        {
            String[] splits = thAuthHeader.split(" ");
            Class<? extends Fragment> xwalk = xWalkFragmentProvider.get();
            if(xwalk != null)
            {
                Class[] cArg = new Class[]{Bundle.class, String.class};
                try
                {
                    Method m = xwalk.getDeclaredMethod("putUrl", cArg);
                    m.invoke(null, args, POPQUIZ_HOME_URL + "?" + POPQUIZ_ACCESS_TOKEN_KEY + "=" + splits[1]);
                } catch (NoSuchMethodException e)
                {
                    e.printStackTrace();
                } catch (InvocationTargetException e1)
                {
                    e1.printStackTrace();
                } catch (IllegalAccessException e2)
                {
                    e2.printStackTrace();
                }
            }
        }
        else
        {
            THToast.show(R.string.popquiz_require_fb);
            finish();
        }
        return args;
    }
}
