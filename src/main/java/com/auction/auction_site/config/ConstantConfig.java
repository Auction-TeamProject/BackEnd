package com.auction.auction_site.config;

public class ConstantConfig {
   // public static final Long ACCESS_EXPIRED_MS = 5 * 60 * 1000L;
    public static final Long ACCESS_EXPIRED_MS = 5 * 60 * 60 * 1000L; //5시간
    public static final Long REFRESH_EXPIRED_MS = 24 * 60 * 60 * 100L;
    public static final int COOKIE_MAX_AGE = 24 * 60 * 60;
}