package me.huypc.elect_shop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huypc.elect_shop.generated.api.AdminApi;
import me.huypc.elect_shop.generated.dto.DealUpdateExpirationForm;
import me.huypc.elect_shop.generated.dto.MultiSelectForm;
import me.huypc.elect_shop.generated.dto.ProductListDto;
import me.huypc.elect_shop.generated.dto.ProductUpsertForm;
import me.huypc.elect_shop.service.DealService;
import me.huypc.elect_shop.service.ProductService;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController implements AdminApi {

    private final ProductService productService;
    private final DealService dealService;

    @Override
    public ResponseEntity<Void> createNewProduct(@Valid ProductUpsertForm productUpsertForm) {
        productService.createProduct(productUpsertForm);
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
    public ResponseEntity<Void> updateProduct(String id, @Valid ProductUpsertForm productUpsertForm) {
        productService.updateProduct(id, productUpsertForm);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> createDealProducts(String id, @Valid MultiSelectForm multiSelectForm) {
        dealService.createDealProducts(id, multiSelectForm);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> updateDealExpiration(String id,
            @Valid DealUpdateExpirationForm dealUpdateExpirationForm) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateDealExpiration'");
    }

}
