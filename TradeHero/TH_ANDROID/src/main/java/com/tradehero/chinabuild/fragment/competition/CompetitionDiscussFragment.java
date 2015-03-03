package com.tradehero.chinabuild.fragment.competition;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.tradehero.th.R;
import com.tradehero.th.utils.DaggerUtils;

/**
 * Created by palmer on 15/3/2.
 */
public class CompetitionDiscussFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.competition_discuss_layout, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override public void onAttach(Activity activity){
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }
}
