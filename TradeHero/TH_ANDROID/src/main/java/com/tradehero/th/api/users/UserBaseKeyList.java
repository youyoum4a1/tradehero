package com.tradehero.th.api.users;

import com.tradehero.common.persistence.DTOKeyIdList;
import java.util.Collection;


public class UserBaseKeyList extends DTOKeyIdList<UserBaseKey>
{
    public static final String TAG = UserBaseKeyList.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public UserBaseKeyList()
    {
        super();
    }

    public UserBaseKeyList(int capacity)
    {
        super(capacity);
    }

    public UserBaseKeyList(Collection<? extends UserBaseKey> collection)
    {
        super(collection);
    }
    //</editor-fold>
}
