package com.tradehero.common.persistence;


public interface DTOKey
{
    // DTOKeys should carefully write these methods to work with Map and LruCache as intended
    /**
     * To understand their meaning, refer to ObjectLearningTest.java
     */
    int hashCode();
    boolean equals(Object other);
}
