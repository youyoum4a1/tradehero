package com.tradehero.th.fragments.updatecenter;

import android.content.Context;
import android.util.AttributeSet;
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
    @InjectView(R.id.tab_title) TextView titleView;
    @InjectView(R.id.tab_title_number) TextView numberView;
    private String title;

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
        if (number > 0)
        {
            String titleWithNumber = title + "(" + number + ")";
            titleView.setText(titleWithNumber);
        }
        else
        {
            titleView.setText(title);
        }
    }

    //public void setTitleNumber(int number)
    //{
    //    //if (number > 100)
    //    //{
    //    //    //when message count is greater 100, just show red background
    //    //    //this is not the best way
    //    //    numberView.setText(" ");
    //    //    numberView.setVisibility(View.VISIBLE);
    //    //}
    //    if (number > 0)
    //    {
    //        numberView.setText(String.valueOf(number));
    //        numberView.setVisibility(View.VISIBLE);
    //    }
    //    else
    //    {
    //        numberView.setText("");
    //        numberView.setVisibility(View.INVISIBLE);
    //    }
    //}

    public void setTitle(String title)
    {
        titleView.setText(title);
        this.title = title;
    }
}
