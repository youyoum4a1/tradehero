package com.tradehero.th.fragments.settings;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tradehero.common.persistence.prefs.LongPreference;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import com.tradehero.th.persistence.prefs.ShowAskForReviewDialog;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.Constants;
import javax.inject.Inject;
import timber.log.Timber;

public class AskForReviewDialogFragment extends BaseDialogFragment
{
    @Inject AlertDialogUtil alertDialogUtil;
    @Inject @ShowAskForReviewDialog LongPreference mShowAskForReviewDialogPreference;

    public static AskForReviewDialogFragment showInviteCodeDialog(FragmentManager fragmentManager)
    {
        AskForReviewDialogFragment dialogFragment = new AskForReviewDialogFragment();
        dialogFragment.show(fragmentManager, AskForReviewDialogFragment.class.getName());
        return dialogFragment;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(BaseDialogFragment.STYLE_NO_TITLE, getTheme());
    }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog d = super.onCreateDialog(savedInstanceState);
        d.getWindow().setWindowAnimations(R.style.TH_BuySellDialogAnimation);
        return d;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.ask_for_review_dialog_layout, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @OnClick(R.id.btn_cancel)
    @Override public void dismiss()
    {
        super.dismiss();
        mShowAskForReviewDialogPreference.set(
                System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000);
    }

    @OnClick(R.id.btn_later)
    public void onLater()
    {
        dismiss();
        //mShowAskForReviewDialogPreference.set(System.currentTimeMillis()+60*1000);//1 min for test
        mShowAskForReviewDialogPreference.set(System.currentTimeMillis()+30*24*60*60*1000);//1 month
    }

    @OnClick(R.id.btn_rate)
    public void onRate()
    {
        rate();
        dismiss();
    }

    private void rate()
    {
        String appName = Constants.PLAYSTORE_APP_ID;
        try
        {
            startActivity(
                    new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
        }
        catch (android.content.ActivityNotFoundException anfe)
        {
            try
            {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
            }
            catch (Exception e)
            {
                Timber.e(e, "Cannot send to Google Play store");
                alertDialogUtil.popWithNegativeButton(
                        getSherlockActivity(),
                        R.string.webview_error_no_browser_for_intent_title,
                        R.string.webview_error_no_browser_for_intent_description,
                        R.string.cancel);
            }
        }
        mShowAskForReviewDialogPreference.set(
                System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000);
    }
}
