package com.auction.auction_site.service;

import com.auction.auction_site.dto.ErrorResponse;
import com.auction.auction_site.dto.product.ProductApiResponse;
import com.auction.auction_site.dto.product.ProductRequestDto;
import com.auction.auction_site.dto.product.ProductResponseDto;
import com.auction.auction_site.entity.Product;
import com.auction.auction_site.entity.User;
import com.auction.auction_site.exception.EntityNotFound;
import com.auction.auction_site.exception.UnauthorizedAccess;
import com.auction.auction_site.repository.ProductRepository;
import com.auction.auction_site.repository.UserRepository;
import com.auction.auction_site.security.spring_security.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            throw new  IllegalStateException("유효하지 않은 인증 정보입니다.");
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
     * 전체 상품 리스트
     */
    public List<ProductResponseDto> productList() {
        return productRepository.findAll().stream()
                .map(product -> new ProductResponseDto(
                        product.getId(),
                        product.getProductName(),
                        product.getProductDetail(),
                        product.getStartPrice(),
                        product.getBidStep(),
                        product.getAuctionEndDate(),
                        product.getProductStatus(),
                        product.getCreatedAt(),
                        product.getUpdatedAt(),
                        product.getViewCount()
                )).collect(Collectors.toList());
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

    /**
     * 상품 수정
     */
    public ProductResponseDto productUpdate(Long id, String username,ProductRequestDto dto){
        User user = userRepository.findByUsername(username);
        // 유저를 찾을 수 없는 경우
        if (username == null || username.trim().isEmpty()) {
             throw new  IllegalStateException("유효하지 않은 인증 정보입니다.");
        }

        // 상품 찾기
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

// 유저와 상품의 소유자가 동일한지 체크
        if (!product.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccess("해당 상품을 수정할 권한이 없습니다.");
        }

        // 수정할 데이터 부분만 업데이트
        if (dto.getProductName() != null) {
            product.setProductName(dto.getProductName());
        }
        if (dto.getProductDetail() != null) {
            product.setProductDetail(dto.getProductDetail());
        }
        if (dto.getStartPrice() != null) {
            product.setStartPrice(dto.getStartPrice());
        }
        if (dto.getBidStep() != null) {
            product.setBidStep(dto.getBidStep());
        }
        if (dto.getAuctionEndDate() != null) {
            product.setAuctionEndDate(dto.getAuctionEndDate());
        }

        Product updatedProduct = productRepository.save(product);
        return ProductResponseDto.builder()
                .id(updatedProduct.getId())
                .productName(updatedProduct.getProductName())
                .productDetail(updatedProduct.getProductDetail())
                .startPrice(updatedProduct.getStartPrice())
                .bidStep(updatedProduct.getBidStep())
                .auctionEndDate(updatedProduct.getAuctionEndDate())
                .createdAt(updatedProduct.getCreatedAt())
                .updatedAt(updatedProduct.getUpdatedAt())
                .viewCount(updatedProduct.getViewCount())
                .productStatus(updatedProduct.getProductStatus())
                .build();
    }

    /**
     * 상품 삭제
     */
    public ProductApiResponse deleteProduct(Long id, String username) {

        User user = userRepository.findByUsername(username);
        // 유저를 찾을 수 없는 경우
        if (username == null || username.trim().isEmpty()) {
             throw new  IllegalStateException("유효하지 않은 인증 정보입니다.");
        }
        // 상품 정보 확인
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

        // 상품 소유자 확인
        if (!product.getUser().getId().equals(user.getId())) {
            return new ProductApiResponse<>("FAIL", "상품 삭제 권한이 없습니다.", null);
        }

        // 상품 삭제
        productRepository.delete(product);
        return new ProductApiResponse<>("SUCCESS", "상품이 성공적으로 삭제되었습니다.", null);

    }
    }


