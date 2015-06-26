package com.tradehero.th.fragments.live;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.widget.DocumentActionWidget;

public class LiveSignUpStep5Fragment extends LiveSignUpStepBaseFragment
{
    @InjectView(R.id.document_action_identity) DocumentActionWidget documentActionIdentity;
    @InjectView(R.id.document_action_residence) DocumentActionWidget documentActionResidence;
    @InjectView(R.id.document_action_signature) DocumentActionWidget documentActionSignature;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_sign_up_live_step_5, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }
}
