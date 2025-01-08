package com.auction.auction_site.dto.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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
