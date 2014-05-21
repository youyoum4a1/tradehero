package com.tradehero.th.fragments.alert;

import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;


public class TextHolder
{
    @InjectView(R.id.title) TextView text;

    public TextHolder(View view)
    {
        ButterKnife.inject(this, view);
    }
}