package com.auction.auction_site.controller;


import com.auction.auction_site.dto.ErrorResponse;
import com.auction.auction_site.dto.product.ProductApiResponse;
import com.auction.auction_site.dto.product.ProductRequestDto;
import com.auction.auction_site.dto.product.ProductResponseDto;
import com.auction.auction_site.security.oauth.CustomOAuth2User;
import com.auction.auction_site.security.spring_security.CustomUserDetails;
import com.auction.auction_site.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;


    /**
     * 상품 등록
     */
    @PostMapping
    public ResponseEntity<ProductApiResponse> createProduct(@RequestBody ProductRequestDto productRequestDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
     //   System.out.println("Principal Type: " + principal.getClass().getName());
        String username = ((CustomOAuth2User) principal).getUsername();

        if (username == null || username.trim().isEmpty()) { // username이 없을때
            ErrorResponse errorResponse = new ErrorResponse("BAD_REQUEST", "Username is missing or invalid");
            return ResponseEntity.badRequest().body(new ProductApiResponse<>("error", errorResponse.getMessage(), errorResponse));
        }
        ProductResponseDto createdProduct = productService.createProduct(productRequestDto, username);

        // 응답 생성
        ProductApiResponse<ProductResponseDto> response = new ProductApiResponse<>(
                "success",
                "상품이 성공적으로 생성되었습니다.",
                createdProduct
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }


/**
 * 상품 상세보기
 */
@GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto>ProductDetail(@PathVariable Long id){

    ProductResponseDto productDetail = productService.productDetail(id);
    return ResponseEntity.status(HttpStatus.OK).body(productDetail);
}



/**
 상품 수정
 */




/**
 * 상품 삭제
 */


}
