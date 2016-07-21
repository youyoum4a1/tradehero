package com.androidth.general.fragments.social.hero;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.androidth.general.R;
import com.androidth.general.adapters.TypedRecyclerAdapter;
import com.androidth.general.api.social.HeroDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.user.follow.FollowUserAssistant;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

public class HeroRecyclerItemAdapter extends TypedRecyclerAdapter<HeroDisplayDTO>
{
    @Inject Picasso picasso;

    final PublishSubject<HeroDisplayDTO> itemActionPublishSubject = PublishSubject.create();

    @NonNull public static List<HeroDisplayDTO> createObjects(Resources resources, CurrentUserId currentUserId, UserBaseKey followerId, List<HeroDTO> heroes,
            UserProfileDTO currentUserProfileDTO)
    {
        ArrayList<HeroDisplayDTO> list = new ArrayList<>(heroes.size());
        for (HeroDTO heroDTO : heroes)
        {
            boolean isCurrentUserFollowing = currentUserProfileDTO.isFollowingUser(heroDTO.getBaseKey());

            /**
             * If it's the heroes' of current user that we're trying to fetch,
             * make sure that the current user is following that hero
             * since the cached values might not be synced with the server yet.
             */
            if (!currentUserId.toUserBaseKey().equals(followerId) || isCurrentUserFollowing)
            {
                list.add(new HeroDisplayDTO(resources, followerId, heroDTO, isCurrentUserFollowing));
            }
        }
        return list;
    }

    public HeroRecyclerItemAdapter(Context context)
    {
        super(HeroDisplayDTO.class, new FollowerItemComparator());
        HierarchyInjector.inject(context, this);
    }

    @Override public TypedViewHolder<HeroDisplayDTO> onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.follower_recycler_item, parent, false);
        HeroItemViewHolder viewHolder = new HeroItemViewHolder(view, picasso);
        viewHolder.getObservable().subscribe(itemActionPublishSubject);
        return viewHolder;
    }

    public Observable<HeroDisplayDTO> getHeroDTOObservable()
    {
        return itemActionPublishSubject.asObservable();
    }

    private static class FollowerItemComparator extends TypedRecyclerComparator<HeroDisplayDTO>
    {
        @Override public int compare(HeroDisplayDTO o1, HeroDisplayDTO o2)
        {
            return o2.heroDTO.followingSince.compareTo(o1.heroDTO.followingSince);
        }

        @Override public boolean areItemsTheSame(HeroDisplayDTO item1, HeroDisplayDTO item2)
        {
            return item1.heroDTO.getBaseKey().equals(item2.heroDTO.getBaseKey());
        }

        @Override public boolean areContentsTheSame(HeroDisplayDTO oldItem, HeroDisplayDTO newItem)
        {
            if (!oldItem.titleText.equals(newItem.titleText)) return false;
            if (oldItem.heroDTO.picture == null && newItem.heroDTO.picture != null) return false;
            if (oldItem.heroDTO.picture != null && newItem.heroDTO.picture == null) return false;
            if (oldItem.heroDTO.picture != null && !oldItem.heroDTO.picture.equals(newItem.heroDTO.picture)) return false;
            if (!oldItem.roiInfo.equals(newItem.roiInfo)) return false;
            if (oldItem.isCurrentUserFollowing != newItem.isCurrentUserFollowing) return false;
            return true;
        }
    }

    public static class HeroItemViewHolder extends TypedViewHolder<HeroDisplayDTO>
    {
        private final Picasso picasso;
        @BindView(R.id.follower_avatar) ImageView userIcon;
        @BindView(R.id.follower_name) TextView name;
        @BindView(R.id.follower_roi) TextView roiInfo;
        @BindView(R.id.follower_since) TextView since;
        @BindView(R.id.follower_button) ImageButton btnFollow;

        private HeroDisplayDTO currentDTO;

        final PublishSubject<HeroDisplayDTO> itemActionPublishSubject = PublishSubject.create();

        public HeroItemViewHolder(View itemView, Picasso picasso)
        {
            super(itemView);
            this.picasso = picasso;
        }

        @Override public void onDisplay(HeroDisplayDTO dto)
        {
            this.currentDTO = dto;
            if (dto.heroDTO.picture != null)
            {
                picasso.load(dto.heroDTO.picture)
                        .placeholder(R.drawable.superman_facebook)
                        .error(R.drawable.superman_facebook)
                        .into(userIcon);
            }
            name.setText(dto.titleText);
            roiInfo.setText(dto.roiInfo);
            since.setText(dto.followingSince);
            FollowUserAssistant.updateFollowImageButton(btnFollow, dto.isCurrentUserFollowing, dto.heroDTO.getBaseKey());
        }

        @OnClick(R.id.follower_button)
        public void onFollowButtonClicked()
        {
            if (this.currentDTO != null)
            {
                itemActionPublishSubject.onNext(currentDTO);
            }
        }

        public Observable<HeroDisplayDTO> getObservable()
        {
            return itemActionPublishSubject.asObservable();
        }
    }
}
