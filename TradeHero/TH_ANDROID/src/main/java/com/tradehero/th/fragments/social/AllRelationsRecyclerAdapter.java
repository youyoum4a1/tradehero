package com.tradehero.th.fragments.social;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedRecyclerAdapter;
import com.tradehero.th.api.users.AllowableRecipientDTO;

public class AllRelationsRecyclerAdapter extends PagedRecyclerAdapter<RelationItemDisplayDTO>
{
    private final Picasso picasso;

    public AllRelationsRecyclerAdapter(Picasso picasso)
    {
        super(RelationItemDisplayDTO.class, new RelationItemComparator());
        this.picasso = picasso;
    }

    @Override public TypedViewHolder<RelationItemDisplayDTO> onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new RelationItemViewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.relations_list_item, parent, false), picasso);
    }

    public static class RelationItemComparator extends TypedRecyclerComparator<RelationItemDisplayDTO>
    {
        @Override public int compare(RelationItemDisplayDTO o1, RelationItemDisplayDTO o2)
        {
            return o1.orderFromServer - o2.orderFromServer;
        }

        @Override public boolean areItemsTheSame(RelationItemDisplayDTO item1, RelationItemDisplayDTO item2)
        {
            return item1.allowableRecipientDTO.user.id == item2.allowableRecipientDTO.user.id;
        }
    }

    public static class RelationItemViewholder extends TypedViewHolder<RelationItemDisplayDTO>
    {
        private final Picasso picasso;

        @Bind(R.id.user_profile_avatar) ImageView avatar;
        @Bind(R.id.user_name) TextView userName;
        private AllowableRecipientDTO recipientDTO;

        public RelationItemViewholder(View itemView, Picasso picasso)
        {
            super(itemView);
            this.picasso = picasso;
        }

        @Override public void display(RelationItemDisplayDTO relationItemDisplayDTO)
        {
            this.recipientDTO = relationItemDisplayDTO.allowableRecipientDTO;
            picasso.load(relationItemDisplayDTO.picture)
                    .placeholder(R.drawable.superman_facebook)
                    .error(R.drawable.superman_facebook)
                    .into(avatar);

            userName.setText(relationItemDisplayDTO.displayName);
        }
    }
}
