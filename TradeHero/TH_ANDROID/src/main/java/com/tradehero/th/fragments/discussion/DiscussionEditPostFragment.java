package com.tradehero.th.fragments.discussion;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.form.SecurityDiscussionFormDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by tho on 4/21/2014.
 */
public class DiscussionEditPostFragment extends DashboardFragment
{
    @InjectView(R.id.discussion_post_content) EditText discussionPostContent;

    @Inject DiscussionServiceWrapper discussionServiceWrapper;
    @Inject SecurityCompactCache securityCompactCache;
    @Inject ProgressDialogUtil progressDialogUtil;

    private SecurityId securityId;
    private DiscussionDTO discussionDTO;
    private MiddleCallback<DiscussionDTO> discussionEditMiddleCallback;
    private ProgressDialog progressDialog;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_discussion_edit_post, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_discussion_edit_post, menu);

        getSherlockActivity().getSupportActionBar().setTitle(R.string.discussion);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.discussion_edit_post:
                postDiscussion();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override public void onDestroyView()
    {
        unsetDiscussionEditMiddleCallback();

        super.onDestroyView();
    }

    private void linkWith(DiscussionDTO discussionDTO, boolean andDisplay)
    {
        this.discussionDTO = discussionDTO;
    }

    private boolean validate()
    {
        boolean notEmptyText = validateNotEmptyText();
        if (!notEmptyText)
        {
            THToast.show(R.string.error_discussion_empty_post);
        }
        return notEmptyText;
    }

    private void postDiscussion()
    {
        SecurityCompactDTO securityCompactDTO = securityCompactCache.get(securityId);
        if (validate() && securityCompactDTO != null)
        {
            SecurityDiscussionFormDTO securityDiscussionFormDTO = new SecurityDiscussionFormDTO();
            securityDiscussionFormDTO.inReplyToId = securityCompactDTO.id;
            securityDiscussionFormDTO.text = discussionPostContent.getText().toString();

            unsetDiscussionEditMiddleCallback();
            progressDialog = progressDialogUtil.show(getActivity(), R.string.alert_dialog_please_wait, R.string.processing);
            discussionEditMiddleCallback = discussionServiceWrapper.createDiscussion(securityDiscussionFormDTO, new SecurityDiscussionEditCallback());
        }
    }

    private void unsetDiscussionEditMiddleCallback()
    {
        if (discussionEditMiddleCallback != null)
        {
            discussionEditMiddleCallback.setPrimaryCallback(null);
        }
        discussionEditMiddleCallback = null;
    }


    private boolean validateNotEmptyText()
    {
        // wow
        return !discussionPostContent.getText().toString().trim().isEmpty();
    }

    @Override public void onResume()
    {
        super.onResume();

        Bundle args = getArguments();
        if (args != null)
        {
            if (args.containsKey(SecurityId.BUNDLE_KEY_SECURITY_ID_BUNDLE))
            {
                Bundle securityBundle = args.getBundle(SecurityId.BUNDLE_KEY_SECURITY_ID_BUNDLE);
                SecurityId securityId = new SecurityId(securityBundle);
                linkWith(securityId, true);
            }
        }
    }

    private void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityId = securityId;

        if (andDisplay && securityId != null)
        {
            String securityName = String.format("%s:%s", securityId.securitySymbol, securityId.exchange);
            discussionPostContent.setHint(getString(R.string.discussion_new_post_hint, securityName));
        }
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }


    private class SecurityDiscussionEditCallback implements Callback<DiscussionDTO>
    {
        @Override public void success(DiscussionDTO discussionDTO, Response response)
        {
            onFinish();

            linkWith(discussionDTO, true);
            getNavigator().popFragment();
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
        }
    }
}
