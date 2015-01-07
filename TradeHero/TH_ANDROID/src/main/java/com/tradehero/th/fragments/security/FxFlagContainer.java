package com.tradehero.th.fragments.security;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.security.FxCurrency;
import com.tradehero.th.api.security.key.FxPairSecurityId;

public class FxFlagContainer extends LinearLayout
    implements DTOView<FxPairSecurityId>
{
    @InjectView(R.id.flag_left) protected ImageView flagLeft;
    @InjectView(R.id.flag_right) protected ImageView flagRight;

    private FxPairSecurityId fxPairSecurityId;

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
        ButterKnife.inject(this);
        display();
    }

    @Override public void display(FxPairSecurityId fxPairSecurityId)
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
