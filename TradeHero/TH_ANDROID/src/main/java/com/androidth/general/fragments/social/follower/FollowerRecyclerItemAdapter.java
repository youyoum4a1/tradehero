package com.androidth.general.fragments.social.follower;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.androidth.general.R;
import com.androidth.general.adapters.TypedRecyclerAdapter;
import com.androidth.general.api.social.UserFollowerDTO;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.user.follow.FollowUserAssistant;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

public class FollowerRecyclerItemAdapter extends TypedRecyclerAdapter<FollowerDisplayDTO>
{
    @Inject Picasso picasso;

    final PublishSubject<FollowerDisplayDTO> itemActionPublishSubject = PublishSubject.create();

    public FollowerRecyclerItemAdapter(Context context)
    {
        super(FollowerDisplayDTO.class, new FollowerItemComparator());
        HierarchyInjector.inject(context, this);
    }

    @Override public TypedViewHolder<FollowerDisplayDTO> onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.follower_recycler_item, parent, false);
        FollowerItemViewHolder viewHolder = new FollowerItemViewHolder(view, picasso);
        viewHolder.getObservable().subscribe(itemActionPublishSubject);
        return viewHolder;
    }

    @NonNull public static List<FollowerDisplayDTO> createItems(Resources resources, List<UserFollowerDTO> followerDTOs, UserProfileDTO userProfileDTO)
    {
        ArrayList<FollowerDisplayDTO> list = new ArrayList<>(followerDTOs.size());
        for (UserFollowerDTO userFollowerDTO : followerDTOs)
        {
            list.add(new FollowerDisplayDTO(resources, userFollowerDTO, userProfileDTO));
        }
        return list;
    }

    public Observable<FollowerDisplayDTO> getFollowerDTOObservable()
    {
        return itemActionPublishSubject.asObservable();
    }

    private static class FollowerItemComparator extends TypedRecyclerComparator<FollowerDisplayDTO>
    {
        @Override public int compare(FollowerDisplayDTO o1, FollowerDisplayDTO o2)
        {
            return o2.userFollowerDTO.followingSince.compareTo(o1.userFollowerDTO.followingSince);
        }

        @Override public boolean areItemsTheSame(FollowerDisplayDTO item1, FollowerDisplayDTO item2)
        {
            return item1.userFollowerDTO.getBaseKey().equals(item2.userFollowerDTO.getBaseKey());
        }

        @Override public boolean areContentsTheSame(FollowerDisplayDTO oldItem, FollowerDisplayDTO newItem)
        {
            if (!oldItem.titleText.equals(newItem.titleText)) return false;
            if (oldItem.userFollowerDTO.picture == null && newItem.userFollowerDTO.picture != null) return false;
            if (oldItem.userFollowerDTO.picture != null && newItem.userFollowerDTO.picture == null) return false;
            if (oldItem.userFollowerDTO.picture != null && !oldItem.userFollowerDTO.picture.equals(newItem.userFollowerDTO.picture)) return false;
            if (!oldItem.roiInfoText.equals(newItem.roiInfoText)) return false;
            if (oldItem.isFollowing != newItem.isFollowing) return false;
            return true;
        }
    }

    public static class FollowerItemViewHolder extends TypedViewHolder<FollowerDisplayDTO>
    {
        private final Picasso picasso;
        @Bind(R.id.follower_avatar) ImageView userIcon;
        @Bind(R.id.follower_name) TextView name;
        @Bind(R.id.follower_roi) TextView roiInfo;
        @Bind(R.id.follower_since) TextView since;
        @Bind(R.id.follower_button) ImageButton btnFollow;

        private FollowerDisplayDTO currentDTO;

        final PublishSubject<FollowerDisplayDTO> itemActionPublishSubject = PublishSubject.create();

        public FollowerItemViewHolder(View itemView, Picasso picasso)
        {
            super(itemView);
            this.picasso = picasso;
        }

        @Override public void onDisplay(FollowerDisplayDTO dto)
        {
            this.currentDTO = dto;
            if (dto.userFollowerDTO.picture != null)
            {
                picasso.load(dto.userFollowerDTO.picture)
                        .placeholder(R.drawable.superman_facebook)
                        .error(R.drawable.superman_facebook)
                        .into(userIcon);
            }
            name.setText(dto.titleText);
            roiInfo.setText(dto.roiInfoText);
            since.setText(dto.followingSince);
            FollowUserAssistant.updateFollowImageButton(btnFollow, dto.isFollowing, this.currentDTO.userFollowerDTO.getBaseKey());
        }

        @OnClick(R.id.follower_button)
        public void onFollowButtonClicked()
        {
            if(this.currentDTO != null)
            {
                itemActionPublishSubject.onNext(currentDTO);
            }
        }

        public Observable<FollowerDisplayDTO> getObservable()
        {
            return itemActionPublishSubject.asObservable();
        }
    }
}
