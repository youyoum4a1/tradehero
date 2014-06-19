package com.tradehero.th.persistence.system;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.system.SystemStatusDTO;
import com.tradehero.th.api.users.LoginFormDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.base.THUser;
import com.tradehero.th.network.service.SessionServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class SystemStatusCache extends StraightDTOCacheNew<UserBaseKey, SystemStatusDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1;

    @NotNull private final Lazy<SessionServiceWrapper> sessionService;
    @NotNull private final Provider<LoginFormDTO> loginFormDTOProvider;

    @Inject public SystemStatusCache(
            @NotNull Lazy<SessionServiceWrapper> sessionService,
            @NotNull Provider<LoginFormDTO> loginFormDTOProvider)
    {
        super(DEFAULT_MAX_SIZE);
        this.sessionService = sessionService;
        this.loginFormDTOProvider = loginFormDTOProvider;
    }

    @Override public SystemStatusDTO fetch(@NotNull UserBaseKey key) throws Throwable
    {
        return sessionService.get().login(THUser.getAuthHeader(), loginFormDTOProvider.get()).systemStatus;
    }
}
