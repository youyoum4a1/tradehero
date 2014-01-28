package com.tradehero.th.fragments.alert;

import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/28/14 Time: 3:38 PM Copyright (c) TradeHero
 */

public class TextHolder
{
    @InjectView(R.id.title) TextView text;

    public TextHolder(View view)
    {
        ButterKnife.inject(this, view);
    }
}