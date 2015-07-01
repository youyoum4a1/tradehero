package com.tradehero.th.fragments.live.ayondo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.tradehero.common.utils.SDKUtils;
import com.tradehero.th.R;
import com.tradehero.th.fragments.live.LiveSignUpStepBaseFragment;
import com.tradehero.th.models.kyc.KYCForm;
import com.tradehero.th.utils.GraphicUtil;

public class LiveSignUpStep1AyondoFragment extends LiveSignUpStepBaseFragment
{
    @Bind(R.id.info_title) Spinner title;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_sign_up_live_ayondo_step_1, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        ArrayAdapter stringArrayAdapter =
                new ArrayAdapter<String>(getActivity(),
                        R.layout.sign_up_dropdown_item_selected,
                        getResources().getStringArray(R.array.live_title_array))
                {
                    @Override public View getView(int position, View convertView, ViewGroup parent)
                    {
                        View v = super.getView(position, convertView, parent);
                        if (!SDKUtils.isLollipopOrHigher())
                        {
                            if (v instanceof TextView)
                            {
                                ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(null, null,
                                        GraphicUtil.createStateListDrawableRes(getActivity(), R.drawable.abc_spinner_mtrl_am_alpha), null);
                            }
                        }
                        return v;
                    }
                };
        stringArrayAdapter.setDropDownViewResource(R.layout.sign_up_dropdown_item);
        title.setAdapter(stringArrayAdapter);
    }

    @Override public void onNext(@NonNull KYCForm kycForm)
    {
        // TODO
    }
}
