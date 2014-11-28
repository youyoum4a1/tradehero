package com.tradehero.chinabuild.fragment.message;

/**
 * 普通评论发表，显示在最新动态里。
 */
public class DiscoveryDiscussSendFragment extends DiscussSendFragment
{

    @Override protected void postDiscussion()
    {
        postDiscoveryDiscusstion();
    }

    @Override public void onResume()
    {
        super.onResume();
    }
}
