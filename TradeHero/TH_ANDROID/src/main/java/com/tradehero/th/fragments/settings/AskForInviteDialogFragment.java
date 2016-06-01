package com.ayondo.academy.fragments.settings;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.ayondo.academy.R;
import com.ayondo.academy.fragments.DashboardNavigator;
import com.ayondo.academy.fragments.base.BaseDialogFragment;
import com.ayondo.academy.fragments.social.friend.FriendsInvitationFragment;
import com.ayondo.academy.persistence.prefs.ShowAskForInviteDialog;
import com.ayondo.academy.persistence.prefs.ShowAskForInviteDialogCloseTimes;
import com.ayondo.academy.persistence.timing.TimingIntervalPreference;
import dagger.Lazy;
import javax.inject.Inject;

public class AskForInviteDialogFragment extends BaseDialogFragment
{
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject @ShowAskForInviteDialog TimingIntervalPreference mShowAskForInviteDialogPreference;
    @Inject @ShowAskForInviteDialogCloseTimes IntPreference mShowAskForInviteDialogCloseTimesPreference;

    public static AskForInviteDialogFragment showInviteDialog(FragmentManager fragmentManager)
    {
        AskForInviteDialogFragment dialogFragment = new AskForInviteDialogFragment();
        dialogFragment.show(fragmentManager, AskForInviteDialogFragment.class.getName());
        return dialogFragment;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mShowAskForInviteDialogPreference.addInFuture(TimingIntervalPreference.MINUTE);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.ask_for_invite_dialog_layout, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.btn_cancel)
    public void onCancel()
    {
        dismiss();
        mShowAskForInviteDialogPreference.pushInFuture(TimingIntervalPreference.WEEK * mShowAskForInviteDialogCloseTimesPreference.get());
        mShowAskForInviteDialogCloseTimesPreference.set(mShowAskForInviteDialogCloseTimesPreference.get()+1);

    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.btn_invite)
    public void onInvite()
    {
        pushInvitationFragment();
        dismiss();
        mShowAskForInviteDialogPreference.pushInFuture(TimingIntervalPreference.YEAR);
    }

    private void pushInvitationFragment()
    {
        navigator.get().pushFragment(FriendsInvitationFragment.class);
    }

}
