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
import org.xwalk.core.XWalkView;

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

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_xwalkwebview, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        xWalkView.load(getLoadingUrl(), null);
    }

    @Nullable protected String getLoadingUrl()
    {
        return getUrl(getArguments());
    }


    @Override public void onPause() {
        super.onPause();
        if (xWalkView != null) {
            xWalkView.pauseTimers();
            xWalkView.onHide();
        }
    }

    @Override public void onResume() {
        super.onResume();
        if (xWalkView != null) {
            xWalkView.resumeTimers();
            xWalkView.onShow();
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();
        if (xWalkView != null) {
            xWalkView.onDestroy();
        }
    }
}
