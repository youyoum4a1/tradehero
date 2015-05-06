package com.tradehero.chinabuild.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.activities.AuthenticationActivity;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;

import javax.inject.Inject;

/**
 * Created by palmer on 14-10-15.
 */
public class LoginSuggestDialogFragment extends BaseDialogFragment {

    @InjectView(R.id.textview_suggest_cancel) TextView mCancelBtn;
    @InjectView(R.id.textview_suggest_signin) TextView mOKBtn;
    @InjectView(R.id.textview_suggest_content) TextView mContent;

    private String content = "";

    @Inject Analytics analytics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BaseDialogFragment.STYLE_NO_TITLE, R.style.TH_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.suggest_login_dialog_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mContent != null && !TextUtils.isEmpty(content)) {
            mContent.setText(content);
        }
    }

    @OnClick(R.id.textview_suggest_cancel)
    public void gotoCancel() {
        analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.ANONYMOUS_TO_REAL_ACCOUNT_CANCEL));
        dismiss();
    }

    public void setContent(String content) {
        this.content = content;
    }

    @OnClick(R.id.textview_suggest_signin)
    public void gotoSignIn() {
        analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.ANONYMOUS_TO_REAL_ACCOUNT_CONFIRM));
        Intent gotoAuthenticationIntent = new Intent(getActivity(), AuthenticationActivity.class);
        getActivity().startActivity(gotoAuthenticationIntent);
        getActivity().finish();
    }

}
