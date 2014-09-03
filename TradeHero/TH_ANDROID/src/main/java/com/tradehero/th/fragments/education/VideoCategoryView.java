package com.tradehero.th.fragments.education;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.education.PagedVideoCategoryId;
import com.tradehero.th.api.education.PaginatedVideoDTO;
import com.tradehero.th.api.education.VideoCategoryDTO;
import com.tradehero.th.api.education.VideoCategoryId;
import com.tradehero.th.api.education.VideoDTO;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.persistence.education.PaginatedVideoCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.StringUtils;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class VideoCategoryView extends RelativeLayout implements DTOView<VideoCategoryDTO>
{
    public final static int FIRST_PAGE = 1;
    public final static int DEFAULT_PER_PAGE = 50; //No pagination for now

    @InjectView(R.id.video_category_name) TextView textName;
    @InjectView(R.id.video_gallery) Gallery gallery;
    @InjectView(android.R.id.empty) View emptyView;
    @InjectView(android.R.id.progress) View progress;
    private VideoAdapter galleryAdapter;

    @Inject PaginatedVideoCache paginatedVideoCache;
    @Inject Lazy<CurrentActivityHolder> currentActivityHolderLazy;

    private DTOCacheNew.Listener<VideoCategoryId, PaginatedVideoDTO> cacheListener;

    private VideoCategoryDTO mCategoryDTO;

    private int page = FIRST_PAGE;
    private int perPage = DEFAULT_PER_PAGE;

    public VideoCategoryView(Context context)
    {
        super(context);
    }

    public VideoCategoryView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public VideoCategoryView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        DaggerUtils.inject(this);
        galleryAdapter = new VideoAdapter(getContext(), R.layout.video_view);
        cacheListener = new PaginatedVideoCacheListener();
        gallery.setAdapter(galleryAdapter);

        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                if (mCategoryDTO != null)
                {
                    mCategoryDTO.currentPosition = i;
                }
            }

            @Override public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                if (gallery.getSelectedItemPosition() == i)
                {
                    VideoDTO videoDTO = galleryAdapter.getItem(i);
                    handleItemClicked(videoDTO);
                }
            }
        });
    }

    private void handleItemClicked(@NotNull VideoDTO videoDTO)
    {
        if (!StringUtils.isNullOrEmpty(videoDTO.url))
        {
            Uri url = Uri.parse(videoDTO.url);
            Intent videoIntent = new Intent(Intent.ACTION_VIEW, url);
            PackageManager packageManager = getContext().getPackageManager();
            List<ResolveInfo> handlerActivities = packageManager.queryIntentActivities(videoIntent, 0);
            if (handlerActivities.size() > 0)
            {
                getContext().startActivity(videoIntent);
            }
            else
            {

                Activity activity = currentActivityHolderLazy.get().getCurrentActivity();
                if (activity instanceof DashboardActivity)
                {
                    Bundle bundle = new Bundle();
                    WebViewFragment.putUrl(bundle, videoDTO.url);
                    ((DashboardActivity) activity).getDashboardNavigator().pushFragment(WebViewFragment.class, bundle);
                }
                else
                {
                    THToast.show(R.string.unable_to_play_videos);
                }
            }
        }
    }

    @Override public void display(VideoCategoryDTO dto)
    {
        this.mCategoryDTO = dto;
        textName.setText(dto.name);
        galleryAdapter.clear();
        attachListenerAndFetch();
        showProgressView();
        hideEmptyView();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        attachListenerAndFetch();
    }

    @Override protected void onDetachedFromWindow()
    {
        detachListener();
        super.onDetachedFromWindow();
    }

    private void attachListenerAndFetch()
    {
        if (mCategoryDTO != null)
        {
            int id = mCategoryDTO.getVideoCategoryId().id;
            paginatedVideoCache.register(new PagedVideoCategoryId(id, page, perPage), cacheListener);
            paginatedVideoCache.getOrFetchAsync(new PagedVideoCategoryId(id, page, perPage));
        }
    }

    private void detachListener()
    {
        paginatedVideoCache.unregister(cacheListener);
    }

    private void restorePosition(VideoCategoryDTO videoCategoryDTO)
    {
        int position = videoCategoryDTO.currentPosition;
        if (galleryAdapter.getCount() <= position && !galleryAdapter.isEmpty())
        {
            position = galleryAdapter.getCount() - 1;
        }
        else if (galleryAdapter.isEmpty())
        {
            position = 0;
        }

        gallery.setSelection(position);
    }

    private void showEmptyView()
    {
        emptyView.setVisibility(View.VISIBLE);
    }

    private void hideEmptyView()
    {
        emptyView.setVisibility(View.GONE);
    }

    private void showProgressView()
    {
        progress.setVisibility(View.VISIBLE);
    }

    private void hideProgressView()
    {
        progress.setVisibility(View.GONE);
    }

    private class PaginatedVideoCacheListener implements DTOCacheNew.Listener<VideoCategoryId, PaginatedVideoDTO>
    {

        @Override public void onDTOReceived(@NotNull VideoCategoryId key, @NotNull PaginatedVideoDTO value)
        {
            if (mCategoryDTO.getVideoCategoryId().equals(key))
            {
                hideEmptyView();
                hideProgressView();
                galleryAdapter.addAll(value.getData());
                galleryAdapter.notifyDataSetChanged();
                restorePosition(mCategoryDTO);
            }
        }

        @Override public void onErrorThrown(@NotNull VideoCategoryId key, @NotNull Throwable error)
        {
            Timber.e("error");
            hideProgressView();
            showEmptyView();
        }
    }
}
