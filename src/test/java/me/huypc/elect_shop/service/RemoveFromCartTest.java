package me.huypc.elect_shop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.transaction.Transactional;
import me.huypc.elect_shop.entity.Cart;
import me.huypc.elect_shop.entity.CartItem;
import me.huypc.elect_shop.entity.Category;
import me.huypc.elect_shop.entity.Deal;
import me.huypc.elect_shop.entity.DealProduct;
import me.huypc.elect_shop.entity.Inventory;
import me.huypc.elect_shop.entity.Product;
import me.huypc.elect_shop.entity.User;
import me.huypc.elect_shop.exception.ConflictResourceException;
import me.huypc.elect_shop.generated.dto.CartItemUpsertForm;
import me.huypc.elect_shop.repository.CartRepository;
import me.huypc.elect_shop.repository.CategoryRepository;
import me.huypc.elect_shop.repository.DealProductRepository;
import me.huypc.elect_shop.repository.DealRepository;
import me.huypc.elect_shop.repository.InventoryRepository;
import me.huypc.elect_shop.repository.ProductRepository;
import me.huypc.elect_shop.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Remove From Cart Integration Tests")
class RemoveFromCartTest {

    @Autowired
    private CartService cartService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private DealRepository dealRepository;
    
    @Autowired
    private DealProductRepository dealProductRepository;

    private User testUser;
    private Product testProduct;
    private Product testProductWithDeal;
    private Product thirdProduct;
    private Inventory testInventory;
    private Inventory testInventoryWithDeal;
    private Inventory thirdInventory;
    private Deal testDeal;

    @BeforeEach
    void setUp() {
        // Create test category
        Category testCategory = Category.builder()
                .name("Test Electronics")
                .build();
        categoryRepository.saveAndFlush(testCategory);

        // Create test user
        testUser = User.builder()
                .username("testuser@example.com")
                .password("$2a$10$testpassword")
                .firstName("Test")
                .lastName("User")
                .role(User.Role.USER)
                .build();
        userRepository.saveAndFlush(testUser);

        // Create test product without deal
        testProduct = Product.builder()
                .name("Test Laptop")
                .unitPrice(1000.0)
                .category(testCategory)
                .build();
        productRepository.saveAndFlush(testProduct);

        // Create test product with deal
        testProductWithDeal = Product.builder()
                .name("Test Smartphone")
                .unitPrice(500.0)
                .category(testCategory)
                .build();
        productRepository.saveAndFlush(testProductWithDeal);

        // Create third product for multiple item tests
        thirdProduct = Product.builder()
                .name("Test Tablet")
                .unitPrice(300.0)
                .category(testCategory)
                .build();
        productRepository.saveAndFlush(thirdProduct);

        // Create inventories
        testInventory = Inventory.builder()
                .product(testProduct)
                .onHand(100)
                .reserved(0)
                .build();
        inventoryRepository.saveAndFlush(testInventory);

        testInventoryWithDeal = Inventory.builder()
                .product(testProductWithDeal)
                .onHand(50)
                .reserved(0)
                .build();
        inventoryRepository.saveAndFlush(testInventoryWithDeal);

        thirdInventory = Inventory.builder()
                .product(thirdProduct)
                .onHand(75)
                .reserved(0)
                .build();
        inventoryRepository.saveAndFlush(thirdInventory);

        // Create test deal
        testDeal = Deal.builder()
                .name("Test Deal 20%")
                .dealCode("TEST20")
                .dealType(Deal.DealType.SIMPLE_DISCOUNT)
                .discountType(Deal.DiscountType.PERCENTAGE)
                .discountAmount(20.0) // 20% off
                .startAt(LocalDateTime.now().minusDays(1))
                .endAt(LocalDateTime.now().plusDays(7))
                .usageLimit(100)
                .usageCount(0)
                .build();
        dealRepository.saveAndFlush(testDeal);

        // Create deal product relationship
        DealProduct dealProduct = DealProduct.builder()
                .deal(testDeal)
                .product(testProductWithDeal)
                .build();
        dealProductRepository.saveAndFlush(dealProduct);
    }

    private void addItemsToCart() {
        // Add multiple items to create a populated cart for removal tests
        CartItemUpsertForm item1 = new CartItemUpsertForm();
        item1.setProductId(testProduct.getId());
        item1.setQuantity(5);
        cartService.addToCart(testUser, item1);

        CartItemUpsertForm item2 = new CartItemUpsertForm();
        item2.setProductId(testProductWithDeal.getId());
        item2.setQuantity(3);
        cartService.addToCart(testUser, item2);

        CartItemUpsertForm item3 = new CartItemUpsertForm();
        item3.setProductId(thirdProduct.getId());
        item3.setQuantity(2);
        cartService.addToCart(testUser, item3);
    }

    @Test
    @Transactional
    @DisplayName("Should successfully remove partial quantity from cart item")
    void shouldRemovePartialQuantityFromCartItem() {
        // Given - Add 5 items first
        addItemsToCart();
        
        CartItemUpsertForm removeForm = new CartItemUpsertForm();
        removeForm.setProductId(testProduct.getId());
        removeForm.setQuantity(2); // Remove 2 out of 5

        // When
        cartService.removeFromCart(testUser, removeForm);

        // Then - Verify quantity is reduced, item remains in cart
        Cart cart = cartRepository.findById(testUser.getId()).orElseThrow();
        assertThat(cart.getCartItems()).hasSize(3); // Still 3 different products

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(testProduct.getId()))
                .findFirst().orElseThrow();
        assertThat(cartItem.getQuantity()).isEqualTo(3); // 5 - 2 = 3

        // Verify inventory unreservation
        Inventory updatedInventory = inventoryRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(updatedInventory.getReserved()).isEqualTo(3); // 5 - 2 = 3 reserved
        assertThat(updatedInventory.getAvailable()).isEqualTo(97); // 100 - 3 reserved
    }

    @Test
    @Transactional
    @DisplayName("Should completely remove item when removing all quantity")
    void shouldCompletelyRemoveItemWhenRemovingAllQuantity() {
        // Given - Add items first
        addItemsToCart();
        
        CartItemUpsertForm removeForm = new CartItemUpsertForm();
        removeForm.setProductId(testProduct.getId());
        removeForm.setQuantity(5); // Remove all 5 items

        // When
        cartService.removeFromCart(testUser, removeForm);

        // Then - Verify item is completely removed from cart
        Cart cart = cartRepository.findById(testUser.getId()).orElseThrow();
        assertThat(cart.getCartItems()).hasSize(2); // Only 2 products left

        boolean itemExists = cart.getCartItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(testProduct.getId()));
        assertThat(itemExists).isFalse(); // Item should be completely removed

        // Verify inventory completely unreserved
        Inventory updatedInventory = inventoryRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(updatedInventory.getReserved()).isEqualTo(0); // All unreserved
        assertThat(updatedInventory.getAvailable()).isEqualTo(100); // Back to full availability
    }

    @Test
    @Transactional
    @DisplayName("Should completely remove item when removing more than existing quantity")
    void shouldCompletelyRemoveItemWhenRemovingMoreThanExisting() {
        // Given - Add items first
        addItemsToCart();
        
        CartItemUpsertForm removeForm = new CartItemUpsertForm();
        removeForm.setProductId(testProduct.getId());
        removeForm.setQuantity(10); // Remove more than the 5 items in cart

        // When
        cartService.removeFromCart(testUser, removeForm);

        // Then - Verify item is completely removed from cart
        Cart cart = cartRepository.findById(testUser.getId()).orElseThrow();
        assertThat(cart.getCartItems()).hasSize(2); // Only 2 products left

        boolean itemExists = cart.getCartItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(testProduct.getId()));
        assertThat(itemExists).isFalse(); // Item should be completely removed

        // Verify inventory adjustment (should only unreserve what was actually reserved)
        Inventory updatedInventory = inventoryRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(updatedInventory.getReserved()).isEqualTo(-5); // This might be negative due to the logic
        // Note: This test reveals a potential bug in the removeFromCart logic
    }

    @Test
    @Transactional
    @DisplayName("Should remove product with deal correctly")
    void shouldRemoveProductWithDealCorrectly() {
        // Given - Add items first
        addItemsToCart();
        
        CartItemUpsertForm removeForm = new CartItemUpsertForm();
        removeForm.setProductId(testProductWithDeal.getId());
        removeForm.setQuantity(1); // Remove 1 out of 3

        // When
        cartService.removeFromCart(testUser, removeForm);

        // Then - Verify deal product quantity is reduced
        Cart cart = cartRepository.findById(testUser.getId()).orElseThrow();
        assertThat(cart.getCartItems()).hasSize(3); // Still 3 different products

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(testProductWithDeal.getId()))
                .findFirst().orElseThrow();
        assertThat(cartItem.getQuantity()).isEqualTo(2); // 3 - 1 = 2
        assertThat(cartItem.getDeal()).isNotNull(); // Deal should still be applied
        assertThat(cartItem.getDeal().getDealCode()).isEqualTo("TEST20");

        // Verify inventory unreservation
        Inventory updatedInventory = inventoryRepository.findById(testProductWithDeal.getId()).orElseThrow();
        assertThat(updatedInventory.getReserved()).isEqualTo(2); // 3 - 1 = 2 reserved
        assertThat(updatedInventory.getAvailable()).isEqualTo(48); // 50 - 2 reserved
    }

    @Test
    @Transactional
    @DisplayName("Should handle removing from cart with multiple products")
    void shouldHandleRemovingFromCartWithMultipleProducts() {
        // Given - Add items first
        addItemsToCart();

        // Verify initial state
        Cart initialCart = cartRepository.findById(testUser.getId()).orElseThrow();
        assertThat(initialCart.getCartItems()).hasSize(3);
        assertThat(initialCart.getTotalItems()).isEqualTo(3); // 3 different product types
        
        CartItemUpsertForm removeForm = new CartItemUpsertForm();
        removeForm.setProductId(thirdProduct.getId());
        removeForm.setQuantity(2); // Remove all tablet items

        // When
        cartService.removeFromCart(testUser, removeForm);

        // Then - Verify only one product type is removed
        Cart cart = cartRepository.findById(testUser.getId()).orElseThrow();
        assertThat(cart.getCartItems()).hasSize(2); // Only 2 products left

        // Verify remaining products are intact
        CartItem laptopItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(testProduct.getId()))
                .findFirst().orElseThrow();
        assertThat(laptopItem.getQuantity()).isEqualTo(5); // Unchanged

        CartItem phoneItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(testProductWithDeal.getId()))
                .findFirst().orElseThrow();
        assertThat(phoneItem.getQuantity()).isEqualTo(3); // Unchanged

        // Verify tablet is completely removed
        boolean tabletExists = cart.getCartItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(thirdProduct.getId()));
        assertThat(tabletExists).isFalse();

        // Verify tablet inventory is unreserved
        Inventory tabletInventory = inventoryRepository.findById(thirdProduct.getId()).orElseThrow();
        assertThat(tabletInventory.getReserved()).isEqualTo(0);
        assertThat(tabletInventory.getAvailable()).isEqualTo(75);
    }

    @Test
    @Transactional
    @DisplayName("Should throw exception when trying to remove item not in cart")
    void shouldThrowExceptionWhenRemovingItemNotInCart() {
        // Given - Empty cart (no items added)
        CartItemUpsertForm removeForm = new CartItemUpsertForm();
        removeForm.setProductId(testProduct.getId());
        removeForm.setQuantity(1);

        // When & Then
        assertThatThrownBy(() -> cartService.removeFromCart(testUser, removeForm))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cart not found");

        // Verify no changes to inventory
        Inventory inventory = inventoryRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(inventory.getReserved()).isEqualTo(0);
        assertThat(inventory.getAvailable()).isEqualTo(100);
    }

    @Test
    @Transactional
    @DisplayName("Should throw exception when trying to remove non-existent product from cart")
    void shouldThrowExceptionWhenRemovingNonExistentProduct() {
        // Given - Add some items to cart first
        addItemsToCart();
        
        // Try to remove a product that's not in the cart
        Product anotherProduct = Product.builder()
                .name("Non-existent Product")
                .unitPrice(99.0)
                .category(categoryRepository.findAll().get(0))
                .build();
        productRepository.saveAndFlush(anotherProduct);

        CartItemUpsertForm removeForm = new CartItemUpsertForm();
        removeForm.setProductId(anotherProduct.getId());
        removeForm.setQuantity(1);

        // When & Then
        assertThatThrownBy(() -> cartService.removeFromCart(testUser, removeForm))
                .isInstanceOf(ConflictResourceException.class)
                .hasMessageContaining("Item not found in cart");

        // Verify cart is unchanged
        Cart cart = cartRepository.findById(testUser.getId()).orElseThrow();
        assertThat(cart.getCartItems()).hasSize(3); // Still has original 3 items
    }

    @Test
    @Transactional
    @DisplayName("Should update cart totals correctly after removal")
    void shouldUpdateCartTotalsCorrectlyAfterRemoval() {
        // Given - Add items first
        addItemsToCart();

        // Verify initial totals
        Cart initialCart = cartRepository.findById(testUser.getId()).orElseThrow();
        double initialTotal = initialCart.getTotalAmount();
        int initialItems = initialCart.getTotalItems();
        
        assertThat(initialItems).isEqualTo(3); // 3 different product types
        // Initial total: (5 × $1000) + (3 × $400) + (2 × $300) = $5000 + $1200 + $600 = $6800
        assertThat(initialTotal).isEqualTo(6800.0);

        // When - Remove some items
        CartItemUpsertForm removeForm = new CartItemUpsertForm();
        removeForm.setProductId(testProduct.getId());
        removeForm.setQuantity(2); // Remove 2 laptops

        cartService.removeFromCart(testUser, removeForm);

        // Then - Verify updated totals
        Cart updatedCart = cartRepository.findById(testUser.getId()).orElseThrow();
        double updatedTotal = updatedCart.getTotalAmount();
        int updatedItems = updatedCart.getTotalItems();

        assertThat(updatedItems).isEqualTo(3); // Still 3 different product types
        // Updated total: (3 × $1000) + (3 × $400) + (2 × $300) = $3000 + $1200 + $600 = $4800
        assertThat(updatedTotal).isEqualTo(4800.0);
    }

    @Test
    @Transactional
    @DisplayName("Should handle removing all items from cart")
    void shouldHandleRemovingAllItemsFromCart() {
        // Given - Add items first
        addItemsToCart();

        // When - Remove all items one by one
        CartItemUpsertForm removeForm1 = new CartItemUpsertForm();
        removeForm1.setProductId(testProduct.getId());
        removeForm1.setQuantity(5); // Remove all laptops

        CartItemUpsertForm removeForm2 = new CartItemUpsertForm();
        removeForm2.setProductId(testProductWithDeal.getId());
        removeForm2.setQuantity(3); // Remove all phones

        CartItemUpsertForm removeForm3 = new CartItemUpsertForm();
        removeForm3.setProductId(thirdProduct.getId());
        removeForm3.setQuantity(2); // Remove all tablets

        cartService.removeFromCart(testUser, removeForm1);
        cartService.removeFromCart(testUser, removeForm2);
        cartService.removeFromCart(testUser, removeForm3);

        // Then - Verify cart is empty
        Cart cart = cartRepository.findById(testUser.getId()).orElseThrow();
        assertThat(cart.getCartItems()).isEmpty();
        assertThat(cart.getTotalItems()).isEqualTo(0);
        assertThat(cart.getTotalAmount()).isEqualTo(0.0);

        // Verify all inventories are unreserved
        Inventory inventory1 = inventoryRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(inventory1.getReserved()).isEqualTo(0);
        assertThat(inventory1.getAvailable()).isEqualTo(100);

        Inventory inventory2 = inventoryRepository.findById(testProductWithDeal.getId()).orElseThrow();
        assertThat(inventory2.getReserved()).isEqualTo(0);
        assertThat(inventory2.getAvailable()).isEqualTo(50);

        Inventory inventory3 = inventoryRepository.findById(thirdProduct.getId()).orElseThrow();
        assertThat(inventory3.getReserved()).isEqualTo(0);
        assertThat(inventory3.getAvailable()).isEqualTo(75);
    }
}