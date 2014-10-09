package com.tradehero.th.fragments.social.message;

import android.content.Context;
import javax.inject.Inject;

public class ReplyPrivateMessageFragment extends AbstractPrivateMessageFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @Override
    public void onResume()
    {
        super.onResume();
        //Timber.d("ReplyPrivateMessageFragment onResume ,so refresh() doing...");
        refresh();
    }
}
