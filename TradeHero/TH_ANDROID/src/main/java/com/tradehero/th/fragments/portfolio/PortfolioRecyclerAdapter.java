package com.tradehero.th.fragments.portfolio;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import butterknife.Bind;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TypedRecyclerAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdDisplayComparator;
import com.tradehero.th.inject.HierarchyInjector;
import javax.inject.Inject;

public class PortfolioRecyclerAdapter extends TypedRecyclerAdapter<PortfolioDisplayDTO>
{
    @Inject Picasso picasso;

    public PortfolioRecyclerAdapter(Context context)
    {
        super(PortfolioDisplayDTO.class, new PortfolioDisplayDTOComparator());
        HierarchyInjector.inject(context, this);
    }

    @Override public TypedViewHolder<PortfolioDisplayDTO> onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new PortfolioDisplayDTOViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.portfolio_recycler_item, parent, false),
                picasso
        );
    }

    protected static class PortfolioDisplayDTOComparator extends TypedRecyclerComparator<PortfolioDisplayDTO>
    {
        @NonNull private final OwnedPortfolioIdDisplayComparator ownedPortfolioIdDisplayComparator;

        public PortfolioDisplayDTOComparator()
        {
            this.ownedPortfolioIdDisplayComparator = new OwnedPortfolioIdDisplayComparator();
        }

        @Override public int compare(PortfolioDisplayDTO o1, PortfolioDisplayDTO o2)
        {
            if (o1.isDefault() && o2.isDefault())
            {
                if (o1.assetClass != null && o2.assetClass != null)
                {
                    return o1.assetClass.compareTo(o2.assetClass);
                }
                else if (o1.assetClass != null)
                {
                    return -1;
                }
                return 1;
            }
            else if (o1.isDefault())
            {
                return -1;
            }
            else if (o2.isDefault())
            {
                return 1;
            }
            else if (o1.isWatchlist)
            {
                if (o2.isWatchlist)
                {
                    return 0;
                }
                return -1;
            }
            else if (o2.isWatchlist)
            {
                return 1;
            }
            else if (o1.isCompetition && o2.isCompetition)
            {
                if (o1.isVip && o2.isVip)
                {
                    return o1.providerId - o2.providerId;
                }
                else if (o1.isVip)
                {
                    return -1;
                }
                else if (o2.isVip)
                {
                    return 1;
                }
            }
            else if (o1.isCompetition)
            {
                return 1;
            }
            else if (o2.isCompetition)
            {
                return -1;
            }
            else if (o1.providerId != null && o2.providerId != null)
            {
                return o1.providerId.compareTo(o2.providerId);
            }
            else if (o1.providerId != null)
            {
                return -1;
            }
            else if (o2.providerId != null)
            {
                return 1;
            }
            return this.ownedPortfolioIdDisplayComparator.compare(o1.ownedPortfolioId, o2.ownedPortfolioId);
        }

        @Override public boolean areItemsTheSame(PortfolioDisplayDTO item1, PortfolioDisplayDTO item2)
        {
            if (item1.isCompetition && item2.isCompetition)
            {
                return item1.providerId == item2.providerId;
            }
            if (item1.ownedPortfolioId == null && item2.ownedPortfolioId != null) return false;
            if (item1.ownedPortfolioId != null && item2.ownedPortfolioId == null) return false;
            return item1.ownedPortfolioId != null && item1.ownedPortfolioId.equals(item2.ownedPortfolioId);
        }

        @Override public boolean areContentsTheSame(PortfolioDisplayDTO oldItem, PortfolioDisplayDTO newItem)
        {
            if (!oldItem.portfolioTitle.equals(newItem.portfolioTitle)) return false;
            if (oldItem.portfolioTitleColor != newItem.portfolioTitleColor) return false;
            if (!oldItem.description.equals(newItem.description)) return false;
            if (!oldItem.roiValue.toString().equals(newItem.roiValue.toString())) return false;
            if (oldItem.roiVisibility != newItem.roiVisibility) return false;
            if (!oldItem.sinceValue.equals(newItem.sinceValue)) return false;
            if (oldItem.sinceValueVisibility != newItem.sinceValueVisibility) return false;
            if (oldItem.marginLeft != null && newItem.marginLeft == null) return false;
            if (oldItem.marginLeft == null && newItem.marginLeft != null) return false;
            if (oldItem.marginLeft != null && !oldItem.marginLeft.toString().equals(newItem.marginLeft.toString())) return false;
            if (oldItem.totalValue != null && newItem.totalValue == null) return false;
            if (oldItem.totalValue == null && newItem.totalValue != null) return false;
            if (oldItem.totalValue != null && !oldItem.totalValue.toString().equals(newItem.totalValue.toString())) return false;
            if (oldItem.joinBanner != null && newItem.joinBanner == null) return false;
            if (oldItem.joinBanner == null && newItem.joinBanner != null) return false;
            if (oldItem.joinBanner != null && !oldItem.joinBanner.equals(newItem.joinBanner)) return false;
            return true;
        }
    }

    protected static class PortfolioDisplayDTOViewHolder extends TypedViewHolder<PortfolioDisplayDTO>
    {
        private final Picasso picasso;
        @Bind(R.id.portfolio_title) TextView title;
        @Bind(R.id.portfolio_description) TextView description;
        @Bind(R.id.roi_value) TextView roi;
        @Bind(R.id.portfolio_roi_since) TextView roiSince;
        @Bind(R.id.portfolio_total_value) TextView totalValue;
        @Bind(R.id.portfolio_cash_margin_left) TextView marginLeft;
        @Bind(R.id.grid_portfolio_values) TableLayout table;
        @Bind(R.id.competition_banner) ImageView competitionBanner;
        @Bind(R.id.portfolio_cash_margin_left_label) TextView cashMarginLabel;

        public PortfolioDisplayDTOViewHolder(View itemView, Picasso picasso)
        {
            super(itemView);
            this.picasso = picasso;
        }

        @Override public void onDisplay(PortfolioDisplayDTO portfolioDisplayDTO)
        {
            title.setText(portfolioDisplayDTO.portfolioTitle);
            title.setTextColor(portfolioDisplayDTO.portfolioTitleColor);
            description.setText(portfolioDisplayDTO.description);
            roi.setText(portfolioDisplayDTO.roiValue);
            roi.setVisibility(portfolioDisplayDTO.roiVisibility);
            roiSince.setText(portfolioDisplayDTO.sinceValue);
            roiSince.setVisibility(portfolioDisplayDTO.sinceValueVisibility);
            cashMarginLabel.setText(portfolioDisplayDTO.cashMarginLabel);
            if (portfolioDisplayDTO.marginLeft != null && portfolioDisplayDTO.totalValue != null)
            {
                table.setVisibility(View.VISIBLE);
                totalValue.setText(portfolioDisplayDTO.totalValue);
                marginLeft.setText(portfolioDisplayDTO.marginLeft);
            }
            else
            {
                table.setVisibility(View.GONE);
            }
            if (portfolioDisplayDTO.isCompetition)
            {
                picasso.load(portfolioDisplayDTO.joinBanner)
                        .placeholder(R.drawable.lb_competitions_bg)
                        .into(competitionBanner);
            }
            else
            {
                competitionBanner.setVisibility(View.GONE);
                competitionBanner.setImageDrawable(null);
            }
        }
    }
}
