package com.tradehero.th.fragments.games;

import android.os.Bundle;
import com.tradehero.th.fragments.web.BaseWebViewFragment;

public class ViralGameWebFragment extends BaseWebViewFragment
{
    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }
}
