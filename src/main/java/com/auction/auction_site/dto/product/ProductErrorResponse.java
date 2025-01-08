package com.auction.auction_site.dto.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class ProductErrorResponse {
    private String code;
    private String message;
    private String status;
}
