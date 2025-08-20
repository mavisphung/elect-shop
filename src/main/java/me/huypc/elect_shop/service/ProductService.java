package me.huypc.elect_shop.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.huypc.elect_shop.entity.Product;
import me.huypc.elect_shop.exception.NotFoundException;
import me.huypc.elect_shop.generated.dto.ProductItemDto;
import me.huypc.elect_shop.generated.dto.ProductListDto;
import me.huypc.elect_shop.generated.dto.ProductUpsertDto;
import me.huypc.elect_shop.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public void createProduct(ProductUpsertDto productUpsertDto) {
        Product newProduct = Product.builder()
                .name(productUpsertDto.getName())
                .stock(productUpsertDto.getStock())
                .unitPrice(productUpsertDto.getUnitPrice())
                .build();

        productRepository.save(newProduct);
    }

    @Transactional(readOnly = true)
    public ProductListDto getAllProducts(Integer page, Integer size, String searchText) {
        Pageable pageable = PageRequest.of(page - 1, size); // Convert to 0-based index
        Page<Product> productPage = productRepository.findAllWithSearch(searchText, pageable);

        List<ProductItemDto> items = productPage.getContent().stream()
                .map(this::convertToProductItemDto)
                .collect(Collectors.toList());

        return new ProductListDto(
                page,
                size,
                (int) productPage.getTotalElements()
        ).items(items);
    }

    @Transactional
    public void updateProduct(String id, ProductUpsertDto productUpsertDto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));

        existingProduct.setName(productUpsertDto.getName());
        existingProduct.setStock(productUpsertDto.getStock());
        existingProduct.setUnitPrice(productUpsertDto.getUnitPrice());

        productRepository.save(existingProduct);
    }

    @Transactional
    public void deleteProduct(String id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));

        productRepository.delete(existingProduct);
    }

    private ProductItemDto convertToProductItemDto(Product product) {
        return new ProductItemDto(
                product.getId(),
                product.getName(),
                product.getStock(),
                product.getUnitPrice()
        );
    }
}
