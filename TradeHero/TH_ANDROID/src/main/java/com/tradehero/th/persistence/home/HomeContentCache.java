package com.tradehero.th.persistence.home;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.utils.IOUtils;
import com.tradehero.common.utils.RetrofitHelper;
import com.tradehero.th.api.home.HomeContentDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.HomeServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.Constants;
import dagger.Lazy;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import retrofit.client.Header;
import retrofit.client.Response;

@Singleton public class HomeContentCache extends StraightDTOCacheNew<UserBaseKey, HomeContentDTO>
{
    private static final int DEFAULT_MAX_CACHE = 1;
    @NotNull private final HomeServiceWrapper homeServiceWrapper;
    @NotNull private final Lazy<UserProfileCache> userProfileCacheLazy;
    @NotNull private final RetrofitHelper retrofitHelper;

    //<editor-fold desc="Constructors">
    @Inject public HomeContentCache(
            @NotNull HomeServiceWrapper homeServiceWrapper,
            @NotNull Lazy<UserProfileCache> userProfileCacheLazy,
            @NotNull RetrofitHelper retrofitHelper)
    {
        super(DEFAULT_MAX_CACHE);
        this.homeServiceWrapper = homeServiceWrapper;
        this.userProfileCacheLazy = userProfileCacheLazy;
        this.retrofitHelper = retrofitHelper;
    }
    //</editor-fold>

    @NotNull @Override public HomeContentDTO fetch(@NotNull UserBaseKey key) throws Throwable
    {
        Response response = homeServiceWrapper.getHomePageContent(key);
        InputStream responseStream = response.getBody().in();
        Header header = retrofitHelper.findHeaderByName(response.getHeaders(), Constants.CONTENT_ENCODING);
        if (header != null && header.getValue().equalsIgnoreCase(Constants.CONTENT_ENCODING_GZIP))
        {
            responseStream = new GZIPInputStream(responseStream);
        }
        byte[] content = IOUtils.streamToBytes(responseStream);
        return new HomeContentDTO(new String(content));
    }

    @NotNull @Override public HomeContentDTO getOrFetchSync(@NotNull UserBaseKey key, boolean force) throws Throwable
    {
        userProfileCacheLazy.get().getOrFetchAsync(key, force);
        return super.getOrFetchSync(key, force);
    }

    @Override public void getOrFetchAsync(@NotNull UserBaseKey key, boolean forceUpdateCache)
    {
        userProfileCacheLazy.get().getOrFetchAsync(key, forceUpdateCache);
        super.getOrFetchAsync(key, forceUpdateCache);
    }
}
