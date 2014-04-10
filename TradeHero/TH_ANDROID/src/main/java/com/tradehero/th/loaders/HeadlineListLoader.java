package com.tradehero.th.loaders;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v4.content.pm.ActivityInfoCompat;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.fragments.news.HeadlineFragment;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import retrofit.RetrofitError;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by tradehero on 14-3-12.
 */
public class HeadlineListLoader extends PaginationListLoader<NewsItemDTO> {

    private int currentPage = -1;
    private static final int PER_PAGE = 42;

    @Inject
    NewsServiceWrapper newsServiceWrapper;

    public HeadlineListLoader(Context context,int targetPage) {
        super(context);
        this.currentPage = targetPage-1;
        DaggerUtils.inject(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    final InterestingConfigChanges mLastConfig = new InterestingConfigChanges();

    public static class InterestingConfigChanges {
        final Configuration mLastConfiguration = new Configuration();
        int mLastDensity;

        boolean applyNewConfig(Resources res) {
            int configChanges = mLastConfiguration.updateFrom(res.getConfiguration());
            boolean densityChanged = mLastDensity != res.getDisplayMetrics().densityDpi;
            if (densityChanged || (configChanges&(ActivityInfo.CONFIG_LOCALE
                    | ActivityInfoCompat.CONFIG_UI_MODE| ActivityInfo.CONFIG_SCREEN_LAYOUT)) != 0) {
                mLastDensity = res.getDisplayMetrics().densityDpi;
                return true;
            }
            return false;
        }
    }


    @Override
    protected void onStartLoading() {
        if (items.size() != 0){
            super.deliverResult(items);
        }

        boolean configChange = mLastConfig.applyNewConfig(getContext().getResources());
        if (takeContentChanged() || items.size() == 0 || configChange) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        //when fragment stop
        cancelLoad();
    }

    @Override
    public void onCanceled(List<NewsItemDTO> data) {
        super.onCanceled(data);
        releaseResources(data);
    }

    @Override
    public List<NewsItemDTO> loadInBackground() {
        PaginatedDTO<NewsItemDTO> dataWrapper = loadData();
        if(dataWrapper != null && dataWrapper.getData() !=null && dataWrapper.getData().size() > 0) {
            return dataWrapper.getData();
        }
        return null;

    }

    @Override
    public void deliverResult(List<NewsItemDTO> data) {
        if (isReset() && data != null) {
            releaseResources(data);
        }
        boolean haveData = data != null;
        if (haveData){
            items.addAll(data);
        }
        setTargetPageResult(haveData);
        if (isStarted()) {
            super.deliverResult(data);
        }
        setNotBusy();
    }


    @Override
    protected void releaseResources(List<NewsItemDTO> data) {
        //super.releaseResources(data);
    }

    ////////////////////////////////////////////////////////////////////////////////////////
   private void addAndSetPage(Object...params) {
       if (params.length == 1 ){
           Integer targetPage = (Integer)params[0];
           //this.curretnPage = targetPage;
       }else {
           //targetPage++;
       }
       if (currentPage < 0) {
           throw new IllegalArgumentException("page cannot be less than -1!");
       }
   }

    private void setTargetPageResult(boolean haveData) {
        if (haveData) {
            currentPage++;
        }else {
        }
        if (currentPage < -1) {
            throw new IllegalArgumentException("page cannot be less than -1!");
        }
    }


    @Override
    public void loadNext(Object...targetPage) {
        if (targetPage.length == 0) {
            Timber.d("%s HeadlineListLoader loadNext(parameter targetPage:%s) ",HeadlineFragment.TAG,targetPage[0]);
        }else {
            Timber.d("%s HeadlineListLoader loadNext(targetPage:%s) ",HeadlineFragment.TAG,currentPage+1);
        }
        boolean isBusy = isBusy();
        if (!isBusy) {
            addAndSetPage(targetPage);
            onContentChanged();
            //startLoading();
            return;
        }
        Timber.e("%s HeadlineListLoader try to loadNext,but the loader is busy", HeadlineFragment.TAG);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    @Deprecated
    @Override
    public void loadPrevious(Object...params) {
        //DO NOTHING
    }

    @Deprecated
    @Override
    protected void onLoadNext(NewsItemDTO endItem) {
        //DO NOTHING
    }

    @Deprecated
    @Override
    protected void onLoadPrevious(NewsItemDTO startItem) {
        //DO NOTHING
    }

    private int transformPage(int page) {
        return page+1;
    }

    //////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////

    private PaginatedDTO<NewsItemDTO> loadData() {
        Timber.d("%s HeadlineListLoader load data(targetPage:%d) ",HeadlineFragment.TAG,currentPage+1);
        try {
            PaginatedDTO<NewsItemDTO> data = newsServiceWrapper.getGlobalNews(transformPage(currentPage+1), getPerPage());
            return data;
        }catch (RetrofitError e){
            Timber.e("HeadlineListLoader load data errer(page:%d)",currentPage+1);
            return null;
        }

    }
}
