package com.tradehero.th.fragments.chinabuild.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.user.UserProfileCache;

import javax.inject.Inject;

/**
 * Created by palmer on 14-10-15.
 */
public class LoginSuggestDialogFragment extends BaseDialogFragment{

    @InjectView(R.id.textview_suggest_cancel) TextView mCancelBtn;
    @InjectView(R.id.textview_suggest_signin) TextView mOKBtn;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(BaseDialogFragment.STYLE_NO_TITLE, R.style.TH_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.suggest_login_dialog_layout, container, false);
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.textview_suggest_cancel)
    public void gotoCancel(){
        dismiss();
    }

    @OnClick(R.id.textview_suggest_signin)
    public void gotoSignIn(){
        Bundle args = new Bundle();
        args.putString(DashboardFragment.BUNDLE_OPEN_CLASS_NAME,
                BindGuestUserFragment.class.getName());
        ActivityHelper.launchDashboard(getActivity(), args);
        dismiss();
    }

}
