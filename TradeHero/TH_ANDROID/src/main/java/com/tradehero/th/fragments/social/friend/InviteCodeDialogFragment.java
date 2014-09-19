package com.tradehero.th.fragments.social.friend;

import android.app.Dialog;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.BaseDialogFragment;

public class InviteCodeDialogFragment extends BaseDialogFragment
{
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
        ButterKnife.inject(this, view);
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @OnClick({R.id.btn_cancel, R.id.btn_cancel_submit, R.id.btn_done})
    @Override public void dismiss()
    {
        super.dismiss();
    }
}
