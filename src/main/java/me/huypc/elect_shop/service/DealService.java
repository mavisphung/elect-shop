package me.huypc.elect_shop.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.huypc.elect_shop.entity.Deal;
import me.huypc.elect_shop.entity.DealProduct;
import me.huypc.elect_shop.entity.Product;
import me.huypc.elect_shop.exception.BadValidationException;
import me.huypc.elect_shop.exception.NotFoundException;
import me.huypc.elect_shop.generated.dto.DealUpdateExpirationForm;
import me.huypc.elect_shop.generated.dto.MultiSelectForm;
import me.huypc.elect_shop.repository.DealProductRepository;
import me.huypc.elect_shop.repository.DealRepository;
import me.huypc.elect_shop.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class DealService {

    private final DealRepository dealRepository;
    private final ProductRepository productRepository;
    private final DealProductRepository dealProductRepository;

    @Transactional
    public void createDealProducts(String dealId, MultiSelectForm selectedProductIds) {
        // Remove duplicates from input
        List<String> uniqueProductIds = selectedProductIds.getSelectedItems().stream()
                .distinct()
                .collect(Collectors.toList());

        // 1. Validate deal exists
        Deal foundDeal = dealRepository.findById(dealId)
                .orElseThrow(() -> new NotFoundException("Deal not found: " + dealId));

        // 2. Constraint 3: Validate all product IDs exist and provide specific missing IDs
        List<Product> foundProducts = productRepository.findAllById(uniqueProductIds);
        if (foundProducts.isEmpty() || foundProducts.size() != uniqueProductIds.size()) {
            Set<String> foundProductIds = foundProducts.stream()
                    .map(Product::getId)
                    .collect(Collectors.toSet());
            
            List<String> missingProductIds = uniqueProductIds.stream()
                    .filter(id -> !foundProductIds.contains(id))
                    .collect(Collectors.toList());
            
            throw new NotFoundException("Products not found with IDs: " + missingProductIds);
        }

        // 3. Constraint 1: Check if products are already in the SAME deal
        List<DealProduct> existingDealProducts = dealProductRepository
                .findAllByDealIdAndProductIdIn(dealId, uniqueProductIds);
        
        if (!existingDealProducts.isEmpty()) {
            List<String> alreadyInSameDealIds = existingDealProducts.stream()
                    .map(dp -> dp.getProduct().getId())
                    .collect(Collectors.toList());
            
            throw new BadValidationException(
                "Products are already in this deal: " + alreadyInSameDealIds
            );
        }

        // 4. Constraint 2: Check if products are already in OTHER deals (prevent stacking)
        List<DealProduct> productsInOtherDeals = dealProductRepository
                .findAllByProductIdIn(uniqueProductIds);
        
        if (!productsInOtherDeals.isEmpty()) {
            List<String> conflictingProducts = productsInOtherDeals.stream()
                    .map(dp -> dp.getProduct().getId() + " (already in deal: " + dp.getDeal().getDealCode() + ")")
                    .collect(Collectors.toList());
            
            throw new BadValidationException(
                "Products are already in other deals. Remove them from existing deals first: " + conflictingProducts
            );
        }

        // 5. Create deal products - no stacking allowed
        foundProducts.forEach(product -> {
            DealProduct dealProduct = DealProduct.builder()
                    .deal(foundDeal)
                    .product(product)
                    .build();
            dealProductRepository.save(dealProduct);
        });
    }

    @Transactional
    public void updateDealExpiration(String id, DealUpdateExpirationForm dealUpdateExpirationForm) {
        Deal foundDeal = dealRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Deal not found: " + id));

        if (dealUpdateExpirationForm.getNewStartAt() == null && dealUpdateExpirationForm.getNewEndAt() == null) {
            return;
        }

        if (dealUpdateExpirationForm.getNewStartAt() != null) {
            foundDeal.setStartAt(dealUpdateExpirationForm.getNewStartAt().toLocalDateTime());
        }
        if (dealUpdateExpirationForm.getNewEndAt() != null) {
            foundDeal.setEndAt(dealUpdateExpirationForm.getNewEndAt().toLocalDateTime());
        }

        dealRepository.save(foundDeal);
    }
}
