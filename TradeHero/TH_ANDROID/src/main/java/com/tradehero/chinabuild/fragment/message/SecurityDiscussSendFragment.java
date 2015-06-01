package com.tradehero.chinabuild.fragment.message;

import android.os.Bundle;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import org.jetbrains.annotations.Nullable;

/**
 * 对股票发起的新讨论
 */
public class SecurityDiscussSendFragment extends DiscussSendFragment
{
    public static final String BUNDLE_KEY_SECURITY_ID = SecurityDiscussSendFragment.class.getName() + ".securityId";
    @Nullable private SecurityId securityId;

    @Nullable public static SecurityId getSecurityId(@Nullable Bundle args)
    {
        SecurityId extracted = null;
        if (args != null && args.containsKey(BUNDLE_KEY_SECURITY_ID))
        {
            extracted = new SecurityId(args.getBundle(BUNDLE_KEY_SECURITY_ID));
        }
        return extracted;
    }

    private void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityId = securityId;

        if (andDisplay && securityId != null)
        {
            String securityName = String.format("%s:%s", securityId.getExchange(), securityId.getSecuritySymbol());
            discussionPostContent.setHint(getString(R.string.discussion_new_post_hint, securityName));
        }
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
