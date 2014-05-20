package com.tradehero.th.api.discussion.key;

import android.os.Bundle;
import com.tradehero.th.api.users.UserBaseKey;

public class MessageHeaderUserId extends MessageHeaderId
{
    private static final String BUNDLE_KEY_USER = MessageHeaderUserId.class.getName() + ".userBaseKey";

    public final UserBaseKey userBaseKey;

    public static void putUserBaseKey(Bundle args, UserBaseKey userBaseKey)
    {
        args.putBundle(BUNDLE_KEY_USER, userBaseKey.getArgs());
    }

    public static UserBaseKey getUserBaseKey(Bundle args)
    {
        if (args != null && args.containsKey(BUNDLE_KEY_USER))
        {
            return new UserBaseKey(args.getBundle(BUNDLE_KEY_USER));
        }
        return null;
    }

    //<editor-fold desc="Constructors">
    public MessageHeaderUserId(int commentId, UserBaseKey userBaseKey)
    {
        super(commentId);
        this.userBaseKey = userBaseKey;
    }

    public MessageHeaderUserId(Bundle args)
    {
        super(args);
        this.userBaseKey = getUserBaseKey(args);
    }
    //</editor-fold>

    @Override protected void populate(Bundle args)
    {
        super.populate(args);
        putUserBaseKey(args, userBaseKey);
    }

    @Override public int hashCode()
    {
        return super.hashCode() ^
                (userBaseKey == null ? 0 : userBaseKey.hashCode());
    }

    @Override protected boolean equalFields(MessageHeaderId other)
    {
        return equalFields((MessageHeaderUserId) other);
    }

    protected boolean equalFields(MessageHeaderUserId other)
    {
        return super.equalFields(other) &&
                userBaseKey == null ? other.userBaseKey == null : userBaseKey.equals(other.userBaseKey);
    }
}
