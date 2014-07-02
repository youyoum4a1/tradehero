package com.tradehero.th.persistence.home;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.utils.IOUtils;
import com.tradehero.th.api.home.HomeContentDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.HomeService;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import retrofit.client.Response;

@Singleton public class HomeContentCache extends StraightDTOCacheNew<UserBaseKey, HomeContentDTO>
{
    private static final int DEFAULT_MAX_CACHE = 1;
    private HomeService homeService;

    @Inject public HomeContentCache(HomeService homeService)
    {
        super(DEFAULT_MAX_CACHE);
        this.homeService = homeService;
    }

    @NotNull @Override public HomeContentDTO fetch(@NotNull UserBaseKey key) throws Throwable
    {
        Response response = homeService.getHomePageContent(key.getUserId());
        if (response.getStatus() == 200 && response.getBody() != null)
        {
            byte[] content = IOUtils.streamToBytes(response.getBody().in());
            HomeContentDTO homeContentDTO = new HomeContentDTO();
            homeContentDTO.content = new String(content);
            return homeContentDTO;
        }
        return get(key);
    }
}
