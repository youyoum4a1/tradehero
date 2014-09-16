package com.tradehero.th.fragments.chinabuild.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.OnClick;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import com.tradehero.th2.R;
import java.util.Collection;

//import com.tradehero.th.activities.MarketUtil;
//import com.tradehero.th.persistence.prefs.ShowAskForReviewDialog;
//import com.tradehero.th.persistence.timing.TimingIntervalPreference;
//TODO not finished by alex
public class CommonDialogFragment extends BaseDialogFragment
{
    //@Inject MarketUtil marketUtil;
    //@Inject @ShowAskForReviewDialog TimingIntervalPreference mShowAskForReviewDialogPreference;
    private static int mLayoutRes;
    private static Collection<String> mCollection;

    public static CommonDialogFragment showDialog(FragmentManager fragmentManager, int layoutRes)
    {
        mLayoutRes = layoutRes;
        CommonDialogFragment dialogFragment = new CommonDialogFragment();
        dialogFragment.show(fragmentManager, CommonDialogFragment.class.getName());
        return dialogFragment;
    }

    public static CommonDialogFragment showListDialog(FragmentManager fragmentManager, int layoutRes, Collection<String> collection)
    {
        mLayoutRes = layoutRes;
        mCollection = collection;
        CommonDialogFragment dialogFragment = new CommonDialogFragment();
        dialogFragment.show(fragmentManager, CommonDialogFragment.class.getName());
        return dialogFragment;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(BaseDialogFragment.STYLE_NO_TITLE, R.style.TH_Dialog);
        setCancelable(false);
        //mShowAskForReviewDialogPreference.addInFuture(TimingIntervalPreference.MINUTE);

    }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog d = super.onCreateDialog(savedInstanceState);
        //d.getWindow().setWindowAnimations(R.style.TH_BuySellDialogAnimation);
        return d;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //return inflater.inflate(mLayoutRes, container, false);
        return inflater.inflate(R.layout.common_dialog_layout, container, false);
        //return inflater.inflate(R.layout.ask_for_review_dialog_layout, container, false);
    }

    @OnClick(R.id.btn_cancel)
    public void onCancel()
    {
        dismiss();
        //mShowAskForReviewDialogPreference.justHandled();
    }

    //@OnClick(R.id.btn_later)
    public void onLater()
    {
        dismiss();
        //mShowAskForReviewDialogPreference.pushInFuture(TimingIntervalPreference.DAY);
    }

    //@OnClick(R.id.btn_rate)
    public void onRate()
    {
        //rate();
        dismiss();
        //mShowAskForReviewDialogPreference.justHandled();
    }

}
