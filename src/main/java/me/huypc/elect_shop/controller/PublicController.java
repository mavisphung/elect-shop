package me.huypc.elect_shop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.huypc.elect_shop.generated.api.PublicApi;
import me.huypc.elect_shop.generated.dto.ProductFilterForm;
import me.huypc.elect_shop.generated.dto.ProductListDto;
import me.huypc.elect_shop.service.ProductService;

@RestController
@RequiredArgsConstructor
public class PublicController implements PublicApi {

    private final ProductService productService;

    @Override
    public ResponseEntity<ProductListDto> getAllProductsPublic(@Valid ProductFilterForm productFilterForm) {
        ProductListDto productList = productService.getProductsByFilter(productFilterForm);
        return ResponseEntity.ok(productList);
    }

}
