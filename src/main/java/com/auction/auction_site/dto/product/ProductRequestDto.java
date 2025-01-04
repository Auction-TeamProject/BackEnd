package com.auction.auction_site.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class ProductRequestDto {
    private String productName;
    private String productDetail;
    private Long startPrice;
    private Long bidStep;
    private Date auctionEndDate;
}
