package com.tradehero.th.fragments.updatecenter.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;

/**
 * Created by tradehero on 14-4-3.
 */
public class TitleTabView extends RelativeLayout
{
    @InjectView(R.id.tab_tilte)TextView titleView;
    @InjectView(R.id.tab_tilte_number)TextView numberView;

    public TitleTabView(Context context)
    {
        super(context);
    }

    public TitleTabView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TitleTabView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    private void init()
    {
        ButterKnife.inject(this);

    }

    public void setTitleNumber(int number)
    {
        if (number > 1)
        {
            numberView.setText(String.valueOf(number));
            numberView.setVisibility(View.VISIBLE);
        }
        else
        {
            numberView.setText("");
            numberView.setVisibility(View.INVISIBLE);
        }
    }

    public void setTitle(String title)
    {
        numberView.setText(title);
    }
}
