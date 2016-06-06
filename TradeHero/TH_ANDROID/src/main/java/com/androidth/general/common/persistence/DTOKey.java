package com.androidth.general.common.persistence;

public interface DTOKey extends DTO
{
    // DTOKeys should carefully write these methods to work with Map and LruCache as intended
    /**
     * To understand their meaning, refer to ObjectLearningTest.java
     */
    int hashCode();
    boolean equals(Object other);
}
