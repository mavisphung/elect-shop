package me.huypc.elect_shop.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.huypc.elect_shop.entity.Category;
import me.huypc.elect_shop.entity.Inventory;
import me.huypc.elect_shop.entity.Product;
import me.huypc.elect_shop.exception.NotFoundException;
import me.huypc.elect_shop.generated.dto.ProductFilterForm;
import me.huypc.elect_shop.generated.dto.ProductItemDto;
import me.huypc.elect_shop.generated.dto.ProductListDto;
import me.huypc.elect_shop.generated.dto.ProductUpsertForm;
import me.huypc.elect_shop.repository.InventoryRepository;
import me.huypc.elect_shop.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public void createProduct(ProductUpsertForm productUpsertForm) {
        Product newProduct = Product.builder()
                .name(productUpsertForm.getName())
                .unitPrice(productUpsertForm.getUnitPrice())
                .category(Category.builder().id(productUpsertForm.getCategoryId()).build())
                .build();

        newProduct = productRepository.save(newProduct);

        Inventory inventory = Inventory.builder()
                .onHand(productUpsertForm.getStock())
                .product(newProduct)
                .reserved(0)
                .build();
        inventoryRepository.save(inventory);
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
    public void updateProduct(String id, ProductUpsertForm productUpsertForm) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));

        existingProduct.setName(productUpsertForm.getName());
        existingProduct.setCategory(Category.builder().id(productUpsertForm.getCategoryId()).build());
        existingProduct.getInventory().setOnHand(productUpsertForm.getStock());
        existingProduct.setUnitPrice(productUpsertForm.getUnitPrice());

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
                product.getInventory().getOnHand(),
                product.getUnitPrice()
        );
    }

    @Transactional(readOnly = true)
    public ProductListDto getProductsByFilter(ProductFilterForm productFilterForm) {
        // TODO: Add sort
        Pageable pageable = PageRequest.of(productFilterForm.getPage() - 1, productFilterForm.getSize());
        Page<Product> productPage = productRepository.findAllByFilter(
                productFilterForm.getSearchText(),
                productFilterForm.getCategory(),
                productFilterForm.getMinPrice(),
                productFilterForm.getMaxPrice(),
                productFilterForm.getIsAvailable(),
                pageable
        );

        List<ProductItemDto> items = productPage.getContent().stream()
                .map(this::convertToProductItemDto)
                .collect(Collectors.toList());

        return new ProductListDto(
                productFilterForm.getPage(),
                productFilterForm.getSize(),
                (int) productPage.getTotalElements()
        ).items(items);
    }
}
