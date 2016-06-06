package com.androidth.general.fragments.security;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.androidth.general.R;
import com.androidth.general.api.DTOView;
import com.androidth.general.api.security.FxCurrency;
import com.androidth.general.api.security.key.FxPairSecurityId;

public class FxFlagContainer extends LinearLayout
    implements DTOView<FxPairSecurityId>
{
    @Bind(R.id.flag_left) protected ImageView flagLeft;
    @Bind(R.id.flag_right) protected ImageView flagRight;

    @Nullable private FxPairSecurityId fxPairSecurityId;

    //<editor-fold desc="Constructors">
    public FxFlagContainer(Context context)
    {
        super(context);
    }

    public FxFlagContainer(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FxFlagContainer(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
        display();
    }

    @Override public void display(@Nullable FxPairSecurityId fxPairSecurityId)
    {
        this.fxPairSecurityId = fxPairSecurityId;
        display();
    }

    public void display()
    {
        if (fxPairSecurityId != null)
        {
            if (flagLeft != null)
            {
                flagLeft.setImageResource(FxCurrency.create(fxPairSecurityId.left).flag);
            }
            if (flagRight != null)
            {
                flagRight.setImageResource(FxCurrency.create(fxPairSecurityId.right).flag);
            }
        }
    }
}
