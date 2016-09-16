package com.androidth.general.fragments.kyc;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
 * Created by ayushnvijay on 8/4/16.
 */
public class CustomSpinnerSelection extends Spinner {

    private boolean mToggleFlag = true;

    public CustomSpinnerSelection(Context context, AttributeSet attrs,
                                  int defStyle, int mode) {
        super(context, attrs, defStyle, mode);
    }

    public CustomSpinnerSelection(Context context, AttributeSet attrs,
                                  int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomSpinnerSelection(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSpinnerSelection(Context context, int mode) {
        super(context, mode);
    }

    public CustomSpinnerSelection(Context context) {
        super(context);
    }

    @Override
    public int getSelectedItemPosition() {
        // this toggle is required because this method will get called in other
        // places too, the most important being called for the
        // OnItemSelectedListener
        if (!mToggleFlag) {
            return 0; // get us to the first element
        }
        return super.getSelectedItemPosition();
    }

    @Override
    public boolean performClick() {
        // this method shows the list of elements from which to select one.
        // we have to make the getSelectedItemPosition to return 0 so you can
        // fool the Spinner and let it think that the selected item is the first
        // element
        mToggleFlag = false;
        boolean result = super.performClick();
        mToggleFlag = true;
        return result;
    }

}