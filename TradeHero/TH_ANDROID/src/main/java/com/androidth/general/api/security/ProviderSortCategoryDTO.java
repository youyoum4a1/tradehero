package com.androidth.general.api.security;

import android.os.Parcel;
import android.os.Parcelable;

import com.androidth.general.common.persistence.DTO;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = ProviderSortCategoryDTO.class
)

public class ProviderSortCategoryDTO implements DTO, Parcelable {

    public Integer resourceId;
    public Integer providerId;
    public String name;
    public String sortOrder;
    public String sortDirection;

    public ProviderSortCategoryDTO()
    {
        super();
    }

    public ProviderSortCategoryDTO(Integer resourceId, Integer providerId, String name, String sortOrder, String sortDirection) {
        super();
        this.resourceId = resourceId;
        this.providerId = providerId;
        this.name = name;
        this.sortOrder = sortOrder;
        this.sortDirection = sortDirection;
    }

    @Override
    public String toString() {
        return "ProviderSortCategoryDTO{" +
                "resourceId=" + resourceId +
                ", providerId=" + providerId +
                ", name='" + name + '\'' +
                ", sortOrder='" + sortOrder + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                '}';
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public String getName() {
        return name;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    /**
     * Parcelable implementations
     * @param other
     */
    private ProviderSortCategoryDTO(Parcel other){
        this.resourceId = other.readInt();
        this.providerId = other.readInt();
        this.name = other.readString();
        this.sortOrder = other.readString();
        this.sortDirection = other.readString();
    }

    public static final Parcelable.Creator<ProviderSortCategoryDTO> CREATOR = new Parcelable.Creator<ProviderSortCategoryDTO>(){
        @Override
        public ProviderSortCategoryDTO createFromParcel(Parcel source) {
            return new ProviderSortCategoryDTO(source);
        }

        @Override
        public ProviderSortCategoryDTO[] newArray(int size) {
            return new ProviderSortCategoryDTO[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.resourceId);
        dest.writeInt(this.providerId);
        dest.writeString(this.name);
        dest.writeString(this.sortOrder);
        dest.writeString(this.getSortDirection());
    }
}
