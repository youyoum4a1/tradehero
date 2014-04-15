package com.tradehero.th.fragments.social.message;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by xavier2 on 2014/4/10.
 */
public class PrivateMessageBubbleOtherView extends PrivateMessageBubbleView
{
    @InjectView(android.R.id.empty) @Optional View fakePlaceHolder;

    //<editor-fold desc="Constructors">
    public PrivateMessageBubbleOtherView(Context context)
    {
        super(context);
    }

    public PrivateMessageBubbleOtherView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PrivateMessageBubbleOtherView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }
}
