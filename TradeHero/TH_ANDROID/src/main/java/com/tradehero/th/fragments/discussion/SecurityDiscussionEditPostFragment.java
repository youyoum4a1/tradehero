package com.tradehero.th.fragments.discussion;

import android.os.Bundle;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SecurityDiscussionEditPostFragment extends DiscussionEditPostFragment
{
    private static final String BUNDLE_KEY_SECURITY_ID = SecurityDiscussionEditPostFragment.class.getName() + ".securityId";

    public static void putSecurityId(@NotNull Bundle args, @NotNull SecurityId securityId)
    {
        args.putBundle(BUNDLE_KEY_SECURITY_ID, securityId.getArgs());
    }

    @Nullable public static SecurityId getSecurityId(@Nullable Bundle args)
    {
        SecurityId extracted = null;
        if (args != null && args.containsKey(BUNDLE_KEY_SECURITY_ID))
        {
            extracted = new SecurityId(args.getBundle(BUNDLE_KEY_SECURITY_ID));
        }
        return extracted;
    }

    @Nullable private SecurityId securityId;

    private void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityId = securityId;

        if (andDisplay && securityId != null)
        {
            String securityName = String.format("%s:%s", securityId.getExchange(), securityId.getSecuritySymbol());
            discussionPostContent.setHint(getString(R.string.discussion_new_post_hint, securityName));
        }

        SecurityCompactDTO securityCompactDTO = securityCompactCache.get(securityId);
        if (andDisplay && securityCompactDTO != null)
        {
            getSherlockActivity().getSupportActionBar().setSubtitle(getString(R.string.discussion_edit_post_subtitle, securityCompactDTO.name));
            getSherlockActivity().invalidateOptionsMenu();
        }
    }

    @Override protected void postDiscussion()
    {
        super.postDiscussion();
    }

    @Override protected DiscussionFormDTO buildDiscussionFormDTO()
    {
        SecurityCompactDTO securityCompactDTO = null;
        if (securityId != null)
        {
            securityCompactDTO = securityCompactCache.get(securityId);
        }

        DiscussionFormDTO discussionFormDTO = super.buildDiscussionFormDTO();
        if (discussionFormDTO != null && securityCompactDTO != null)
        {
            discussionFormDTO.inReplyToId = securityCompactDTO.id;
        }
        return discussionFormDTO;
    }

    @Override protected DiscussionType getDiscussionType()
    {
        return DiscussionType.SECURITY;
    }

    @Override public void onResume()
    {
        super.onResume();

        SecurityId fromArgs = getSecurityId(getArguments());
        if (fromArgs != null)
        {
            linkWith(fromArgs, true);
        }
    }
}
