package me.huypc.elect_shop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.Optional;

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
@DisplayName("Add To Cart Integration Tests")
class AddToCartTest {

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
    private Inventory testInventory;
    private Inventory testInventoryWithDeal;
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

    // @AfterEach
    // void resetDatabase() {
    //     cartRepository.deleteAll();
    //     inventoryRepository.deleteAll();
    //     productRepository.deleteAll();
    //     userRepository.deleteAll();
    //     dealProductRepository.deleteAll();
    //     dealRepository.deleteAll();
    //     categoryRepository.deleteAll();
    // }

    @Test
    @Transactional
    @DisplayName("Should successfully add product to cart without deal")
    void shouldAddProductToCartWithoutDeal() {
        // Given
        CartItemUpsertForm cartItemForm = new CartItemUpsertForm();
        cartItemForm.setProductId(testProduct.getId());
        cartItemForm.setQuantity(2);

        // When
        cartService.addToCart(testUser, cartItemForm);

        // Then - Verify cart creation and item addition
        Optional<Cart> cartOpt = cartRepository.findById(testUser.getId());
        assertThat(cartOpt).isPresent();
        
        Cart cart = cartOpt.get();
        assertThat(cart.getUserId()).isEqualTo(testUser.getId());
        assertThat(cart.getCartItems()).hasSize(1);

        CartItem cartItem = cart.getCartItems().get(0);
        assertThat(cartItem.getProduct().getId()).isEqualTo(testProduct.getId());
        assertThat(cartItem.getQuantity()).isEqualTo(2);
        assertThat(cartItem.getUnitPrice()).isEqualTo(1000.0); // Original price, no deal
        assertThat(cartItem.getDeal()).isNull(); // No deal applied

        // Verify inventory reservation
        Inventory updatedInventory = inventoryRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(updatedInventory.getReserved()).isEqualTo(2);
        assertThat(updatedInventory.getAvailable()).isEqualTo(98); // 100 - 2 reserved
    }

    @Test
    @Transactional
    @DisplayName("Should successfully add product to cart with deal applied")
    void shouldAddProductToCartWithDeal() {
        // Given
        CartItemUpsertForm cartItemForm = new CartItemUpsertForm();
        cartItemForm.setProductId(testProductWithDeal.getId());
        cartItemForm.setQuantity(3);

        // When
        cartService.addToCart(testUser, cartItemForm);

        // Then - Verify cart creation and item addition
        Optional<Cart> cartOpt = cartRepository.findById(testUser.getId());
        assertThat(cartOpt).isPresent();
        
        Cart cart = cartOpt.get();
        assertThat(cart.getCartItems()).hasSize(1);

        CartItem cartItem = cart.getCartItems().get(0);
        assertThat(cartItem.getProduct().getId()).isEqualTo(testProductWithDeal.getId());
        assertThat(cartItem.getQuantity()).isEqualTo(3);
        
        // Verify deal price calculation: $500 - 20% = $400
        double expectedPrice = 500.0 - (500.0 * 20.0 / 100);
        assertThat(cartItem.getUnitPrice()).isEqualTo(expectedPrice);
        assertThat(cartItem.getDeal()).isNotNull();
        assertThat(cartItem.getDeal().getDealCode()).isEqualTo("TEST20");

        // Verify inventory reservation
        Inventory updatedInventory = inventoryRepository.findById(testProductWithDeal.getId()).orElseThrow();
        assertThat(updatedInventory.getReserved()).isEqualTo(3);
        assertThat(updatedInventory.getAvailable()).isEqualTo(47); // 50 - 3 reserved
    }

    @Test
    @Transactional
    @DisplayName("Should update quantity when adding same product to existing cart")
    void shouldUpdateQuantityWhenAddingSameProduct() {
        // Given - First add 2 items
        CartItemUpsertForm firstAdd = new CartItemUpsertForm();
        firstAdd.setProductId(testProduct.getId());
        firstAdd.setQuantity(2);
        cartService.addToCart(testUser, firstAdd);

        // When - Add 3 more items of the same product
        CartItemUpsertForm secondAdd = new CartItemUpsertForm();
        secondAdd.setProductId(testProduct.getId());
        secondAdd.setQuantity(3);
        cartService.addToCart(testUser, secondAdd);

        // Then - Verify quantity is updated, not duplicated
        Cart cart = cartRepository.findById(testUser.getId()).orElseThrow();
        assertThat(cart.getCartItems()).hasSize(1); // Still only one item type
        
        CartItem cartItem = cart.getCartItems().get(0);
        assertThat(cartItem.getQuantity()).isEqualTo(5); // 2 + 3 = 5

        // Verify total inventory reservation
        Inventory updatedInventory = inventoryRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(updatedInventory.getReserved()).isEqualTo(5); // Total reserved
        assertThat(updatedInventory.getAvailable()).isEqualTo(95); // 100 - 5 reserved
    }

    @Test
    @Transactional
    @DisplayName("Should handle multiple different products in cart")
    void shouldHandleMultipleProductsInCart() {
        // Given
        CartItemUpsertForm firstProduct = new CartItemUpsertForm();
        firstProduct.setProductId(testProduct.getId());
        firstProduct.setQuantity(1);

        CartItemUpsertForm secondProduct = new CartItemUpsertForm();
        secondProduct.setProductId(testProductWithDeal.getId());
        secondProduct.setQuantity(2);

        // When
        cartService.addToCart(testUser, firstProduct);
        cartService.addToCart(testUser, secondProduct);

        // Then
        Cart cart = cartRepository.findById(testUser.getId()).orElseThrow();
        assertThat(cart.getCartItems()).hasSize(2);

        // Verify first product (no deal)
        CartItem firstItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(testProduct.getId()))
                .findFirst().orElseThrow();
        assertThat(firstItem.getQuantity()).isEqualTo(1);
        assertThat(firstItem.getUnitPrice()).isEqualTo(1000.0);
        assertThat(firstItem.getDeal()).isNull();

        // Verify second product (with deal)
        CartItem secondItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(testProductWithDeal.getId()))
                .findFirst().orElseThrow();
        assertThat(secondItem.getQuantity()).isEqualTo(2);
        assertThat(secondItem.getUnitPrice()).isEqualTo(400.0); // 20% off $500
        assertThat(secondItem.getDeal()).isNotNull();

        // Verify individual inventory reservations
        Inventory inventory1 = inventoryRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(inventory1.getReserved()).isEqualTo(1);

        Inventory inventory2 = inventoryRepository.findById(testProductWithDeal.getId()).orElseThrow();
        assertThat(inventory2.getReserved()).isEqualTo(2);
    }

    @Test
    @Transactional
    @DisplayName("Should throw exception when insufficient inventory")
    void shouldThrowExceptionWhenInsufficientInventory() {
        // Given - Try to add more than available (100 available, try to add 101)
        CartItemUpsertForm cartItemForm = new CartItemUpsertForm();
        cartItemForm.setProductId(testProduct.getId());
        cartItemForm.setQuantity(101);

        // When & Then
        assertThatThrownBy(() -> cartService.addToCart(testUser, cartItemForm))
                .isInstanceOf(ConflictResourceException.class)
                .hasMessageContaining("Not enough product available");

        // Verify no changes to inventory or cart
        Inventory inventory = inventoryRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(inventory.getReserved()).isEqualTo(0); // No reservation made

        Optional<Cart> cartOpt = cartRepository.findById(testUser.getId());
        assertThat(cartOpt).isEmpty(); // No cart created
    }

    @Test
    @Transactional
    @DisplayName("Should throw exception when deal is inactive")
    void shouldThrowExceptionWhenDealIsInactive() {
        // Given - Make the deal inactive by setting end date in the past
        testDeal.setEndAt(LocalDateTime.now().minusDays(1));
        dealRepository.saveAndFlush(testDeal);

        CartItemUpsertForm cartItemForm = new CartItemUpsertForm();
        cartItemForm.setProductId(testProductWithDeal.getId());
        cartItemForm.setQuantity(1);

        // When & Then
        assertThatThrownBy(() -> cartService.addToCart(testUser, cartItemForm))
                .isInstanceOf(ConflictResourceException.class)
                .hasMessageContaining("Deal is not active");

        // Verify no changes made
        Inventory inventory = inventoryRepository.findById(testProductWithDeal.getId()).orElseThrow();
        assertThat(inventory.getReserved()).isEqualTo(0);

        Optional<Cart> cartOpt = cartRepository.findById(testUser.getId());
        assertThat(cartOpt).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Should handle cart total calculation correctly")
    void shouldCalculateCartTotalCorrectly() {
        // Given - Add products with and without deals
        CartItemUpsertForm productNoDeal = new CartItemUpsertForm();
        productNoDeal.setProductId(testProduct.getId());
        productNoDeal.setQuantity(2); // 2 × $1000 = $2000

        CartItemUpsertForm productWithDeal = new CartItemUpsertForm();
        productWithDeal.setProductId(testProductWithDeal.getId());
        productWithDeal.setQuantity(3); // 3 × $400 (20% off $500) = $1200

        // When
        cartService.addToCart(testUser, productNoDeal);
        cartService.addToCart(testUser, productWithDeal);

        // Then
        Cart cart = cartRepository.findById(testUser.getId()).orElseThrow();
        
        // Verify total using Cart's helper method
        double expectedTotal = (2 * 1000.0) + (3 * 400.0); // $2000 + $1200 = $3200
        assertThat(cart.getTotalAmount()).isEqualTo(expectedTotal);
        assertThat(cart.getTotalItems()).isEqualTo(2); // 2 different product types
    }
}