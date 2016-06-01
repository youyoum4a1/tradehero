package com.ayondo.academy.fragments.education;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;
import com.ayondo.academy.R;
import com.ayondo.academy.api.DTOView;
import com.ayondo.academy.api.education.PagedVideoCategoryId;
import com.ayondo.academy.api.education.PaginatedVideoDTO;
import com.ayondo.academy.api.education.VideoCategoryDTO;
import com.ayondo.academy.api.education.VideoCategoryId;
import com.ayondo.academy.api.education.VideoDTO;
import com.ayondo.academy.fragments.DashboardNavigator;
import com.ayondo.academy.inject.HierarchyInjector;
import com.ayondo.academy.persistence.education.PaginatedVideoCacheRx;
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

    @Bind(R.id.video_category_name) TextView textName;
    @Bind(R.id.video_gallery) Gallery gallery;
    @Bind(android.R.id.empty) View emptyView;
    @Bind(android.R.id.progress) View progress;
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
                R.layout.video_view);
    }
    //</editor-fold>

    private void adjustFirstItemOfGallery() {
        try
        {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            MarginLayoutParams mlp = (MarginLayoutParams) gallery.getLayoutParams();
            mlp.setMargins(-((metrics.widthPixels/2)+100),
                    mlp.topMargin,
                    mlp.rightMargin,
                    mlp.bottomMargin
            );
        } catch (Exception e) {
            Timber.d("Error",e);
        }
    }
    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
        gallery.setAdapter(galleryAdapter);
        adjustFirstItemOfGallery();
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
