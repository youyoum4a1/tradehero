package com.tradehero.th.fragments.social.message;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class PrivateMessageBubbleMineView extends PrivateMessageBubbleView
{
    @InjectView(android.R.id.empty) @Optional View fakePlaceHolder;

    //<editor-fold desc="Constructors">
    public PrivateMessageBubbleMineView(Context context)
    {
        super(context);
    }

    public PrivateMessageBubbleMineView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PrivateMessageBubbleMineView(Context context, AttributeSet attrs, int defStyle)
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
