package com.auction.auction_site.service;

import com.auction.auction_site.dto.ErrorResponse;
import com.auction.auction_site.dto.product.ProductRequestDto;
import com.auction.auction_site.dto.product.ProductResponseDto;
import com.auction.auction_site.entity.Image;
import com.auction.auction_site.entity.Member;
import com.auction.auction_site.entity.Product;
import com.auction.auction_site.repository.ImageRepository;
import com.auction.auction_site.repository.MemberRepository;
import com.auction.auction_site.repository.ProductRepository;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ImageRepository imageRepository;

  //  @Value("${file.upload-dir}")
    private String uploadDir = "/Users/kimdayeong/intelij/teambackproject/BackEnd/uploads"; // 추후수정


    /**
     * 상품 조회
     */

        public List<ProductResponseDto> getProductsSorted(String sortBy) {
            List<Product> products;

            // 정렬 기준에 따라 Product 리스트 조회
            if ("auctionEndDate".equals(sortBy)) {
                products = productRepository.findAllByAuctionEndDate();
            } else if ("viewCount".equals(sortBy)) {
                products = productRepository.findAllByViewCount();
            } else if ("createdAt".equals(sortBy)) {
                products = productRepository.findAllByCreatedAt();
            } else {
                throw new IllegalArgumentException("Invalid sortBy parameter");
            }

            // Product 리스트를 ProductResponseDto 리스트로 변환
            return products.stream()
                    .map(this::convertToResponseDto) // 변환 메서드 호출
                    .toList();
        }

        // Product -> ProductResponseDto 변환 메서드
        private ProductResponseDto convertToResponseDto(Product product) {
            return new ProductResponseDto(
                    product.getId(),
                    product.getProductName(),
                    product.getProductDetail(),
                    product.getStartPrice(),
                    product.getBidStep(),
                    product.getAuctionEndDate(),
                    product.getProductStatus(),
                    product.getCreatedAt(),
                    product.getUpdatedAt(),
                    product.getImageUrls(), // Product 엔티티에 이미지 URL 리스트가 있어야 함
                    product.getViewCount()
            );
        }
    }

    /**
     * 상품 등록
     */
    public ProductResponseDto createProduct(ProductRequestDto dto, String loginId){


        Member member = memberRepository.findByLoginId(loginId);
        // 유저를 찾을 수 없는 경우
        if (member == null) {
            throw new  IllegalStateException("유효하지 않은 인증 정보입니다.");
        }

        List<Image> images = new ArrayList<>();
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : dto.getProductImage()) {
            try {
                String imageUrl = saveImage(file); // 저장 된 이미지 경로
                imageUrls.add(imageUrl);

                String filePath = uploadDir + file.getOriginalFilename(); //새로운 경로 설정
                Image image = Image.builder()
                        .imageUrl(imageUrl)
                        .originFileName(file.getOriginalFilename())
                        .filePath(filePath)
                        .build();
                images.add(image);

            } catch (IOException e) {
                System.out.println("IOException 발생: " + e.getMessage());
                throw new RuntimeException("이미지 저장에 실패했습니다.", e);
            }
        }

        Product product = Product.builder()
                .productName(dto.getProductName()).productDetail(dto.getProductDetail()).startPrice(dto.getStartPrice())
                .bidStep(dto.getBidStep()).auctionEndDate(dto.getAuctionEndDate()).member(member).viewCount(0)
                .build();
        Product savedProduct = productRepository.save(product);

        // images 리스트 초기화 if needed
        if (product.getImages() == null) {
            product.setImages(new ArrayList<>());
        }

        for (Image image : images) {
            product.addImage(image);
        }

        imageRepository.saveAll(images);


        return ProductResponseDto.builder()
                .id(savedProduct.getId()).productName(savedProduct.getProductName()).productDetail(savedProduct.getProductDetail())
                .startPrice(savedProduct.getStartPrice()).bidStep(savedProduct.getBidStep()).auctionEndDate(savedProduct.getAuctionEndDate())
                .createdAt(savedProduct.getCreatedAt()).updatedAt(savedProduct.getUpdatedAt()).viewCount(savedProduct.getViewCount())
                .productStatus(savedProduct.getProductStatus()).imageUrls(imageUrls).build();
    }

    /**
     * 상품 이미지 저장 로직
     */
    private String saveImage(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + extension;

        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        if (!dir.canWrite()) {
            throw new IOException("Directory does not have write permissions");
        }

        String filePath = uploadDir +"/"+ newFileName;
        file.transferTo(new File(filePath));

        return filePath;
    }



    /**
     * 상품 상세 조회
     */
    public ProductResponseDto productDetail(Long id) {
        Product product = productRepository.findById(id).orElseThrow(()->new BadCredentialsException("상품 정보를 찾을 수 없습니다."));
        return ProductResponseDto.builder()
                .id(product.getId()).productName(product.getProductName()).productDetail(product.getProductDetail())
                .startPrice(product.getStartPrice()).bidStep(product.getBidStep()).auctionEndDate(product.getAuctionEndDate())
                .createdAt(product.getCreatedAt()).updatedAt(product.getUpdatedAt()).viewCount(product.getViewCount())
                .productStatus(product.getProductStatus()).imageUrls(product.getImages().stream()
                        .map(Image::getImageUrl)
                        .collect(Collectors.toList())).build();
    }

    /**
     * 상품 수정
     */
    public ResponseEntity<?> productUpdate(Long id, String loginId, ProductRequestDto dto){
        ErrorResponse errorResponse = new ErrorResponse();
        Member member = memberRepository.findByLoginId(loginId);
        // 유저를 찾을 수 없는 경우
        if (loginId == null || loginId.trim().isEmpty()) {
            errorResponse.setStatus("FAIL");
            errorResponse.setMessage("유효하지 않은 인증 정보입니다.");
            errorResponse.setCode("UNAUTHORIZED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        // 상품 찾기
        Optional<Product> productOptional = productRepository.findById(id);
        if (!productOptional.isPresent()) {
            errorResponse.setStatus("FAIL");
            errorResponse.setMessage("해당 상품을 찾을 수 없습니다.");
            errorResponse.setCode("NOT_FOUND");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        Product product = productOptional.get();

        // 유저와 상품의 소유자가 동일한지 체크
        if (!product.getMember().getId().equals(member.getId())) {
            errorResponse.setStatus("FAIL");
            errorResponse.setMessage("해당 상품을 수정할 권한이 없습니다.");
            errorResponse.setCode("FORBIDDEN");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
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

        List<Image> currentImages = product.getImages();
        List<Image> newImages = new ArrayList<>();
        List<String> imageUrls = new ArrayList<>();
        if (dto.getProductImage() != null && !dto.getProductImage().isEmpty()) {

            for (MultipartFile file : dto.getProductImage()) {
                try {
                    String imageUrl = saveImage(file); // 이미지 저장 경로
                    imageUrls.add(imageUrl);

                    Image image = Image.builder()
                            .imageUrl(imageUrl)
                            .originFileName(file.getOriginalFilename())
                            .filePath(uploadDir + "/" + file.getOriginalFilename())
                            .product(product)
                            .build();
                    newImages.add(image);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ErrorResponse("FAIL", "Internal Error", "상품 이미지 저장 중 오류 발생: " + e.getMessage()));
                }
            }
        }
        currentImages.clear();
        for(Image image : newImages){
            currentImages.add(image);
        }
        product.setImages(currentImages);
        Product updatedProduct = productRepository.save(product);

        ProductResponseDto responseDto = ProductResponseDto.builder()
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
                .imageUrls(imageUrls)
                .build();

        return ResponseEntity.ok(responseDto);
    }

    /**
     * 상품 삭제
     */
    public ResponseEntity<?> deleteProduct(Long id, String loginId) {
        // 회원 정보 확인
        Member member = memberRepository.findByLoginId(loginId);
        if (loginId == null || loginId.trim().isEmpty() || member == null) {
            throw new IllegalStateException("유효하지 않은 인증 정보입니다.");
        }

        Optional<Product> productOptional = productRepository.findById(id);

        ErrorResponse errorResponse = new ErrorResponse();
        // 상품 확인
        if (!productOptional.isPresent()) {
            errorResponse.setStatus("FAIL");
            errorResponse.setMessage("해당 상품을 찾을 수없습니다.");
            errorResponse.setCode("NOT_FOUND");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        }

        Product product = productOptional.get();

        // 상품 소유자 확인
        if (!product.getMember().getId().equals(member.getId())) {
            errorResponse.setStatus("FAIL");
            errorResponse.setMessage("상품 삭제 권한이 없습니다.");
            errorResponse.setCode("UNAUTHORIZED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }


        // 상품 삭제
        productRepository.delete(product);
        errorResponse.setStatus("SUCCESS");
        errorResponse.setMessage("상품이 삭제되었습니다.");

        // 성공 응답 반환
        return ResponseEntity.ok().body(errorResponse);
    }


}
