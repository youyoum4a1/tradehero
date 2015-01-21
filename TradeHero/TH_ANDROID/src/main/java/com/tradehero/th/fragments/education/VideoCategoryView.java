package com.tradehero.th.fragments.education;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.education.PagedVideoCategoryId;
import com.tradehero.th.api.education.PaginatedVideoDTO;
import com.tradehero.th.api.education.VideoCategoryDTO;
import com.tradehero.th.api.education.VideoCategoryId;
import com.tradehero.th.api.education.VideoDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.persistence.education.PaginatedVideoCacheRx;
import com.tradehero.th.utils.StringUtils;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class VideoCategoryView extends RelativeLayout
        implements DTOView<VideoCategoryDTO>
{
    public final static int FIRST_PAGE = 1;
    public final static int DEFAULT_PER_PAGE = 50; //No pagination for now

    @InjectView(R.id.video_category_name) TextView textName;
    @InjectView(R.id.video_gallery) Gallery gallery;
    @InjectView(android.R.id.empty) View emptyView;
    @InjectView(android.R.id.progress) View progress;
    @NonNull private final VideoAdapter galleryAdapter;

    @Inject PaginatedVideoCacheRx paginatedVideoCache;
    @Inject DashboardNavigator navigator;

    @Nullable private Subscription paginatedVideoCacheSubscription;

    private VideoCategoryDTO mCategoryDTO;

    private int page = FIRST_PAGE;
    private int perPage = DEFAULT_PER_PAGE;

    //<editor-fold desc="Constructors">
    public VideoCategoryView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
        galleryAdapter = new VideoAdapter(
                getContext(),
                (lhs, rhs) -> lhs.getVideoId().compareTo(rhs.getVideoId()),
                R.layout.video_view);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        gallery.setAdapter(galleryAdapter);
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemSelected(value = R.id.video_gallery, callback = OnItemSelected.Callback.ITEM_SELECTED)
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        if (mCategoryDTO != null)
        {
            mCategoryDTO.currentPosition = i;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemClick(R.id.video_gallery)
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        if (gallery.getSelectedItemPosition() == i)
        {
            VideoDTO videoDTO = galleryAdapter.getItem(i);
            handleItemClicked(videoDTO);
        }
    }

    private void handleItemClicked(@NonNull VideoDTO videoDTO)
    {
        VideoDTOUtil.openVideoDTO(getContext(), navigator, videoDTO);
    }

    @Override public void display(@NonNull VideoCategoryDTO dto)
    {
        this.mCategoryDTO = dto;
        textName.setText(dto.name);
        galleryAdapter.clear();
        galleryAdapter.notifyDataSetChanged();
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
        detachVideoListCacheSubscription();
        super.onDetachedFromWindow();
    }

    private void attachListenerAndFetch()
    {
        if (mCategoryDTO != null)
        {
            int id = mCategoryDTO.getVideoCategoryId().id;
            detachVideoListCacheSubscription();
            paginatedVideoCacheSubscription = paginatedVideoCache.get(new PagedVideoCategoryId(id, page, perPage))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new PaginatedVideoCacheObserver());
        }
    }

    private void detachVideoListCacheSubscription()
    {
        Subscription copy = paginatedVideoCacheSubscription;
        if (copy != null)
        {
            copy.unsubscribe();
        }
        paginatedVideoCacheSubscription = null;
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

    private class PaginatedVideoCacheObserver implements Observer<Pair<VideoCategoryId, PaginatedVideoDTO>>
    {
        @Override public void onNext(Pair<VideoCategoryId, PaginatedVideoDTO> pair)
        {
            if (mCategoryDTO.getVideoCategoryId().equals(pair.first))
            {
                hideEmptyView();
                hideProgressView();
                galleryAdapter.appendHead(pair.second.getData());
                galleryAdapter.notifyDataSetChanged();
                restorePosition(mCategoryDTO);
            }
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            Timber.e("error");
            hideProgressView();
            showEmptyView();
        }
    }
}
