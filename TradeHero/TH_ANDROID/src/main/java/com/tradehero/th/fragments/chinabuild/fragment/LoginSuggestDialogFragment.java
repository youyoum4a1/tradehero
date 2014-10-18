package com.tradehero.th.fragments.chinabuild.fragment;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.activities.AuthenticationActivity;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.authentication.EmailSignInFragment;
import com.tradehero.th.fragments.authentication.EmailSignUpFragment;
import com.tradehero.th.fragments.authentication.SignInFragment;
import com.tradehero.th.fragments.authentication.SignUpFragment;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.user.UserProfileCache;

import javax.inject.Inject;

/**
 * Created by palmer on 14-10-15.
 */
public class LoginSuggestDialogFragment extends BaseDialogFragment {

    @InjectView(R.id.textview_suggest_cancel)
    TextView mCancelBtn;
    @InjectView(R.id.textview_suggest_signin)
    TextView mOKBtn;
    @InjectView(R.id.textview_suggest_content)
    TextView mContent;

    private String content = "";

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
        dismiss();
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setContent(int content_id){
        String content = getActivity().getResources().getString(content_id);
    }


    @OnClick(R.id.textview_suggest_signin)
    public void gotoSignIn() {
        Intent gotoAuthticationIntent = new Intent(getActivity(), AuthenticationActivity.class);
        getActivity().startActivity(gotoAuthticationIntent);
        getActivity().finish();
    }

}
