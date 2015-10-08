package com.tradehero.th.fragments.trade;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TypedRecyclerAdapter;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.api.trade.TradeDTOList;
import java.util.ArrayList;
import java.util.List;
import org.ocpsoft.prettytime.PrettyTime;

public class TradesRecyclerAdapter extends TypedRecyclerAdapter<Object>
{
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_SUMMARY = 1;
    private static final int VIEW_TYPE_TRADE = 2;

    public TradesRecyclerAdapter()
    {
        super(Object.class, new TradeListComparator());
    }

    @NonNull public static List<Object> createObjects(
            @NonNull Resources resources,
            @NonNull PositionDTO positionDTO,
            @NonNull SecurityCompactDTO securityCompactDTO,
            @Nullable TradeDTOList tradeDTOs,
            @NonNull PrettyTime prettyTime)
    {
        List<Object> objects = new ArrayList<>();

        Boolean isClosed = positionDTO.isClosed();
        objects.add(new TradeHeaderDisplayDTO(TradeHeaderDisplayDTO.HEADER_TYPE_POSITION, resources.getString(
                (isClosed != null && isClosed) ? R.string.trade_list_header_closed_summary : R.string.trade_list_header_open_summary)));

        objects.add(new TradeSummaryDisplayDTO(resources, securityCompactDTO, positionDTO));

        if (tradeDTOs != null && !tradeDTOs.isEmpty())
        {
            objects.add(new TradeHeaderDisplayDTO(TradeHeaderDisplayDTO.HEADER_TYPE_TRADE, resources.getString(R.string.trade_list_header_history)));
            for (int i = 0; i < tradeDTOs.size(); i++)
            {
                TradeDTO dto = tradeDTOs.get(i);
                objects.add(
                        new TradeDisplayDTO(resources, securityCompactDTO, positionDTO, dto, prettyTime));
            }
        }

        return objects;
    }

    @Override public int getItemViewType(int position)
    {
        Object o = getItem(position);
        if (o instanceof TradeHeaderDisplayDTO)
        {
            return VIEW_TYPE_HEADER;
        }
        if (o instanceof TradeSummaryDisplayDTO)
        {
            return VIEW_TYPE_SUMMARY;
        }
        if (o instanceof TradeDisplayDTO)
        {
            return VIEW_TYPE_TRADE;
        }
        throw new IllegalStateException("Unhandled object " + o.getClass().getName());
    }

    @Override public TypedViewHolder<Object> onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType)
        {
            case VIEW_TYPE_HEADER:
                return new TradeHeaderViewHolder(inflater.inflate(R.layout.trade_list_item_header, parent, false));
            case VIEW_TYPE_SUMMARY:
                return new TradeSummaryViewHolder(inflater.inflate(R.layout.trade_list_item_summary, parent, false));
            case VIEW_TYPE_TRADE:
                return new TradeItemViewHolder(inflater.inflate(R.layout.trade_list_item, parent, false));
            default:
                throw new IllegalStateException("unhandled viewType " + viewType);
        }
    }

    private static class TradeListComparator extends TypedRecyclerComparator<Object>
    {
        @Override public int compare(Object o1, Object o2)
        {
            if (o1 instanceof TradeHeaderDisplayDTO && o2 instanceof TradeHeaderDisplayDTO)
            {
                return ((TradeHeaderDisplayDTO) o1).headerType - ((TradeHeaderDisplayDTO) o2).headerType;
            }
            if (o1 instanceof TradeHeaderDisplayDTO)
            {
                if (((TradeHeaderDisplayDTO) o1).headerType == TradeHeaderDisplayDTO.HEADER_TYPE_POSITION)
                {
                    return -1;
                }
                else
                {
                    if (o2 instanceof TradeSummaryDisplayDTO)
                    {
                        return 1;
                    }
                    else
                    {
                        return -1;
                    }
                }
            }
            if (o2 instanceof TradeHeaderDisplayDTO)
            {
                if (((TradeHeaderDisplayDTO) o2).headerType == TradeHeaderDisplayDTO.HEADER_TYPE_POSITION)
                {
                    return 1;
                }
                else
                {
                    if (o1 instanceof TradeSummaryDisplayDTO)
                    {
                        return -1;
                    }
                    else
                    {
                        return 1;
                    }
                }
            }
            if (o1 instanceof TradeSummaryDisplayDTO && o2 instanceof TradeSummaryDisplayDTO)
            {
                return 0;
            }
            if (o1 instanceof TradeSummaryDisplayDTO)
            {
                return -1;
            }
            if (o2 instanceof TradeSummaryDisplayDTO)
            {
                return 1;
            }
            if (o1 instanceof TradeDisplayDTO && o2 instanceof TradeDisplayDTO)
            {
                return ((TradeDisplayDTO) o2).tradeDate.compareTo(((TradeDisplayDTO) o1).tradeDate);
            }
            else
            {
                throw new IllegalStateException("Unhandled");
            }
        }

        @Override public boolean areItemsTheSame(Object item1, Object item2)
        {
            if (item1.getClass().equals(item2.getClass()))
            {
                if (item1 instanceof TradeHeaderDisplayDTO)
                {
                    return ((TradeHeaderDisplayDTO) item1).headerType == ((TradeHeaderDisplayDTO) item2).headerType;
                }
                else if (item1 instanceof TradeSummaryDisplayDTO)
                {
                    return true;
                }
                else if (item1 instanceof TradeDisplayDTO)
                {
                    return ((TradeDisplayDTO) item1).tradeId == ((TradeDisplayDTO) item2).tradeId;
                }
            }
            return false;
        }

        @Override public boolean areContentsTheSame(Object oldItem, Object newItem)
        {
            if (oldItem.getClass().equals(newItem.getClass()))
            {
                if (oldItem instanceof TradeHeaderDisplayDTO)
                {
                    return ((TradeHeaderDisplayDTO) oldItem).headerType == ((TradeHeaderDisplayDTO) newItem).headerType;
                }
                else if (oldItem instanceof TradeSummaryDisplayDTO)
                {
                    TradeSummaryDisplayDTO sOld = (TradeSummaryDisplayDTO) oldItem;
                    TradeSummaryDisplayDTO sNew = (TradeSummaryDisplayDTO) newItem;

                    if (!sOld.plValueHeader.toString().equals(sNew.plValueHeader.toString())) return false;
                    if (!sOld.plValue.toString().equals(sNew.plValue.toString())) return false;
                    if (!sOld.totalInvested.toString().equals(sNew.totalInvested.toString())) return false;
                    if (!sOld.averagePrice.toString().equals(sNew.averagePrice.toString())) return false;
                    return false;
                }
                else if (oldItem instanceof TradeDisplayDTO)
                {
                    return ((TradeDisplayDTO) oldItem).tradeId == ((TradeDisplayDTO) newItem).tradeId;
                }
            }
            return false;
        }
    }

    protected static class TradeSummaryViewHolder extends TypedViewHolder<Object>
    {
        @Bind(R.id.pl_header) TextView plHeader;
        @Bind(R.id.pl_value) TextView plValue;
        @Bind(R.id.total_invested_value) TextView invested;
        @Bind(R.id.average_price_value) TextView avgPrice;

        public TradeSummaryViewHolder(View itemView)
        {
            super(itemView);
        }

        @Override public void onDisplay(Object o)
        {
            if (o instanceof TradeSummaryDisplayDTO)
            {
                TradeSummaryDisplayDTO dto = (TradeSummaryDisplayDTO) o;

                plHeader.setText(dto.plValueHeader);
                plValue.setText(dto.plValue);
                invested.setText(dto.totalInvested);
                avgPrice.setText(dto.averagePrice);
            }
        }
    }

    protected static class TradeHeaderViewHolder extends TypedViewHolder<Object>
    {

        public TradeHeaderViewHolder(View itemView)
        {
            super(itemView);
        }

        @Override public void onDisplay(Object o)
        {
            if (itemView instanceof TextView && o instanceof TradeHeaderDisplayDTO)
            {
                ((TextView) itemView).setText(((TradeHeaderDisplayDTO) o).header);
            }
        }
    }

    protected static class TradeItemViewHolder extends TypedViewHolder<Object>
    {
        @Bind(R.id.trade_action) TextView action;
        @Bind(R.id.trade_pl_value) TextView plValue;
        @Bind(R.id.trade_date_label) TextView date;

        public TradeItemViewHolder(View itemView)
        {
            super(itemView);
        }

        @Override public void onDisplay(Object o)
        {
            if (o instanceof TradeDisplayDTO)
            {
                TradeDisplayDTO dto = (TradeDisplayDTO) o;
                action.setText(dto.mainText);
                plValue.setText(dto.subText);
                date.setText(dto.getTradeDateText());
            }
        }
    }
}
