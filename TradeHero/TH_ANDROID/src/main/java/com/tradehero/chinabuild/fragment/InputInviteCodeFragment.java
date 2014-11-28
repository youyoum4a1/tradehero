package com.tradehero.chinabuild.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UpdateReferralCodeDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.utils.ProgressDialogUtil;
import javax.inject.Inject;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class InputInviteCodeFragment extends DashboardFragment implements View.OnClickListener
{
    @InjectView(R.id.invite_code) EditText mInviteCode;
    @Inject CurrentUserId currentUserId;
    @Inject UserServiceWrapper userServiceWrapper;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Nullable private MiddleCallback<Response> middleCallbackUpdateInviteCode;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(R.string.invite_code);
        setHeadViewRight0(R.string.submit);
    }

    @Override public void onClickHeadRight0()
    {
        progressDialogUtil.show(getActivity(), R.string.processing, R.string.alert_dialog_please_wait);
        detachMiddleCallbackUpdateInvite();
        UpdateReferralCodeDTO formDTO = new UpdateReferralCodeDTO(mInviteCode.getText().toString());
        middleCallbackUpdateInviteCode = userServiceWrapper.updateReferralCode(
                currentUserId.toUserBaseKey(), formDTO, new InviteCodeUpdateInviteCallback());
    }

    private void detachMiddleCallbackUpdateInvite()
    {
        if (middleCallbackUpdateInviteCode != null)
        {
            middleCallbackUpdateInviteCode.setPrimaryCallback(null);
        }
        middleCallbackUpdateInviteCode = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.input_invite_code_fragment_layout, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override public void onResume()
    {
        super.onResume();

    }

    @Override public void onClick(View view)
    {
        switch (view.getId())
        {
        }
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }
    protected class InviteCodeUpdateInviteCallback implements Callback<Response>
    {
        @Override public void success(Response response, Response response2)
        {
            progressDialogUtil.dismiss(getActivity());
            THToast.show(R.string.invite_code_submit_success);
            onClickHeadLeft();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            progressDialogUtil.dismiss(getActivity());
            if ((new THException(retrofitError)).getMessage().contains("Already invited"))
            {
                THToast.show(R.string.invite_friend_success);
            }
            else
            {
                THToast.show(R.string.invite_friend_fail);
            }
        }
    }
}
