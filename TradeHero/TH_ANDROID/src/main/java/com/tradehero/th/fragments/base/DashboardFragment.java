package com.tradehero.th.fragments.base;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tradehero.chinabuild.fragment.LoginSuggestDialogFragment;
import com.tradehero.common.text.RichTextCreator;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.utils.AlertDialogUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

abstract public class DashboardFragment extends BaseFragment {
    private static final String BUNDLE_KEY_TITLE = DashboardFragment.class.getName() + ".title";
    private static final String BUNDLE_KEY_SHOW_HOME_AS_UP = DashboardFragment.class.getName() + ".show_home_as_up";
    public static final String BUNDLE_OPEN_CLASS_NAME = DashboardFragment.class.getName() + ".oepn_class_name";
    private static final boolean DEFAULT_SHOW_HOME_AS_UP = true;

    @Inject protected AlertDialogUtil alertDialogUtil;

    private RelativeLayout rlCustomHeadView;
    private TextView tvHeadLeft;
    private TextView tvHeadRight0;
    private TextView tvHeadRight1;
    private TextView tvHeadMiddleMain;
    private TextView tvHeadMiddleSub;

    private LoginSuggestDialogFragment dialogFragment;
    private FragmentManager fm;
    @Inject RichTextCreator parser;


    //Listen to back pressed
    private boolean needToMonitorBackPressed = false;

    public boolean isNeedBackPressed() {
        return needToMonitorBackPressed;
    }

    public void setNeedToMonitorBackPressed(boolean needToMonitorBackPressed) {
        this.needToMonitorBackPressed = needToMonitorBackPressed;
    }

    public void onBackPressed() {
    }

    public void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void updateHeadView(boolean display) {
        if (rlCustomHeadView != null) {
            rlCustomHeadView.setVisibility(display ? View.VISIBLE : View.GONE);
        }
    }

    public void onClickHeadLeft() {
        if (getActivity() instanceof DashboardActivity) {
            DashboardActivity activity = ((DashboardActivity) getActivity());
            if (activity.isGuideViewShow()) {
                activity.dismissGuideView();
                return;
            }
        }
        popCurrentFragment();
    }

    public void onClickHeadRight0() {

    }

    public void onClickHeadRight1() {

    }

    public void setHeadViewLeft(String leftText) {
        if (tvHeadLeft != null) {
            tvHeadLeft.setVisibility(View.VISIBLE);
            tvHeadLeft.setText(leftText);
        }
    }

    public void setHeadViewMiddleMain(String middleMainText) {
        if (middleMainText == null || middleMainText.equals("")) {
            return;
        }
        if (tvHeadMiddleMain != null) {
            String str = middleMainText;
            if (str.length() > 15) {
                str = middleMainText.substring(0, 15);
            }
            tvHeadMiddleMain.setVisibility(View.VISIBLE);
            tvHeadMiddleMain.setText(str);
        }
    }

    public void setHeadViewMiddleMainWithFullStr(String middleMainText) {
        if (middleMainText == null || middleMainText.equals("")) {
            return;
        }
        if (tvHeadMiddleMain != null) {
            tvHeadMiddleMain.setVisibility(View.VISIBLE);
            tvHeadMiddleMain.setText(middleMainText);
        }
    }

    public void setHeadViewMiddleMain(int middleMainText) {
        if (tvHeadMiddleMain != null) {
            tvHeadMiddleMain.setVisibility(View.VISIBLE);
            tvHeadMiddleMain.setText(middleMainText);
        }
    }

    public void setHeadViewMiddleSub(String middleSubText) {
        if (tvHeadMiddleSub != null) {
            tvHeadMiddleSub.setVisibility(View.VISIBLE);
            tvHeadMiddleSub.setText(middleSubText);
        }
    }

    public void setHeadViewRight0(String right0) {
        if (tvHeadRight0 != null) {
            tvHeadRight0.setVisibility(View.VISIBLE);
            tvHeadRight0.setText(right0);
        }
    }

    public void setHeadViewRight0Visibility(int visibility) {
        if (tvHeadRight0 != null) {
            tvHeadRight0.setVisibility(visibility);
        }
    }

    public void setHeadViewRight0(int right0) {
        if (tvHeadRight0 != null) {
            tvHeadRight0.setVisibility(View.VISIBLE);
            tvHeadRight0.setText(right0);
        }
    }

    public void setHeadViewRight0Drawable(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (tvHeadRight0 != null) {
            if (right != null) {
                right.setBounds(0, 0, right.getMinimumWidth(), right.getMinimumHeight());
            }
            if (left != null) {
                left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
            }
            if (top != null) {
                top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());
            }
            if (bottom != null) {
                bottom.setBounds(0, 0, bottom.getMinimumWidth(), bottom.getMinimumHeight());
            }
            tvHeadRight0.setCompoundDrawables(left, top, right, bottom);
        }
    }

    public void setHeadViewRight1(String right1) {
        if (tvHeadRight1 != null) {
            tvHeadRight1.setVisibility(View.VISIBLE);
            tvHeadRight1.setText(right1);
        }
    }

    public static void putKeyShowHomeAsUp(@NotNull Bundle args, boolean showAsUp) {
        args.putBoolean(BUNDLE_KEY_SHOW_HOME_AS_UP, showAsUp);
    }

    protected static boolean getKeyShowHomeAsUp(@Nullable Bundle args) {
        if (args == null) {
            return DEFAULT_SHOW_HOME_AS_UP;
        }
        return args.getBoolean(BUNDLE_KEY_SHOW_HOME_AS_UP, DEFAULT_SHOW_HOME_AS_UP);
    }

    public static void putActionBarTitle(Bundle args, String title) {
        if (args != null) {
            args.putString(BUNDLE_KEY_TITLE, title);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        Bundle argument = getArguments();

        if (argument != null && argument.containsKey(BUNDLE_KEY_TITLE)) {
            String title = argument.getString(BUNDLE_KEY_TITLE);

            if (title != null && !title.isEmpty()) {
                setActionBarTitle(title);
            }
        }
        initHeadViewCustomLayout();

    }

    public void initHeadViewCustomLayout() {
        Toolbar toolbar = getToolbar();
        if (toolbar == null) {
            return;
        }

        rlCustomHeadView = (RelativeLayout) toolbar.findViewById(R.id.rlCustomHeadView);
        tvHeadLeft = (TextView) toolbar.findViewById(R.id.tvHeadLeft);
        tvHeadRight0 = (TextView) toolbar.findViewById(R.id.tvHeadRight0);
        tvHeadRight1 = (TextView) toolbar.findViewById(R.id.tvHeadRight1);
        tvHeadMiddleMain = (TextView) toolbar.findViewById(R.id.tvHeadMiddleMain);
        tvHeadMiddleSub = (TextView) toolbar.findViewById(R.id.tvHeadMiddleSub);

        tvHeadLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickHeadLeft();
            }
        });

        tvHeadRight0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickHeadRight0();
            }
        });

        tvHeadRight1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickHeadRight1();
            }
        });

    }

    public void popCurrentFragment() {
        DashboardNavigator navigator = getDashboardNavigator();
        if (navigator != null) {
            navigator.popFragment();
        }
    }

    @Nullable
    protected DashboardNavigator getDashboardNavigator() {
        @Nullable DashboardNavigatorActivity activity = ((DashboardNavigatorActivity) getActivity());
        if (activity != null) {
            return activity.getDashboardNavigator();
        }
        return null;
    }

    public <T extends Fragment> boolean allowNavigateTo(@NotNull Class<T> fragmentClass, Bundle args) {
        return true;
    }

    public void gotoDashboard(String strFragment) {
        Bundle args = new Bundle();
        args.putString(DashboardFragment.BUNDLE_OPEN_CLASS_NAME, strFragment);
        ActivityHelper.launchDashboard(this.getActivity(), args);
    }

    public void gotoDashboard(String strFragment, Bundle bundle) {
        Bundle args = new Bundle();
        args.putString(DashboardFragment.BUNDLE_OPEN_CLASS_NAME, strFragment);
        args.putAll(bundle);
        ActivityHelper.launchDashboard(this.getActivity(), args);
    }

    public void gotoDashboard(Class Fragment, Bundle bundle) {
        Bundle args = new Bundle();
        args.putString(DashboardFragment.BUNDLE_OPEN_CLASS_NAME, Fragment.getName());
        args.putAll(bundle);
        ActivityHelper.launchDashboard(this.getActivity(), args);
    }

    public Fragment pushFragment(@NotNull Class fragmentClass, Bundle args) {
        return getDashboardNavigator().pushFragment(fragmentClass, args);
    }

    public void goToFragment(@NotNull Class fragmentClass) {
        getDashboardNavigator().pushFragment(fragmentClass);
    }

    public void setRight0ButtonOnClickListener(View.OnClickListener listener) {
        tvHeadRight0.setOnClickListener(listener);
    }

    public void setLeftButtonOnClickListener(View.OnClickListener listener) {
        tvHeadLeft.setOnClickListener(listener);
    }

    public void closeInputMethod() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {

        }
    }

    protected void showSuggestLoginDialogFragment(String dialogContent) {
        if (dialogFragment == null) {
            dialogFragment = new LoginSuggestDialogFragment();
        }
        if (fm == null) {
            fm = getActivity().getSupportFragmentManager();
        }
        dialogFragment.setContent(dialogContent);
        dialogFragment.show(fm, LoginSuggestDialogFragment.class.getName());
    }

    protected String getUnParsedText(String text) {
        if (parser != null && text != null) {
            text = text.toString().replace("*", "");
            CharSequence cs = parser.load(text.toString().trim()).create();
            return cs.toString();
        }
        return text;
    }

    protected Toolbar getToolbar() {
        if ((getActivity() != null) && (getActivity() instanceof DashboardNavigatorActivity)) {
            return ((DashboardNavigatorActivity) getActivity()).getToolbar();
        }
        return null;
    }
}
