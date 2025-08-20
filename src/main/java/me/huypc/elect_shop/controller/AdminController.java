package me.huypc.elect_shop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huypc.elect_shop.generated.api.AdminApi;
import me.huypc.elect_shop.generated.dto.ProductListDto;
import me.huypc.elect_shop.generated.dto.ProductUpsertDto;
import me.huypc.elect_shop.service.ProductService;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController implements AdminApi {

    private final ProductService productService;

    @Override
    public ResponseEntity<Void> createNewProduct(@Valid ProductUpsertDto productUpsertDto) {
        productService.createProduct(productUpsertDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> deleteProduct(String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ProductListDto> getAllProducts(@Valid Integer page, @Valid Integer size,
            @Nullable @Valid String searchText) {
        
        // Set default values if not provided
        int pageNumber = (page != null && page > 0) ? page : 1;
        int pageSize = (size != null && size > 0) ? size : 10;
        
        ProductListDto result = productService.getAllProducts(pageNumber, pageSize, searchText);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<Void> updateProduct(String id, @Valid ProductUpsertDto productUpsertDto) {
        productService.updateProduct(id, productUpsertDto);
        return ResponseEntity.noContent().build();
    }

}
