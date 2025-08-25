package me.huypc.elect_shop.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.huypc.elect_shop.entity.Cart;
import me.huypc.elect_shop.entity.CartItem;
import me.huypc.elect_shop.entity.Deal;
import me.huypc.elect_shop.entity.DealProduct;
import me.huypc.elect_shop.entity.Inventory;
import me.huypc.elect_shop.entity.Order;
import me.huypc.elect_shop.entity.OrderDetail;
import me.huypc.elect_shop.entity.User;
import me.huypc.elect_shop.exception.ConflictResourceException;
import me.huypc.elect_shop.exception.NotFoundException;
import me.huypc.elect_shop.generated.dto.CartItemUpsertForm;
import me.huypc.elect_shop.generated.dto.OrderPlaceForm;
import me.huypc.elect_shop.repository.CartRepository;
import me.huypc.elect_shop.repository.DealProductRepository;
import me.huypc.elect_shop.repository.InventoryRepository;
import me.huypc.elect_shop.repository.OrderRepository;
import me.huypc.elect_shop.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CartService {

    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final CartRepository cartRepository;
    private final DealProductRepository dealProductRepository;
    private final OrderRepository orderRepository;

    private Double calculateEffectivePrice(Deal deal, Integer buyQuantity, Double originalPrice) {
        switch (deal.getDealType()) {
            case SIMPLE_DISCOUNT -> {
                return calculateSimpleDiscount(deal, originalPrice);
            }
            // TODO: implement BULK_DISCOUNT if have time
            default -> {
                return originalPrice;
            }
        }
    }

    private Double calculateSimpleDiscount(Deal deal, Double originalPrice) {
        switch (deal.getDiscountType()) {
            case PERCENTAGE -> {
                return originalPrice - (originalPrice * deal.getDiscountAmount() / 100);
            }
            case AMOUNT -> {
                return originalPrice - deal.getDiscountAmount();
            }
            default -> {
                return originalPrice;
            }
        }
    }

    @Transactional
    public void addToCart(User currentUser, CartItemUpsertForm cartItemUpsertForm) {

        Inventory inventory = inventoryRepository.findByProductIdForUpdate(cartItemUpsertForm.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (inventory.getAvailable() < cartItemUpsertForm.getQuantity()) {
            throw new ConflictResourceException("Not enough product available");
        }

        // get the cart
        Cart cart = cartRepository.findById(currentUser.getId())
                .orElseGet(() -> Cart.builder()
                        .userId(currentUser.getId())
                        .user(currentUser)
                        .build());

        Double effectivePrice, originalPrice = inventory.getProduct().getUnitPrice();
        Deal appliedDeal = null; // Track the applied deal
        Optional<DealProduct> dealProductOpt = dealProductRepository.findByProductId(cartItemUpsertForm.getProductId());

        if (dealProductOpt.isPresent()) {
            // Process the applied deal
            DealProduct dealProduct = dealProductOpt.get();
            appliedDeal = dealProduct.getDeal();

            // Check if the deal is active
            if (!appliedDeal.isActive()) {
                throw new ConflictResourceException("Deal is not active");
            }

            // Apply deal pricing - only one deal applied for a product at a time
            effectivePrice = calculateEffectivePrice(appliedDeal, cartItemUpsertForm.getQuantity(), originalPrice);
        } else {
            // No deal found, use original price
            effectivePrice = originalPrice;
        }

        // check if the product is already in the cart
        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(cartItemUpsertForm.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Update quantity if already in cart
            existingItem.setQuantity(existingItem.getQuantity() + cartItemUpsertForm.getQuantity());
        } else {
            // Add new item to cart
            CartItem newItem = CartItem.builder()
                    .product(inventory.getProduct())
                    .deal(appliedDeal) // Use the applied deal (can be null)
                    .quantity(cartItemUpsertForm.getQuantity())
                    .unitPrice(effectivePrice)
                    .cart(cart)
                    .build();
            cart.getCartItems().add(newItem);
        }

        inventory.setReserved(inventory.getReserved() + cartItemUpsertForm.getQuantity());
        inventoryRepository.save(inventory);
        // Save the cart
        currentUser.setCart(cart);
        userRepository.save(currentUser);
    }

    @Transactional
    public void removeFromCart(User currentUser, CartItemUpsertForm cartItemUpsertForm) {

        // Get the cart
        Cart cart = cartRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // Find the item in the cart
        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(cartItemUpsertForm.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem == null) {
            throw new ConflictResourceException("Item not found in cart");
        }

        // reduce quantity or remove item from cart
        if (existingItem.getQuantity() > cartItemUpsertForm.getQuantity()) {
            existingItem.setQuantity(existingItem.getQuantity() - cartItemUpsertForm.getQuantity());
        } else {
            cart.getCartItems().remove(existingItem);
        }

        // Update inventory
        Inventory inventory = inventoryRepository.findByProductIdForUpdate(cartItemUpsertForm.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        inventory.setReserved(inventory.getReserved() - cartItemUpsertForm.getQuantity());
        inventoryRepository.save(inventory);

        // Save the cart
        currentUser.setCart(cart);
        userRepository.save(currentUser);
    }

    @Transactional
    public void placeOrder(User currentUser, OrderPlaceForm orderPlaceForm) {
        // get the cart
        Cart cart = currentUser.getCart();
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new ConflictResourceException("Cart is empty");
        }
        // cache the cart item
        List<CartItem> cartItems = new ArrayList<>(cart.getCartItems());
        // create order and order_details, this won't be affected by the deal due to the
        // way deals are applied at the cart level
        Order order = Order.builder()
                .user(currentUser)
                .totalPrice(calculateTotalPriceForOrder(cartItems))
                .shippingAddress(orderPlaceForm.getShippingAddress())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(Order.OrderStatus.PLACED)
                .build();

        List<OrderDetail> orderDetails = new ArrayList<>();
        // create order details
        for (CartItem cartItem : cartItems) {
            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .build();
            orderDetails.add(orderDetail);
        }
        order.setOrderDetails(orderDetails);

        orderRepository.save(order);

        // clear the cart
        cart.getCartItems().clear();
        cartRepository.save(cart);

        // reduce reserved and onHand inventory of those products
        handleInventoryAfterOrder(cartItems);

    }

    private Double calculateTotalPriceForOrder(List<CartItem> cartItems) {
        return cartItems.stream()
                .mapToDouble(this::calculateItemTotalPrice)
                .sum();
    }

    /**
     * Calculate the total price for a cart item considering special deal types
     */
    private Double calculateItemTotalPrice(CartItem cartItem) {
        Deal appliedDeal = cartItem.getDeal();
        Integer quantity = cartItem.getQuantity();
        Double unitPrice = cartItem.getUnitPrice();

        // If no deal applied, use simple multiplication
        if (appliedDeal == null) {
            return quantity * unitPrice;
        }

        // Handle different deal types for final price calculation
        switch (appliedDeal.getDealType()) {
            case BUY_ONE_GET_ONE:
                return calculateBogoPrice(quantity, unitPrice);

            case BUY_X_GET_Y_OFF:
                return calculateBuyXGetYOffFinalPrice(appliedDeal, quantity, cartItem.getProduct().getUnitPrice());

            case SIMPLE_DISCOUNT:
                // For simple discounts, the unitPrice already has discount applied
                return quantity * unitPrice;

            default:
                return quantity * unitPrice;
        }
    }

    /**
     * Calculate BOGO (Buy One Get One) pricing
     * Customer pays for ceil(quantity/2) items only
     */
    private Double calculateBogoPrice(Integer quantity, Double unitPrice) {
        int paidItems = (int) Math.ceil(quantity / 2.0);
        return paidItems * unitPrice;
    }

    /**
     * Calculate final price for "Buy X Get Y Off" deals
     * Example: Buy 2 get 30% off next 1
     */
    private Double calculateBuyXGetYOffFinalPrice(Deal deal, Integer totalQuantity, Double originalPrice) {
        Integer buyQuantity = deal.getBuyQuantity() != null ? deal.getBuyQuantity() : 1;
        Integer getQuantity = deal.getGetQuantity() != null ? deal.getGetQuantity() : 1;
        Double discountPercentage = deal.getDiscountAmount() != null ? deal.getDiscountAmount() : 0.0;

        if (totalQuantity <= buyQuantity) {
            // Not enough items to trigger the deal
            return totalQuantity * originalPrice;
        }

        // Calculate how many complete deal cycles we have
        int dealCycles = totalQuantity / (buyQuantity + getQuantity);
        int remainingItems = totalQuantity % (buyQuantity + getQuantity);

        double totalCost = 0.0;

        // Calculate cost for complete deal cycles
        for (int i = 0; i < dealCycles; i++) {
            // Pay full price for 'buy' items
            totalCost += buyQuantity * originalPrice;
            // Pay discounted price for 'get' items
            totalCost += getQuantity * originalPrice * (1 - discountPercentage / 100);
        }

        // Handle remaining items
        if (remainingItems > 0) {
            if (remainingItems <= buyQuantity) {
                // All remaining items are at full price
                totalCost += remainingItems * originalPrice;
            } else {
                // Some remaining items get discount
                totalCost += buyQuantity * originalPrice;
                int discountedRemainingItems = remainingItems - buyQuantity;
                totalCost += discountedRemainingItems * originalPrice * (1 - discountPercentage / 100);
            }
        }

        return totalCost;
    }

    private void handleInventoryAfterOrder(List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            Inventory inventory = inventoryRepository.findByProductIdForUpdate(cartItem.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Reduce both reserved and onHand
            inventory.setReserved(inventory.getReserved() - cartItem.getQuantity());
            inventory.setOnHand(inventory.getOnHand() - cartItem.getQuantity());

            inventoryRepository.save(inventory);
        }
    }
}
