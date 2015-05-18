package com.tradehero.chinabuild.fragment.message;

import com.tradehero.chinabuild.data.DiscoveryDiscussFormDTO;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.utils.DeviceUtil;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 普通评论发表，显示在最新动态里。
 */
public class DiscoveryDiscussSendFragment extends DiscussSendFragment
{

    @Override protected void postDiscussion()
    {
        postDiscoveryDiscusstion();
    }

    //发布普通的自己的TIMELINE流入最新动态
    protected void postDiscoveryDiscusstion()
    {
        if (validate())
        {
            isSending = true;
            DiscoveryDiscussFormDTO discussionFormDTO = new DiscoveryDiscussFormDTO();
            if (discussionFormDTO == null) return;
            discussionFormDTO.text = unSpanText(discussionPostContent.getText()).toString();
            unsetDiscoveryEditMiddleCallback();
            progressDialog = progressDialogUtil.show(getActivity(), R.string.alert_dialog_please_wait, R.string.processing);
            dicoveryEditMiddleCallback = discussionServiceWrapper.createDiscoveryDiscussion(currentUserId.toUserBaseKey().key, discussionFormDTO,
                    new DiscoveryDiscussionEditCallback());
        }
    }

    private class DiscoveryDiscussionEditCallback implements Callback<TimelineItemDTO>
    {
        @Override public void success(TimelineItemDTO discussionDTO, Response response)
        {
            onFinish();
            DeviceUtil.dismissKeyboard(getActivity());
            getDashboardNavigator().popFragment();
        }

        @Override public void failure(RetrofitError error)
        {
            onFinish();
            THToast.show(new THException(error));
        }

        private void onFinish()
        {
            if (progressDialog != null)
            {
                progressDialog.hide();
            }

            isSending = false;
        }
    }
}
