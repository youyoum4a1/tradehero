package com.tradehero.th.fragments.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;

/**
 * Created by tho on 4/24/2014.
 */
public class StaffTitleView extends LinearLayout
{
    @InjectView(R.id.staff_name) TextView staffNameTextView;
    @InjectView(R.id.staff_title) TextView staffTitleTextView;

    //<editor-fold desc="Constructors">
    public StaffTitleView(Context context)
    {
        super(context);
    }

    public StaffTitleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public StaffTitleView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
    }

    public void setStaffName(String staffName)
    {
        staffNameTextView.setText(staffName);
    }

    public void setStaffTitle(String staffTitle)
    {
        staffTitleTextView.setText(staffTitle);
    }
}
