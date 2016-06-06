package com.androidth.general.fragments.discussion;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import com.androidth.general.R;
import com.androidth.general.api.discussion.DiscussionType;
import com.androidth.general.api.discussion.form.DiscussionFormDTO;
import com.androidth.general.api.discussion.form.SecurityReplyDiscussionFormDTO;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityId;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class SecurityDiscussionEditPostFragment extends DiscussionEditPostFragment
{
    private static final String BUNDLE_KEY_SECURITY_ID = SecurityDiscussionEditPostFragment.class.getName() + ".securityId";
    private static final String BUNDLE_KEY_COMMENT = SecurityDiscussionEditPostFragment.class.getName() + ".comment";

    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @Nullable private SecurityId securityId;
    @Nullable Subscription securityCompactCacheSubscription;
    @Nullable SecurityCompactDTO securityCompactDTO;

    public static void putSecurityId(@NonNull Bundle args, @NonNull SecurityId securityId)
    {
        args.putBundle(BUNDLE_KEY_SECURITY_ID, securityId.getArgs());
    }

    public static void putComment(@NonNull Bundle args, @NonNull String comment)
    {
        args.putString(BUNDLE_KEY_COMMENT, comment);
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

    @NonNull public static String getComment(@NonNull Bundle args)
    {
        return args.getString(BUNDLE_KEY_COMMENT, "");
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

    @Override public void onDestroyView()
    {
        unsubscribe(securityCompactCacheSubscription);
        super.onDestroyView();
    }

    private void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityId = securityId;

        if (andDisplay && securityId != null)
        {
            String securityName = String.format("%s:%s", securityId.getExchange(), securityId.getSecuritySymbol());
            discussionPostContent.setHint(getString(R.string.discussion_new_post_hint, securityName));
        }

        if (securityId != null)
        {
            unsubscribe(securityCompactCacheSubscription);
            securityCompactCacheSubscription = securityCompactCache.get(securityId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Pair<SecurityId, SecurityCompactDTO>>()
                    {
                        @Override public void onCompleted()
                        {
                        }

                        @Override public void onError(Throwable e)
                        {
                        }

                        @Override public void onNext(Pair<SecurityId, SecurityCompactDTO> pair)
                        {
                            securityCompactDTO = pair.second;
                            setActionBarSubtitle(getString(R.string.discussion_edit_post_subtitle, pair.second.name));
                            FragmentActivity activityCopy = getActivity();
                            if (activityCopy != null)
                            {
                                activityCopy.invalidateOptionsMenu();
                            }
                        }
                    });
        }
        if (andDisplay)
        {
        }
    }

    @Override protected DiscussionFormDTO buildDiscussionFormDTO()
    {
        SecurityReplyDiscussionFormDTO discussionFormDTO = (SecurityReplyDiscussionFormDTO) super.buildDiscussionFormDTO();
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
}
