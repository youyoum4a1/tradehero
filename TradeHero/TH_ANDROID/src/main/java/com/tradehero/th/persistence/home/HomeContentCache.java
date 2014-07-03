package com.tradehero.th.persistence.home;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.utils.IOUtils;
import com.tradehero.th.api.home.HomeContentDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.HomeServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import retrofit.client.Response;

@Singleton public class HomeContentCache extends StraightDTOCacheNew<UserBaseKey, HomeContentDTO>
{
    private static final int DEFAULT_MAX_CACHE = 1;
    @NotNull private HomeServiceWrapper homeServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public HomeContentCache(@NotNull HomeServiceWrapper homeServiceWrapper)
    {
        super(DEFAULT_MAX_CACHE);
        this.homeServiceWrapper = homeServiceWrapper;
    }
    //</editor-fold>

    @NotNull @Override public HomeContentDTO fetch(@NotNull UserBaseKey key) throws Throwable
    {
        Response response = homeServiceWrapper.getHomePageContent(key);
        byte[] content = IOUtils.streamToBytes(response.getBody().in());
        return new HomeContentDTO(new String(content));
    }
}
