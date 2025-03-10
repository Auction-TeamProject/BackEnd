package com.auction.auction_site.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ID")
    private Long id;

    @Column(length = 60, nullable = false)
    private String productName;
    @Column(length = 1000, nullable = false)
    private String productDetail;
    private Long startPrice;
    private Long bidStep;
    private LocalDateTime auctionEndDate;
    @Builder.Default
    private Boolean productStatus = true;
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
    private int viewCount;

    // @Column(nullable = false)
    private String thumbnailPath;

   // @Column(nullable = false)
    private String thumbnailUrl;

    // 상품을 저장할 때 경매도 자동 저장되도록 CascadeType.PERSIST 설정
    @OneToOne(mappedBy = "product", cascade = CascadeType.PERSIST)
    private Auction auction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    public void associateWithAuction(Auction auction) {
        this.auction = auction;
    }

    public void addImage(Image image) {
        images.add(image);
        image.setProduct(this);
    }

    public void removeImage(Image image) {
        images.remove(image);
        image.setProduct(null);
    }
}
