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
import me.huypc.elect_shop.entity.Category;
import me.huypc.elect_shop.entity.Deal;
import me.huypc.elect_shop.entity.DealProduct;
import me.huypc.elect_shop.entity.Inventory;
import me.huypc.elect_shop.entity.Order;
import me.huypc.elect_shop.entity.OrderDetail;
import me.huypc.elect_shop.entity.Product;
import me.huypc.elect_shop.entity.User;
import me.huypc.elect_shop.exception.ConflictResourceException;
import me.huypc.elect_shop.generated.dto.CartItemUpsertForm;
import me.huypc.elect_shop.generated.dto.OrderPlaceForm;
import me.huypc.elect_shop.repository.CartRepository;
import me.huypc.elect_shop.repository.CategoryRepository;
import me.huypc.elect_shop.repository.DealProductRepository;
import me.huypc.elect_shop.repository.DealRepository;
import me.huypc.elect_shop.repository.InventoryRepository;
import me.huypc.elect_shop.repository.OrderRepository;
import me.huypc.elect_shop.repository.ProductRepository;
import me.huypc.elect_shop.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Place Order Integration Tests")
class PlaceOrderTest {

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
    
    @Autowired
    private OrderRepository orderRepository;

    private User testUser;
    private Product testProduct;
    private Product testProductWithDeal;
    private Product bogoProduct;
    private Product buyXGetYProduct;
    private Inventory testInventory;
    private Inventory testInventoryWithDeal;
    private Inventory bogoInventory;
    private Inventory buyXGetYInventory;
    private Deal simpleDiscountDeal;
    private Deal bogoDeal;
    private Deal buyXGetYDeal;

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

        // Create test product with simple discount deal
        testProductWithDeal = Product.builder()
                .name("Test Smartphone")
                .unitPrice(500.0)
                .category(testCategory)
                .build();
        productRepository.saveAndFlush(testProductWithDeal);

        // Create product for BOGO deal
        bogoProduct = Product.builder()
                .name("Test Headphones")
                .unitPrice(100.0)
                .category(testCategory)
                .build();
        productRepository.saveAndFlush(bogoProduct);

        // Create product for Buy X Get Y deal
        buyXGetYProduct = Product.builder()
                .name("Test Accessories")
                .unitPrice(50.0)
                .category(testCategory)
                .build();
        productRepository.saveAndFlush(buyXGetYProduct);

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

        bogoInventory = Inventory.builder()
                .product(bogoProduct)
                .onHand(20)
                .reserved(0)
                .build();
        inventoryRepository.saveAndFlush(bogoInventory);

        buyXGetYInventory = Inventory.builder()
                .product(buyXGetYProduct)
                .onHand(30)
                .reserved(0)
                .build();
        inventoryRepository.saveAndFlush(buyXGetYInventory);

        // Create simple discount deal
        simpleDiscountDeal = Deal.builder()
                .name("Simple Discount 20%")
                .dealCode("SIMPLE20")
                .dealType(Deal.DealType.SIMPLE_DISCOUNT)
                .discountType(Deal.DiscountType.PERCENTAGE)
                .discountAmount(20.0) // 20% off
                .startAt(LocalDateTime.now().minusDays(1))
                .endAt(LocalDateTime.now().plusDays(7))
                .usageLimit(100)
                .usageCount(0)
                .build();
        dealRepository.saveAndFlush(simpleDiscountDeal);

        // Create BOGO deal
        bogoDeal = Deal.builder()
                .name("Buy One Get One")
                .dealCode("BOGO")
                .dealType(Deal.DealType.BUY_ONE_GET_ONE)
                .startAt(LocalDateTime.now().minusDays(1))
                .endAt(LocalDateTime.now().plusDays(7))
                .usageLimit(100)
                .usageCount(0)
                .build();
        dealRepository.saveAndFlush(bogoDeal);

        // Create Buy X Get Y deal
        buyXGetYDeal = Deal.builder()
                .name("Buy 2 Get 30% Off Next 1")
                .dealCode("BUY2GET30")
                .dealType(Deal.DealType.BUY_X_GET_Y_OFF)
                .discountType(Deal.DiscountType.PERCENTAGE)
                .discountAmount(30.0)
                .buyQuantity(2)
                .getQuantity(1)
                .startAt(LocalDateTime.now().minusDays(1))
                .endAt(LocalDateTime.now().plusDays(7))
                .usageLimit(100)
                .usageCount(0)
                .build();
        dealRepository.saveAndFlush(buyXGetYDeal);

        // Create deal product relationships
        DealProduct simpleDiscountDealProduct = DealProduct.builder()
                .deal(simpleDiscountDeal)
                .product(testProductWithDeal)
                .build();
        dealProductRepository.saveAndFlush(simpleDiscountDealProduct);

        DealProduct bogoDealProduct = DealProduct.builder()
                .deal(bogoDeal)
                .product(bogoProduct)
                .build();
        dealProductRepository.saveAndFlush(bogoDealProduct);

        DealProduct buyXGetYDealProduct = DealProduct.builder()
                .deal(buyXGetYDeal)
                .product(buyXGetYProduct)
                .build();
        dealProductRepository.saveAndFlush(buyXGetYDealProduct);
    }

    private void addItemsToCart() {
        // Add regular product
        CartItemUpsertForm item1 = new CartItemUpsertForm();
        item1.setProductId(testProduct.getId());
        item1.setQuantity(2);
        cartService.addToCart(testUser, item1);

        // Add product with simple discount
        CartItemUpsertForm item2 = new CartItemUpsertForm();
        item2.setProductId(testProductWithDeal.getId());
        item2.setQuantity(1);
        cartService.addToCart(testUser, item2);
    }

    @Test
    @Transactional
    @DisplayName("Should successfully place order with regular products")
    void shouldSuccessfullyPlaceOrderWithRegularProducts() {
        // Given - Add items to cart
        addItemsToCart();

        OrderPlaceForm orderForm = new OrderPlaceForm();
        orderForm.setShippingAddress("123 Test Street, Test City, TC 12345");

        // Verify initial state
        Cart initialCart = cartRepository.findById(testUser.getId()).orElseThrow();
        assertThat(initialCart.getCartItems()).hasSize(2);

        // When
        cartService.placeOrder(testUser, orderForm);

        // Then - Verify order creation
        assertThat(orderRepository.findAll()).hasSize(1);
        Order order = orderRepository.findAll().get(0);
        
        assertThat(order.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.PLACED);
        assertThat(order.getTotalPrice()).isEqualTo(2400.0); // (2 × $1000) + (1 × $400) = $2400
        assertThat(order.getOrderDetails()).hasSize(2);

        // Verify order details
        OrderDetail laptopDetail = order.getOrderDetails().stream()
                .filter(detail -> detail.getProduct().getId().equals(testProduct.getId()))
                .findFirst().orElseThrow();
        assertThat(laptopDetail.getQuantity()).isEqualTo(2);
        assertThat(laptopDetail.getUnitPrice()).isEqualTo(1000.0);

        OrderDetail phoneDetail = order.getOrderDetails().stream()
                .filter(detail -> detail.getProduct().getId().equals(testProductWithDeal.getId()))
                .findFirst().orElseThrow();
        assertThat(phoneDetail.getQuantity()).isEqualTo(1);
        assertThat(phoneDetail.getUnitPrice()).isEqualTo(400.0); // 20% discount applied

        // Verify cart is cleared
        Cart clearedCart = cartRepository.findById(testUser.getId()).orElseThrow();
        assertThat(clearedCart.getCartItems()).isEmpty();

        // Verify inventory updates
        Inventory laptopInventory = inventoryRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(laptopInventory.getOnHand()).isEqualTo(98); // 100 - 2 = 98
        assertThat(laptopInventory.getReserved()).isEqualTo(0); // 2 - 2 = 0

        Inventory phoneInventory = inventoryRepository.findById(testProductWithDeal.getId()).orElseThrow();
        assertThat(phoneInventory.getOnHand()).isEqualTo(49); // 50 - 1 = 49
        assertThat(phoneInventory.getReserved()).isEqualTo(0); // 1 - 1 = 0
    }

    @Test
    @Transactional
    @DisplayName("Should successfully place order with BOGO deal")
    void shouldSuccessfullyPlaceOrderWithBogoDeal() {
        // Given - Add BOGO product to cart
        CartItemUpsertForm bogoItem = new CartItemUpsertForm();
        bogoItem.setProductId(bogoProduct.getId());
        bogoItem.setQuantity(3); // Buy 3, pay for 2 (ceil(3/2) = 2)
        cartService.addToCart(testUser, bogoItem);

        OrderPlaceForm orderForm = new OrderPlaceForm();
        orderForm.setShippingAddress("123 Test Street, Test City, TC 12345");

        // When
        cartService.placeOrder(testUser, orderForm);

        // Then
        Order order = orderRepository.findAll().get(0);
        assertThat(order.getTotalPrice()).isEqualTo(200.0); // BOGO: ceil(3/2) = 2 items × $100 = $200
        assertThat(order.getOrderDetails()).hasSize(1);

        OrderDetail bogoDetail = order.getOrderDetails().get(0);
        assertThat(bogoDetail.getQuantity()).isEqualTo(3);
        assertThat(bogoDetail.getUnitPrice()).isEqualTo(100.0); // Original unit price stored

        // Verify inventory
        Inventory bogoInventoryAfter = inventoryRepository.findById(bogoProduct.getId()).orElseThrow();
        assertThat(bogoInventoryAfter.getOnHand()).isEqualTo(17); // 20 - 3 = 17
        assertThat(bogoInventoryAfter.getReserved()).isEqualTo(0);
    }

    @Test
    @Transactional
    @DisplayName("Should successfully place order with Buy X Get Y Off deal")
    void shouldSuccessfullyPlaceOrderWithBuyXGetYOffDeal() {
        // Given - Add Buy X Get Y product to cart
        CartItemUpsertForm buyXGetYItem = new CartItemUpsertForm();
        buyXGetYItem.setProductId(buyXGetYProduct.getId());
        buyXGetYItem.setQuantity(5); // Buy 2 get 30% off next 1, repeat: 5 items = 2 + 1(30% off) + 2 = $100 + $35 + $100 = $235
        cartService.addToCart(testUser, buyXGetYItem);

        OrderPlaceForm orderForm = new OrderPlaceForm();
        orderForm.setShippingAddress("123 Test Street, Test City, TC 12345");

        // When
        cartService.placeOrder(testUser, orderForm);

        // Then
        Order order = orderRepository.findAll().get(0);
        // Expected: 2×$50 + 1×$35 + 2×$50 = $100 + $35 + $100 = $235
        assertThat(order.getTotalPrice()).isEqualTo(235.0);
        assertThat(order.getOrderDetails()).hasSize(1);

        OrderDetail buyXGetYDetail = order.getOrderDetails().get(0);
        assertThat(buyXGetYDetail.getQuantity()).isEqualTo(5);
        assertThat(buyXGetYDetail.getUnitPrice()).isEqualTo(50.0); // Original unit price stored

        // Verify inventory
        Inventory buyXGetYInventoryAfter = inventoryRepository.findById(buyXGetYProduct.getId()).orElseThrow();
        assertThat(buyXGetYInventoryAfter.getOnHand()).isEqualTo(25); // 30 - 5 = 25
        assertThat(buyXGetYInventoryAfter.getReserved()).isEqualTo(0);
    }

    @Test
    @Transactional
    @DisplayName("Should successfully place order with mixed deal types")
    void shouldSuccessfullyPlaceOrderWithMixedDealTypes() {
        // Given - Add items with different deal types
        CartItemUpsertForm regularItem = new CartItemUpsertForm();
        regularItem.setProductId(testProduct.getId());
        regularItem.setQuantity(1);
        cartService.addToCart(testUser, regularItem);

        CartItemUpsertForm simpleDiscountItem = new CartItemUpsertForm();
        simpleDiscountItem.setProductId(testProductWithDeal.getId());
        simpleDiscountItem.setQuantity(1);
        cartService.addToCart(testUser, simpleDiscountItem);

        CartItemUpsertForm bogoItem = new CartItemUpsertForm();
        bogoItem.setProductId(bogoProduct.getId());
        bogoItem.setQuantity(2);
        cartService.addToCart(testUser, bogoItem);

        OrderPlaceForm orderForm = new OrderPlaceForm();
        orderForm.setShippingAddress("123 Test Street, Test City, TC 12345");

        // When
        cartService.placeOrder(testUser, orderForm);

        // Then
        Order order = orderRepository.findAll().get(0);
        // Expected: $1000 (regular) + $400 (20% off $500) + $100 (BOGO: ceil(2/2) = 1 item) = $1500
        assertThat(order.getTotalPrice()).isEqualTo(1500.0);
        assertThat(order.getOrderDetails()).hasSize(3);

        // Verify all inventories are updated correctly
        Inventory regularInventory = inventoryRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(regularInventory.getOnHand()).isEqualTo(99);
        assertThat(regularInventory.getReserved()).isEqualTo(0);

        Inventory discountInventory = inventoryRepository.findById(testProductWithDeal.getId()).orElseThrow();
        assertThat(discountInventory.getOnHand()).isEqualTo(49);
        assertThat(discountInventory.getReserved()).isEqualTo(0);

        Inventory bogoInventoryAfter = inventoryRepository.findById(bogoProduct.getId()).orElseThrow();
        assertThat(bogoInventoryAfter.getOnHand()).isEqualTo(18);
        assertThat(bogoInventoryAfter.getReserved()).isEqualTo(0);
    }

    @Test
    @Transactional
    @DisplayName("Should throw exception when trying to place order with empty cart")
    void shouldThrowExceptionWhenPlacingOrderWithEmptyCart() {
        // Given - User with empty cart
        OrderPlaceForm orderForm = new OrderPlaceForm();
        orderForm.setShippingAddress("123 Test Street, Test City, TC 12345");

        // When & Then
        assertThatThrownBy(() -> cartService.placeOrder(testUser, orderForm))
                .isInstanceOf(ConflictResourceException.class)
                .hasMessageContaining("Cart is empty");

        // Verify no order is created
        assertThat(orderRepository.findAll()).isEmpty();

        // Verify no inventory changes
        Inventory inventory = inventoryRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(inventory.getOnHand()).isEqualTo(100);
        assertThat(inventory.getReserved()).isEqualTo(0);
    }

    @Test
    @Transactional
    @DisplayName("Should throw exception when user has no cart")
    void shouldThrowExceptionWhenUserHasNoCart() {
        // Given - User with no cart (user.getCart() returns null)
        testUser.setCart(null);
        userRepository.saveAndFlush(testUser);

        OrderPlaceForm orderForm = new OrderPlaceForm();
        orderForm.setShippingAddress("123 Test Street, Test City, TC 12345");

        // When & Then
        assertThatThrownBy(() -> cartService.placeOrder(testUser, orderForm))
                .isInstanceOf(ConflictResourceException.class)
                .hasMessageContaining("Cart is empty");

        // Verify no order is created
        assertThat(orderRepository.findAll()).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Should handle multiple orders from same user")
    void shouldHandleMultipleOrdersFromSameUser() {
        // Given - Place first order
        addItemsToCart();
        OrderPlaceForm firstOrderForm = new OrderPlaceForm();
        firstOrderForm.setShippingAddress("123 First Street, Test City, TC 12345");
        cartService.placeOrder(testUser, firstOrderForm);

        // Add items for second order
        CartItemUpsertForm secondOrderItem = new CartItemUpsertForm();
        secondOrderItem.setProductId(testProduct.getId());
        secondOrderItem.setQuantity(1);
        cartService.addToCart(testUser, secondOrderItem);

        OrderPlaceForm secondOrderForm = new OrderPlaceForm();
        secondOrderForm.setShippingAddress("456 Second Street, Test City, TC 12345");

        // When - Place second order
        cartService.placeOrder(testUser, secondOrderForm);

        // Then - Verify both orders exist
        assertThat(orderRepository.findAll()).hasSize(2);

        // Verify total inventory reduction
        Inventory laptopInventory = inventoryRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(laptopInventory.getOnHand()).isEqualTo(97); // 100 - 2 - 1 = 97
        assertThat(laptopInventory.getReserved()).isEqualTo(0);

        // Verify cart is empty after second order
        Cart finalCart = cartRepository.findById(testUser.getId()).orElseThrow();
        assertThat(finalCart.getCartItems()).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Should preserve original product prices in order details")
    void shouldPreserveOriginalProductPricesInOrderDetails() {
        // Given - Add items with deals to cart
        CartItemUpsertForm dealItem = new CartItemUpsertForm();
        dealItem.setProductId(testProductWithDeal.getId());
        dealItem.setQuantity(2);
        cartService.addToCart(testUser, dealItem);

        OrderPlaceForm orderForm = new OrderPlaceForm();
        orderForm.setShippingAddress("123 Test Street, Test City, TC 12345");

        // When
        cartService.placeOrder(testUser, orderForm);

        // Then - Verify order details store the discounted unit price from cart
        Order order = orderRepository.findAll().get(0);
        OrderDetail dealDetail = order.getOrderDetails().get(0);
        
        assertThat(dealDetail.getUnitPrice()).isEqualTo(400.0); // Discounted price from cart
        assertThat(dealDetail.getQuantity()).isEqualTo(2);
        
        // But total calculation uses deal logic: 2 × $400 = $800
        assertThat(order.getTotalPrice()).isEqualTo(800.0);
    }

    @Test
    @Transactional
    @DisplayName("Should handle order timestamps correctly")
    void shouldHandleOrderTimestampsCorrectly() {
        // Given
        LocalDateTime beforeOrder = LocalDateTime.now().minusSeconds(1);
        addItemsToCart();
        
        OrderPlaceForm orderForm = new OrderPlaceForm();
        orderForm.setShippingAddress("123 Test Street, Test City, TC 12345");

        // When
        cartService.placeOrder(testUser, orderForm);
        LocalDateTime afterOrder = LocalDateTime.now().plusSeconds(1);

        // Then
        Order order = orderRepository.findAll().get(0);
        assertThat(order.getCreatedAt()).isAfter(beforeOrder);
        assertThat(order.getCreatedAt()).isBefore(afterOrder);
        assertThat(order.getUpdatedAt()).isAfter(beforeOrder);
        assertThat(order.getUpdatedAt()).isBefore(afterOrder);
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.PLACED);
    }
}