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

import java.util.List;
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

        String username = ((CustomOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
     //   System.out.println("Principal Type: " + principal.getClass().getName());
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ProductApiResponse<>("error", "Username is missing or invalid", new ErrorResponse("BAD_REQUEST", "Username is missing or invalid")));
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
     * 전체 상품 상세보기
     */

    @GetMapping
    public List<ProductResponseDto> ProductList(){
        return productService.productList();
    }


/**
 * 특정 상품 상세보기
 */
@GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto>ProductDetail(@PathVariable Long id){

    ProductResponseDto productDetail = productService.productDetail(id);
    return ResponseEntity.status(HttpStatus.OK).body(productDetail);
}


/**
 상품 수정
 */
@PatchMapping("/{id}")
public ResponseEntity<ProductApiResponse>productUpdate(@PathVariable Long id, @RequestBody ProductRequestDto dto){
    String username = ((CustomOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    if (username == null || username.trim().isEmpty()) {
        return ResponseEntity.badRequest().body(new ProductApiResponse<>("error", "Username is missing or invalid", new ErrorResponse("BAD_REQUEST", "Username is missing or invalid")));
    }
    ProductResponseDto updatedProduct = productService.productUpdate(id, username, dto);
    // 응답 생성
    ProductApiResponse<ProductResponseDto> response = new ProductApiResponse<>(
            "success",
            "상품이 성공적으로 수정되었습니다.",
            updatedProduct
    );

    return ResponseEntity.status(HttpStatus.CREATED).body(response);

}


/**
 * 상품 삭제
 */
@DeleteMapping("/{id}")
    public ResponseEntity<ProductApiResponse>productDelete(@PathVariable Long id){

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof CustomOAuth2User)) {
        throw new IllegalStateException("유효하지 않은 인증 정보입니다.");
    }
    String username = ((CustomOAuth2User) authentication.getPrincipal()).getUsername();


    ProductApiResponse<Void> response = productService.deleteProduct(id, username);
    // 응답 처리
    if ("SUCCESS".equals(response.getStatus())) {
        return ResponseEntity.ok(response); // 삭제 성공
    } else {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // 삭제 권한 없음
    }
}

}


