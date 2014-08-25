package com.tradehero.th.fragments.onboarding;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import org.jetbrains.annotations.NotNull;

public class OnBoardDialogFragment extends BaseDialogFragment
{
    @InjectView(R.id.done_button) View doneButton;
    @InjectView(R.id.close) View closeButton;
    @NotNull OnBoardPagingHolder onBoardPagingHolder;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(BaseDialogFragment.STYLE_NO_TITLE, getTheme());
        onBoardPagingHolder = new OnBoardPagingHolder(((Fragment) this).getChildFragmentManager());
    }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog d = super.onCreateDialog(savedInstanceState);
        d.getWindow().setWindowAnimations(R.style.TH_BuySellDialogAnimation);
        return d;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.onboard_dialog, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        onBoardPagingHolder.attachView(view);
    }

    @Override public void onDestroyView()
    {
        onBoardPagingHolder.detachView();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @OnClick({
            R.id.done_button,
            R.id.close,
    })
    public void onDoneClicked(/*View view*/)
    {
        dismiss();
    }
}
