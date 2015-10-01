package com.tradehero.th.fragments.portfolio;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import butterknife.Bind;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TypedRecyclerAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdDisplayComparator;

public class PortfolioRecyclerAdapter extends TypedRecyclerAdapter<PortfolioDisplayDTO>
{
    public PortfolioRecyclerAdapter()
    {
        super(PortfolioDisplayDTO.class, new PortfolioDisplayDTOComparator());
    }

    @Override public TypedViewHolder<PortfolioDisplayDTO> onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new PortfolioDisplayDTOViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.portfolio_recycler_item, parent, false)
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
            return true;
        }
    }

    protected static class PortfolioDisplayDTOViewHolder extends TypedViewHolder<PortfolioDisplayDTO>
    {
        @Bind(R.id.portfolio_title) TextView title;
        @Bind(R.id.portfolio_description) TextView description;
        @Bind(R.id.roi_value) TextView roi;
        @Bind(R.id.portfolio_roi_since) TextView roiSince;
        @Bind(R.id.portfolio_total_value) TextView totalValue;
        @Bind(R.id.portfolio_cash_margin_left) TextView marginLeft;
        @Bind(R.id.grid_portfolio_values) TableLayout table;

        public PortfolioDisplayDTOViewHolder(View itemView)
        {
            super(itemView);
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
        }
    }
}
