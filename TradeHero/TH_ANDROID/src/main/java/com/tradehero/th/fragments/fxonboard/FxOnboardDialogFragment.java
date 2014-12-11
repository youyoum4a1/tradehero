package com.tradehero.th.fragments.fxonboard;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewAnimator;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import timber.log.Timber;

public class FxOnBoardDialogFragment extends BaseDialogFragment
{
    private static final String TAG = FxOnBoardDialogFragment.class.getName();

    @InjectView(R.id.view_animator) ViewAnimator viewAnimator;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fx_onboard_dialog, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Observable.just(viewAnimator)
                .flatMapIterable(animator -> {
                    List<FxOnBoardView<View>> ooo = new ArrayList<>();
                    for (int i = 0; i < animator.getChildCount(); ++i)
                    {
                        View child = animator.getChildAt(i);
                        if (child instanceof FxOnBoardView)
                        {
                            ooo.add((FxOnBoardView<View>) child);
                        }
                    }
                    return ooo;
                })
                .flatMap(fxOnBoardView -> fxOnBoardView.<View>result())
                .subscribe(t -> viewAnimator.showNext(), throwable -> Timber.d(throwable.getMessage()))
        ;
    }

    @OnClick(R.id.close)
    public void onCloseClicked(View view)
    {
        dismiss();
        // TODO mark fx onboard handled
    }

    public static FxOnBoardDialogFragment showOnBoardDialog(FragmentManager fragmentManager)
    {
        FxOnBoardDialogFragment dialogFragment = new FxOnBoardDialogFragment();
        dialogFragment.show(fragmentManager, TAG);
        return dialogFragment;
    }
}
