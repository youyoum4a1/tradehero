package com.tradehero.th.fragments.games.popquiz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.utils.Constants;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;
import timber.log.Timber;

public class XWalkWebViewFragment extends BaseFragment
{
    private static final String BUNDLE_KEY_URL = XWalkWebViewFragment.class.getName() + ".url";

    @InjectView(R.id.xwalkWebView) XWalkView xWalkView;

    public static void putUrl(@NonNull Bundle args, @NonNull String url)
    {
        args.putString(BUNDLE_KEY_URL, url);
    }

    @Nullable public static String getUrl(@Nullable Bundle args)
    {
        if (args != null)
        {
            return args.getString(BUNDLE_KEY_URL);
        }
        return null;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, !Constants.RELEASE);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_xwalkwebview, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        xWalkView.setResourceClient(new CustomResourceClient(xWalkView));

        xWalkView.load(getLoadingUrl(), null);
    }

    @Nullable protected String getLoadingUrl()
    {
        return getUrl(getArguments());
    }

    @Override public void onPause()
    {
        super.onPause();
        if (xWalkView != null)
        {
            xWalkView.pauseTimers();
            xWalkView.onHide();
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        if (xWalkView != null)
        {
            xWalkView.resumeTimers();
            xWalkView.onShow();
        }
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        if (xWalkView != null)
        {
            xWalkView.onDestroy();
        }
    }

    private static class CustomResourceClient extends XWalkResourceClient
    {

        public CustomResourceClient(XWalkView view)
        {
            super(view);
        }

        @Override public void onLoadStarted(XWalkView view, String url)
        {
            super.onLoadStarted(view, url);
            Timber.d("Start loading: %s", url);
        }

        @Override public boolean shouldOverrideUrlLoading(XWalkView view, String url)
        {
            Timber.d("Override loading: %s", url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override public void onLoadFinished(XWalkView view, String url)
        {
            super.onLoadFinished(view, url);
            Timber.d("Finished loading: %s", url);
        }
    }
}
