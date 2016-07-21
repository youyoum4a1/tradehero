package com.androidth.general.fragments.social.friend;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import com.androidth.general.R;
import com.androidth.general.fragments.base.BaseDialogFragment;
import javax.inject.Inject;

public class InviteCodeDialogFragment extends BaseDialogFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    private Unbinder unbinder;
    public static InviteCodeDialogFragment showInviteCodeDialog(FragmentManager fragmentManager)
    {
        InviteCodeDialogFragment dialogFragment = new InviteCodeDialogFragment();
        dialogFragment.show(fragmentManager, InviteCodeDialogFragment.class.getName());
        return dialogFragment;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(BaseDialogFragment.STYLE_NO_TITLE, getTheme());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.invite_code_dialog_layout, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override public void onDestroyView()
    {
        unbinder.unbind();
        super.onDestroyView();
    }

    @SuppressWarnings("EmptyMethod")
    @OnClick({R.id.btn_cancel, R.id.btn_cancel_submit, R.id.btn_done})
    @Override public void dismiss()
    {
        super.dismiss();
    }
}
