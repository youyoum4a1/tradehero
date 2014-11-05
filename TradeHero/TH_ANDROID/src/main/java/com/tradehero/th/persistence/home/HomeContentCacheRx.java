package com.tradehero.th.persistence.home;

import android.util.Pair;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.common.utils.IOUtils;
import com.tradehero.common.utils.RetrofitHelper;
import com.tradehero.th.api.home.HomeContentDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.HomeServiceWrapper;
import com.tradehero.th.utils.Constants;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import retrofit.client.Header;
import rx.Observable;

@Singleton @UserCache
public class HomeContentCacheRx extends BaseFetchDTOCacheRx<UserBaseKey, HomeContentDTO>
{
    private static final int DEFAULT_MAX_CACHE = 1;
    @NonNull private final HomeServiceWrapper homeServiceWrapper;
    @NonNull private final RetrofitHelper retrofitHelper;

    //<editor-fold desc="Constructors">
    @Inject public HomeContentCacheRx(
            @NonNull HomeServiceWrapper homeServiceWrapper,
            @NonNull RetrofitHelper retrofitHelper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_CACHE, DEFAULT_MAX_CACHE, DEFAULT_MAX_CACHE, dtoCacheUtil);
        this.homeServiceWrapper = homeServiceWrapper;
        this.retrofitHelper = retrofitHelper;
    }
    //</editor-fold>

    @NonNull @Override protected Observable<HomeContentDTO> fetch(@NonNull UserBaseKey key)
    {
        return homeServiceWrapper.getHomePageContentRx(key)
                .flatMap(response -> {
                    try
                    {
                        InputStream responseStream = response.getBody().in();
                        Header header = retrofitHelper.findHeaderByName(response.getHeaders(), Constants.CONTENT_ENCODING);
                        if (header != null && header.getValue().equalsIgnoreCase(Constants.CONTENT_ENCODING_GZIP))
                        {
                            responseStream = new GZIPInputStream(responseStream);
                        }
                        byte[] content = IOUtils.streamToBytes(responseStream);
                        return Observable.just(new HomeContentDTO(new String(content)));
                    } catch (IOException e)
                    {
                        return Observable.error(e);
                    }
                });
    }

    @NonNull @Override public Observable<Pair<UserBaseKey, HomeContentDTO>> get(@NonNull UserBaseKey key)
    {
        return super.get(key);
    }
}
