package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THProductDetail;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

abstract public class ProductDetailAdapter<
        ProductIdentifierType extends ProductIdentifier,
        THProductDetailType extends THProductDetail<ProductIdentifierType>,
        ProductDetailViewType extends ProductDetailView<ProductIdentifierType, THProductDetailType>>
        extends ArrayDTOAdapter<THProductDetailType, ProductDetailViewType>
{
    public static final int ITEM_TYPE_HEADER = 0;
    public static final int ITEM_TYPE_VALUE = 1;

    protected Comparator<THProductDetailType> productDetailComparator;
    protected Map<ProductIdentifier, Boolean> enabledItems;
    protected ProductIdentifierDomain skuDomain;

    //<editor-fold desc="Constructors">
    public ProductDetailAdapter(Context context, LayoutInflater inflater, int layoutResourceId, ProductIdentifierDomain skuDomain)
    {
        super(context, inflater, layoutResourceId);
        this.skuDomain = skuDomain;
    }
    //</editor-fold>

    public Comparator<THProductDetailType> getProductDetailComparator()
    {
        return productDetailComparator;
    }

    public void setProductDetailComparator(Comparator<THProductDetailType> productDetailComparator)
    {
        this.productDetailComparator = productDetailComparator;
    }

    public void setEnabledItems(Map<ProductIdentifier, Boolean> enabledItems)
    {
        this.enabledItems = enabledItems;
    }

    @Override public void setItems(List<THProductDetailType> items)
    {
        if (productDetailComparator == null || items == null)
        {
            super.setItems(items);
        }
        else
        {
            TreeSet<THProductDetailType> sorted = new TreeSet<>(productDetailComparator);
            sorted.addAll(items);
            super.setItems(new ArrayList<>(sorted));
        }
    }

    @Override public int getViewTypeCount()
    {
        return 2;
    }

    @Override public int getItemViewType(int position)
    {
        switch (position)
        {
            case 0:
                return ITEM_TYPE_HEADER;
            default:
                return ITEM_TYPE_VALUE;
        }
    }

    @Override public int getCount()
    {
        return super.getCount() + 1;
    }

    @Override public Object getItem(int i)
    {
        return getItemViewType(i) == ITEM_TYPE_HEADER ? null : super.getItem(i - 1);
    }

    protected View getHeaderView(int position, View convertView, ViewGroup viewGroup)
    {
        ProductDetailQuickDescriptionView
                quickDescription = convertView instanceof ProductDetailQuickDescriptionView ?
                (ProductDetailQuickDescriptionView) convertView :
                (ProductDetailQuickDescriptionView) inflater.inflate(R.layout.store_quick_message, viewGroup, false);
        quickDescription.linkWithProductDomain(skuDomain, true);
        return quickDescription;
    }

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        View view = getItemViewType(position) == ITEM_TYPE_HEADER ?
                getHeaderView(position, convertView, viewGroup) :
                super.getView(position, convertView instanceof ProductDetailQuickDescriptionView ? null : convertView, viewGroup);
        view.setEnabled(isEnabled(position));
        return view;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        if (getItemViewType(position) == ITEM_TYPE_HEADER)
        {
            return false;
        }

        if (enabledItems == null)
        {
            return true;
        }

        Object item = getItem(position);
        if (item == null || !(item instanceof ProductDetail))
        {
            return true;
        }

        ProductIdentifier id = ((ProductDetail) item).getProductIdentifier();
        if (id == null)
        {
            return true;
        }
        Boolean status = enabledItems.get(id);
        return status == null || status;
    }

    public ProductIdentifierDomain getSkuDomain()
    {
        return skuDomain;
    }

    public void setSkuDomain(ProductIdentifierDomain skuDomain)
    {
        this.skuDomain = skuDomain;
    }
}
