package com.tradehero.th.fragments.alert;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.alert.AlertEventDTO;
import com.tradehero.th.utils.THSignedNumber;


public class AlertEventItemView extends LinearLayout
        implements DTOView<AlertEventDTO>
{
    @InjectView(R.id.name) TextView eventName;
    @InjectView(R.id.date) TextView eventDate;

    //region Constructors
    public AlertEventItemView(Context context)
    {
        super(context);
    }

    public AlertEventItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AlertEventItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //endregion

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
    }

    @Override public void display(AlertEventDTO alertEventDTO)
    {
        THSignedNumber thSecurityPrice = new THSignedNumber(THSignedNumber.TYPE_MONEY, alertEventDTO.securityPrice, false);
        eventName.setText(thSecurityPrice.toString());
        eventDate.setText("On " + alertEventDTO.triggeredAt);
    }
}
