package com.androidth.general.api.kyc.ayondo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.utils.StringUtils;
import java.util.ArrayList;

public class AyondoLeveragedProductList implements DTO
{
    @JsonIgnore public ArrayList<AyondoLeveragedProduct> products = new ArrayList<>(3);

    public AyondoLeveragedProductList()
    {
        super();
    }

    @JsonCreator public static AyondoLeveragedProductList createAyondoLeveragedProductList(@Nullable String concatenated)
    {
        AyondoLeveragedProductList ayondoLeveragedProducts = new AyondoLeveragedProductList();
        if (concatenated != null)
        {
            String[] split = concatenated.split(",");
            for (String candidate : split)
            {
                ayondoLeveragedProducts.add(AyondoLeveragedProduct.getLeveragedProduct(candidate));
            }
        }
        return ayondoLeveragedProducts;
    }

    public void add(AyondoLeveragedProduct leveragedProduct)
    {
        products.add(leveragedProduct);
    }

    @JsonValue @NonNull @Override public String toString()
    {
        //noinspection ConstantConditions
        return StringUtils.join(",", products);
    }

    public boolean contains(AyondoLeveragedProduct exchangeTradedDerivative)
    {
        return products.contains(exchangeTradedDerivative);
    }

    public void remove(AyondoLeveragedProduct product)
    {
        products.remove(product);
    }

    public int size()
    {
        return products.size();
    }

    public AyondoLeveragedProduct get(int index)
    {
        return products.get(index);
    }

    public boolean isEmpty()
    {
        return products.isEmpty();
    }
}
