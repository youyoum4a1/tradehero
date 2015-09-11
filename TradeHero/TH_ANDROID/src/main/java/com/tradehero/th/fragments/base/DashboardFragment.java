package com.tradehero.th.fragments.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tradehero.chinabuild.fragment.LoginSuggestDialogFragment;
import com.tradehero.common.text.RichTextCreator;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class DashboardFragment extends BaseFragment {
    public static final String BUNDLE_KEY_TITLE = DashboardFragment.class.getName() + ".title";
    public static final String BUNDLE_OPEN_CLASS_NAME = DashboardFragment.class.getName() + ".oepn_class_name";
    @Inject RichTextCreator parser;

    private RelativeLayout rlCustomHeadView;
    private TextView tvHeadLeft;
    private TextView tvHeadRight0;
    private TextView tvHeadRight1;
    private TextView tvHeadMiddleMain;
    private TextView tvHeadMiddleSub;

    //Toolbar for page change
    private LinearLayout pageLL;
    private TextView pageA;
    private TextView pageB;
    public final static String INTENT_TOOLBAR_PAGE_A_CLICK = "INTENT_TOOLBAR_PAGE_A_CLICK";
    public final static String INTENT_TOOLBAR_PAGE_B_CLICK = "INTENT_TOOLBAR_PAGE_B_CLICK";

    private LoginSuggestDialogFragment dialogFragment;
    private FragmentManager fm;

    private int subMainTextColor;

    //Listen to back pressed
    private boolean needToMonitorBackPressed = false;

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
        if(tvHeadLeft!=null && tvHeadLeft.getVisibility()!=View.VISIBLE){
            tvHeadLeft.setVisibility(View.VISIBLE);
        }
        if(pageLL!=null){
            pageLL.setVisibility(View.GONE);
        }
    }

    public void setHeadViewMiddleMain(int middleMainText) {
        if (tvHeadMiddleMain != null) {
            tvHeadMiddleMain.setVisibility(View.VISIBLE);
            tvHeadMiddleMain.setText(middleMainText);
        }
    }

    public void setHeadViewMiddleSub(String middleSubText) {
        if(tvHeadMiddleSub==null){
            return;
        }
        if(middleSubText==null || middleSubText.equals("")){
            tvHeadMiddleSub.setVisibility(View.GONE);
        }else{
            tvHeadMiddleSub.setVisibility(View.VISIBLE);
            tvHeadMiddleSub.setText(middleSubText);
        }
    }

    public void setHeadViewMiddleSubTextColor(int color){
        if (tvHeadMiddleSub != null) {
            tvHeadMiddleSub.setVisibility(View.VISIBLE);
            tvHeadMiddleSub.setTextColor(color);
        }
    }

    public void setHeadViewRight0(String right0) {
        if (tvHeadRight0 != null) {
            tvHeadRight0.setVisibility(View.VISIBLE);
            tvHeadRight0.setText(right0);
        }
    }

    public void setHeadViewRight0(int drawable){
        if (tvHeadRight0 != null) {
            tvHeadRight0.setVisibility(View.VISIBLE);
            tvHeadRight0.setBackgroundResource(drawable);
        }
    }

    public void setHeadViewRight1(int drawable){
        if (tvHeadRight1 != null) {
            tvHeadRight1.setVisibility(View.VISIBLE);
            tvHeadRight1.setBackgroundResource(drawable);
        }
    }

    public void setHeadViewRight0Visibility(int visibility) {
        if (tvHeadRight0 != null) {
            tvHeadRight0.setVisibility(visibility);
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
        subMainTextColor = getResources().getColor(R.color.bar_normal);
        rlCustomHeadView = (RelativeLayout) toolbar.findViewById(R.id.rlCustomHeadView);
        tvHeadLeft = (TextView) toolbar.findViewById(R.id.tvHeadLeft);
        tvHeadRight0 = (TextView) toolbar.findViewById(R.id.tvHeadRight0);
        tvHeadRight1 = (TextView)toolbar.findViewById(R.id.tvHeadRight1);
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

        //hide sub header and right button as default
        tvHeadRight0.setVisibility(View.GONE);
        tvHeadRight0.setText("");
        tvHeadMiddleSub.setVisibility(View.GONE);
        tvHeadMiddleSub.setText("");
        tvHeadMiddleSub.setTextColor(subMainTextColor);
        tvHeadRight1.setVisibility(View.GONE);
        tvHeadRight1.setText("");
        tvHeadRight0.setBackgroundColor(Color.TRANSPARENT);
        tvHeadRight1.setBackgroundColor(Color.TRANSPARENT);
        tvHeadRight0.setCompoundDrawables(null, null, null, null);

        pageLL = (LinearLayout)toolbar.findViewById(R.id.linearlayout_pager);
        pageA = (TextView)toolbar.findViewById(R.id.page_a);
        pageB = (TextView)toolbar.findViewById(R.id.page_b);

        if(pageA!=null) {
            pageA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    focusOnPageA();
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(INTENT_TOOLBAR_PAGE_A_CLICK));
                }
            });
        }

        if(pageB!=null){
            pageB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    focusOnPageB();
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(INTENT_TOOLBAR_PAGE_B_CLICK));
                }
            });
        }
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

    public void gotoDashboard(Class Fragment, Bundle bundle) {
        bundle.putString(DashboardFragment.BUNDLE_OPEN_CLASS_NAME, Fragment.getName());
        ActivityHelper.launchDashboard(this.getActivity(), bundle);
    }

    public Fragment pushFragment(@NotNull Class fragmentClass, Bundle args) {
        return getDashboardNavigator().pushFragment(fragmentClass, args);
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
            e.printStackTrace();
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

    public boolean isNeedBackPressed() {
        return needToMonitorBackPressed;
    }

    public void setNeedToMonitorBackPressed(boolean needToMonitorBackPressed) {
        this.needToMonitorBackPressed = needToMonitorBackPressed;
    }

    public void onBackPressed() {
    }

    public void focusOnPageA(){
        if(pageA == null || pageB == null){
            return;
        }
        pageA.setTextColor(Color.parseColor("#ffffff"));
        pageA.setTextSize((float)15.50);
        pageB.setTextColor(Color.parseColor("#CDE0F3"));
        pageB.setTextSize(13);
    }


    public void focusOnPageB(){
        if(pageA == null || pageB == null){
            return;
        }
        pageA.setTextColor(Color.parseColor("#CDE0F3"));
        pageA.setTextSize(13);
        pageB.setTextColor(Color.parseColor("#ffffff"));
        pageB.setTextSize((float)15.50);
    }

    public void setPageView(String pageATitle, String pageBTitle){
        if(pageLL!=null){
            pageLL.setVisibility(View.VISIBLE);
            pageA.setText(pageATitle);
            pageB.setText(pageBTitle);

            tvHeadLeft.setVisibility(View.GONE);
            tvHeadRight0.setVisibility(View.GONE);
            tvHeadRight1.setVisibility(View.GONE);
            tvHeadMiddleMain.setVisibility(View.GONE);
            tvHeadMiddleSub.setVisibility(View.GONE);
        }
    }


}
