package com.tradehero.th.fragments.social.hero;

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
import com.tradehero.th.R;
import com.tradehero.th.adapters.TypedRecyclerAdapter;
import com.tradehero.th.api.social.HeroDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.social.hero.HeroListItemView.DTO;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.user.follow.SimpleFollowUserAssistant;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

public class HeroRecyclerItemAdapter extends TypedRecyclerAdapter<HeroListItemView.DTO>
{
    @Inject Picasso picasso;

    final PublishSubject<HeroListItemView.DTO> itemActionPublishSubject = PublishSubject.create();

    @NonNull public static List<DTO> createObjects(Resources resources, CurrentUserId currentUserId, UserBaseKey followerId, List<HeroDTO> heroes,
            UserProfileDTO currentUserProfileDTO)
    {
        ArrayList<HeroListItemView.DTO> list = new ArrayList<>(heroes.size());
        for (HeroDTO heroDTO : heroes)
        {
            list.add(new HeroListItemView.DTO(resources, currentUserId, followerId, heroDTO, currentUserProfileDTO));
        }
        return list;

    }

    public HeroRecyclerItemAdapter(Context context)
    {
        super(HeroListItemView.DTO.class, new FollowerItemComparator());
        HierarchyInjector.inject(context, this);
    }

    @Override public TypedViewHolder<HeroListItemView.DTO> onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.follower_recycler_item, parent, false);
        HeroItemViewHolder viewHolder = new HeroItemViewHolder(view, picasso);
        viewHolder.getObservable().subscribe(itemActionPublishSubject);
        return viewHolder;
    }

    public Observable<HeroListItemView.DTO> getHeroDTOObservable()
    {
        return itemActionPublishSubject.asObservable();
    }

    private static class FollowerItemComparator extends TypedRecyclerComparator<HeroListItemView.DTO>
    {
        @Override public int compare(HeroListItemView.DTO o1, HeroListItemView.DTO o2)
        {
            return o2.heroDTO.followingSince.compareTo(o1.heroDTO.followingSince);
        }

        @Override public boolean areItemsTheSame(HeroListItemView.DTO item1, HeroListItemView.DTO item2)
        {
            return item1.heroDTO.getBaseKey().equals(item2.heroDTO.getBaseKey());
        }

        @Override public boolean areContentsTheSame(HeroListItemView.DTO oldItem, HeroListItemView.DTO newItem)
        {
            if (!oldItem.titleText.equals(newItem.titleText)) return false;
            if (oldItem.heroDTO.picture == null && newItem.heroDTO.picture != null) return false;
            if (oldItem.heroDTO.picture != null && newItem.heroDTO.picture == null) return false;
            if (oldItem.heroDTO.picture != null && !oldItem.heroDTO.picture.equals(newItem.heroDTO.picture)) return false;
            if (!oldItem.roiInfo.equals(newItem.roiInfo)) return false;
            if (oldItem.isFollowing != newItem.isFollowing) return false;
            return true;
        }
    }

    public static class HeroItemViewHolder extends TypedViewHolder<HeroListItemView.DTO>
    {
        private final Picasso picasso;
        @Bind(R.id.follower_avatar) ImageView userIcon;
        @Bind(R.id.follower_name) TextView name;
        @Bind(R.id.follower_roi) TextView roiInfo;
        @Bind(R.id.follower_since) TextView since;
        @Bind(R.id.follower_button) ImageButton btnFollow;

        private HeroListItemView.DTO currentDTO;

        final PublishSubject<HeroListItemView.DTO> itemActionPublishSubject = PublishSubject.create();

        public HeroItemViewHolder(View itemView, Picasso picasso)
        {
            super(itemView);
            this.picasso = picasso;
        }

        @Override public void display(HeroListItemView.DTO dto)
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
            SimpleFollowUserAssistant.updateFollowImageButton(btnFollow, dto.isFollowing);
        }

        @OnClick(R.id.follower_button)
        public void onFollowButtonClicked()
        {
            if(this.currentDTO != null)
            {
                itemActionPublishSubject.onNext(currentDTO);
            }
        }

        public Observable<HeroListItemView.DTO> getObservable()
        {
            return itemActionPublishSubject.asObservable();
        }
    }
}
