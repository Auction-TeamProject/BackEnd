package com.auction.auction_site.service;

import com.auction.auction_site.dto.ErrorResponse;
import com.auction.auction_site.dto.product.ProductRequestDto;
import com.auction.auction_site.dto.product.ProductResponseDto;
import com.auction.auction_site.entity.Product;
import com.auction.auction_site.entity.User;
import com.auction.auction_site.exception.EntityNotFound;
import com.auction.auction_site.repository.ProductRepository;
import com.auction.auction_site.repository.UserRepository;
import com.auction.auction_site.security.spring_security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * 상품 등록
     */
    public ProductResponseDto createProduct(ProductRequestDto dto, String username){

        User user = userRepository.findByUsername(username);
        // 유저를 찾을 수 없는 경우
        if (user == null) {
            throw new EntityNotFound("유저명: " + username + " 을 찾을 수 없습니다.");
        }
        Product product = Product.builder()
                .productName(dto.getProductName()).productDetail(dto.getProductDetail()).startPrice(dto.getStartPrice())
                .bidStep(dto.getBidStep()).auctionEndDate(dto.getAuctionEndDate()).user(user)
                .build();

        Product savedProduct = productRepository.save(product);

        return ProductResponseDto.builder()
                .id(savedProduct.getId()).productName(savedProduct.getProductName()).productDetail(savedProduct.getProductDetail())
                .startPrice(savedProduct.getStartPrice()).bidStep(savedProduct.getBidStep()).auctionEndDate(savedProduct.getAuctionEndDate())
                .createdAt(savedProduct.getCreatedAt()).updatedAt(savedProduct.getUpdatedAt()).viewCount(savedProduct.getViewCount())
                .productStatus(savedProduct.getProductStatus()).build();
    }

    /**
     * 상품 상세
     */
    public ProductResponseDto productDetail(Long id) {
        Product product = productRepository.findById(id).orElseThrow(()->new BadCredentialsException("상품 정보를 찾을 수 없습니다."));
        return ProductResponseDto.builder()
                .id(product.getId()).productName(product.getProductName()).productDetail(product.getProductDetail())
                .startPrice(product.getStartPrice()).bidStep(product.getBidStep()).auctionEndDate(product.getAuctionEndDate())
                .createdAt(product.getCreatedAt()).updatedAt(product.getUpdatedAt()).viewCount(product.getViewCount())
                .productStatus(product.getProductStatus()).build();
    }

}
