package com.auction.auction_site.exception;

public class UnauthorizedAccess extends RuntimeException{
    public UnauthorizedAccess(String message) {
        super(message);
    }
}
