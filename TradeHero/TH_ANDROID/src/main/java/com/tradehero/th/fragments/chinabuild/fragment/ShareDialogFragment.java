package com.tradehero.th.fragments.chinabuild.fragment;

//import android.app.FragmentManager;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.InjectView;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import com.tradehero.th.fragments.chinabuild.dialog.ShareSheetDialogLayout;
import com.tradehero.th.fragments.social.WeiboSocialLinkHelper;
import com.tradehero.th.fragments.social.friend.SocialFriendHandlerWeibo;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;

//import com.tradehero.th.activities.MarketUtil;
//import com.tradehero.th.persistence.prefs.ShowAskForReviewDialog;
//import com.tradehero.th.persistence.timing.TimingIntervalPreference;

public class ShareDialogFragment extends BaseDialogFragment implements View.OnClickListener
{
    @InjectView(R.id.title) TextView mTitleText;
    @InjectView(R.id.btn_cancel) TextView mCancelBtn;
    @InjectView(R.id.btn_ok) TextView mOKBtn;
    @Inject Lazy<SocialSharer> socialSharerLazy;
    @Inject Provider<SocialFriendHandlerWeibo> weiboSocialFriendHandlerProvider;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject Provider<WeiboSocialLinkHelper> weiboSocialLinkHelperProvider;

    private static String mTitle;

    public static ShareDialogFragment showDialog(FragmentManager fragmentManager, String title)
    {
        mTitle = title;
        ShareDialogFragment dialogFragment = new ShareDialogFragment();
        dialogFragment.show(fragmentManager, ShareDialogFragment.class.getName());
        return dialogFragment;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(BaseDialogFragment.STYLE_NO_TITLE, R.style.TH_Dialog);
        setCancelable(false);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.share_dialog_layout, container, false);
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mTitleText.setText(mTitle);
        mCancelBtn.setOnClickListener(this);
        mOKBtn.setOnClickListener(this);
    }

    @Override public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_ok:
                dismiss();
                ShareSheetDialogLayout contentView = (ShareSheetDialogLayout) LayoutInflater.from(getActivity())
                        .inflate(R.layout.share_sheet_dialog_layout, null);
                THDialog.showUpDialog(getActivity(), contentView);
                break;
        }
    }

}
