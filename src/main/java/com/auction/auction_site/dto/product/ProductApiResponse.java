package com.auction.auction_site.dto.product;

public class ProductApiResponse<T> {

    private String status;
    private String message;
    private T data;

    public ProductApiResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

}
