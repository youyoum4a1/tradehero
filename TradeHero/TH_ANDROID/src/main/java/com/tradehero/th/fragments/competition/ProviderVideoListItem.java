package com.tradehero.th.fragments.competition;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.competition.HelpVideoDTO;
import com.tradehero.th.api.competition.HelpVideoId;
import com.tradehero.th.persistence.competition.HelpVideoCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

/**
 * Created by xavier on 1/16/14.
 */
public class ProviderVideoListItem extends RelativeLayout implements DTOView<HelpVideoId>
{
    public static final String TAG = ProviderVideoListItem.class.getSimpleName();

    private HelpVideoId videoId;
    private HelpVideoDTO videoDTO;
    @Inject protected HelpVideoCache helpVideoCache;
    private DTOCache.Listener<HelpVideoId, HelpVideoDTO> helpVideoCacheListener;
    private DTOCache.GetOrFetchTask<HelpVideoId, HelpVideoDTO> helpVideoCacheFetchTask;

    //<editor-fold desc="Constructors">
    public ProviderVideoListItem(Context context)
    {
        super(context);
    }

    public ProviderVideoListItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ProviderVideoListItem(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        this.helpVideoCacheListener = new ProviderVideoListItemVideoCacheListener();
    }

    @Override protected void onDetachedFromWindow()
    {
        detachFetchTask();
        this.helpVideoCacheListener = null;
        super.onDetachedFromWindow();
    }

    private void detachFetchTask()
    {
        if (this.helpVideoCacheFetchTask != null)
        {
            this.helpVideoCacheFetchTask.setListener(null);
        }
        this.helpVideoCacheFetchTask = null;
    }

    @Override public void display(HelpVideoId videoId1)
    {
        // TODO call
    }

    public void linkWith(HelpVideoDTO videoDto, boolean andDisplay)
    {
        this.videoDTO = videoDto;
        if (andDisplay)
        {
            // TODO
        }
    }

    private class ProviderVideoListItemVideoCacheListener implements DTOCache.Listener<HelpVideoId, HelpVideoDTO>
    {
        @Override public void onDTOReceived(HelpVideoId key, HelpVideoDTO value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(HelpVideoId key, Throwable error)
        {
            THToast.show(getContext().getString(R.string.error_fetch_help_video_list_info));
            THLog.e(TAG, "Error fetching the help video info" + key, error);
        }
    }
}
