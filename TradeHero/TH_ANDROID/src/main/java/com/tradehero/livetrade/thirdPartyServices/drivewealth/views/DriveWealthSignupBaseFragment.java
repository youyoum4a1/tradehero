package com.tradehero.livetrade.thirdPartyServices.drivewealth.views;

import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuInflater;

import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public abstract class DriveWealthSignupBaseFragment extends DashboardFragment {

    abstract public String getTitle();

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        setHeadViewMiddleMain(getTitle());
        setHeadViewRight0(getString(R.string.cancel));
    }

    @Override
    public void onClickHeadRight0() {
        THDialog.showCenterDialog(getActivity(), "", "您确认取消开户吗？",
                getString(R.string.cancel), getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            getActivity().finish();
                        }
                    }
                });
    }
}
