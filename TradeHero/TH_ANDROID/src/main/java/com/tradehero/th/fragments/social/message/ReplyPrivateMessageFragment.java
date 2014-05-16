package com.tradehero.th.fragments.social.message;

public class ReplyPrivateMessageFragment extends AbstractPrivateMessageFragment
{
    @Override
    public void onResume() {
        super.onResume();
        //Timber.d("ReplyPrivateMessageFragment onResume ,so refresh() doing...");
        refresh();
    }
}
