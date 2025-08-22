package me.huypc.elect_shop.service;

import java.util.List;

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
        Deal foundDeal = dealRepository.findById(dealId)
                .orElseThrow(() -> new NotFoundException("Deal not found: " + dealId));

        // find the deal_products first
        List<DealProduct> foundDealProducts = dealProductRepository.findAllByDealIdAndProductIdIn(dealId, selectedProductIds.getSelectedItems());
        if (foundDealProducts.size() > 0) {
            throw new BadValidationException("Some products are already added to the deal");
        }

        List<Product> foundProducts = productRepository.findAllById(selectedProductIds.getSelectedItems());

        if (foundProducts.isEmpty() || foundProducts.size() != selectedProductIds.getSelectedItems().size()) {
            throw new NotFoundException("Some products not found for the given IDs");
        }

        foundProducts.forEach(product -> {
            DealProduct dealProduct = new DealProduct();
            dealProduct.setDeal(foundDeal);
            dealProduct.setProduct(product);
            Double discountPrice = calculateDiscountPrice(foundDeal, product);
            dealProduct.setDiscountPrice(discountPrice);
            dealProductRepository.save(dealProduct);
        });
    }

    private Double calculateDiscountPrice(Deal deal, Product product) {
        switch (deal.getDiscountType()) {
            case PERCENTAGE:
                return product.getUnitPrice() * (1 - deal.getDiscountAmount() / 100);
            case AMOUNT:
                return product.getUnitPrice() - deal.getDiscountAmount();
            default:
                return null;
        }
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
