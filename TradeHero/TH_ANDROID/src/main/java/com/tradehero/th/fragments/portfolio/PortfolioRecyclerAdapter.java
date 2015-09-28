package com.tradehero.th.fragments.portfolio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import butterknife.Bind;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TypedRecyclerAdapter;

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
        @Override public int compare(PortfolioDisplayDTO o1, PortfolioDisplayDTO o2)
        {
            if (o1.ownedPortfolioId == null)
            {
                return o2.ownedPortfolioId == null ? 0 : -1;
            }
            if (o2.ownedPortfolioId == null)
            {
                return 1;
            }

            return o1.ownedPortfolioId.compareTo(o2.ownedPortfolioId);
        }

        @Override public boolean areItemsTheSame(PortfolioDisplayDTO item1, PortfolioDisplayDTO item2)
        {
            if (item1.ownedPortfolioId == null && item2.ownedPortfolioId != null) return false;
            if (item1.ownedPortfolioId != null && item2.ownedPortfolioId == null) return false;
            return item1.ownedPortfolioId != null && item1.ownedPortfolioId.equals(item2.ownedPortfolioId);
        }

        @Override public boolean areContentsTheSame(PortfolioDisplayDTO oldItem, PortfolioDisplayDTO newItem)
        {
            return super.areContentsTheSame(oldItem, newItem);
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

        @Override public void display(PortfolioDisplayDTO portfolioDisplayDTO)
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
