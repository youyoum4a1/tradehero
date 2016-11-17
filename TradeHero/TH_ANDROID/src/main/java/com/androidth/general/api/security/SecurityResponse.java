package com.androidth.general.api.security;

/**
 * Created by diana on 17/11/16.
 */
// created because json response of https://www.tradehero.mobi/api/securities/700015583 for example
// has a parent 'security'
public class SecurityResponse {
    public SecurityCompactDTO security;
}
