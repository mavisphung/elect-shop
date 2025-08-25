package me.huypc.elect_shop.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import me.huypc.elect_shop.entity.Cart;
import me.huypc.elect_shop.entity.CartItem;
import me.huypc.elect_shop.entity.Category;
import me.huypc.elect_shop.entity.Deal;
import me.huypc.elect_shop.entity.DealProduct;
import me.huypc.elect_shop.entity.Inventory;
import me.huypc.elect_shop.entity.Product;
import me.huypc.elect_shop.entity.User;
import me.huypc.elect_shop.generated.dto.CartItemUpsertForm;
import me.huypc.elect_shop.repository.CartRepository;
import me.huypc.elect_shop.repository.CategoryRepository;
import me.huypc.elect_shop.repository.DealProductRepository;
import me.huypc.elect_shop.repository.DealRepository;
import me.huypc.elect_shop.repository.InventoryRepository;
import me.huypc.elect_shop.repository.ProductRepository;
import me.huypc.elect_shop.repository.UserRepository;
import me.huypc.elect_shop.service.JwtService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Add To Cart API Integration Tests")
class AddToCartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private DealRepository dealRepository;

    @Autowired
    private DealProductRepository dealProductRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private String authToken;
    private Category electronicsCategory;
    private Category clothingCategory;
    private Product laptop;
    private Product smartphone;
    private Product tablet;
    private Product tshirt;
    private Product outOfStockProduct;
    private Deal activeDeal;

    private void cleanUp() {
        // Clean up any existing data in proper order (child to parent)
        cartRepository.deleteAll();
        dealProductRepository.deleteAll();
        dealRepository.deleteAll();
        inventoryRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    void setUp() {
        cleanUp();

        // Create test user
        testUser = User.builder()
                .username("testuser@shop.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("Test")
                .lastName("User")
                .role(User.Role.USER)
                .build();
        userRepository.saveAndFlush(testUser);

        // Generate JWT token for authentication
        authToken = jwtService.generateToken(testUser);

        // Create test categories
        electronicsCategory = Category.builder()
                .name("Electronics")
                .build();
        categoryRepository.saveAndFlush(electronicsCategory);

        clothingCategory = Category.builder()
                .name("Clothing")
                .build();
        categoryRepository.saveAndFlush(clothingCategory);

        // Create test products
        laptop = Product.builder()
                .name("Gaming Laptop")
                .unitPrice(1500.0)
                .category(electronicsCategory)
                .build();
        productRepository.saveAndFlush(laptop);

        smartphone = Product.builder()
                .name("Smartphone Pro")
                .unitPrice(800.0)
                .category(electronicsCategory)
                .build();
        productRepository.saveAndFlush(smartphone);

        tablet = Product.builder()
                .name("Tablet Device")
                .unitPrice(400.0)
                .category(electronicsCategory)
                .build();
        productRepository.saveAndFlush(tablet);

        tshirt = Product.builder()
                .name("Cotton T-Shirt")
                .unitPrice(25.0)
                .category(clothingCategory)
                .build();
        productRepository.saveAndFlush(tshirt);

        outOfStockProduct = Product.builder()
                .name("Out of Stock Item")
                .unitPrice(100.0)
                .category(electronicsCategory)
                .build();
        productRepository.saveAndFlush(outOfStockProduct);

        // Create inventories with different availability and set bidirectional relationships
        Inventory laptopInventory = Inventory.builder()
                .product(laptop)
                .onHand(10)
                .reserved(0)
                .build();
        laptop.setInventory(laptopInventory);
        inventoryRepository.saveAndFlush(laptopInventory);

        Inventory smartphoneInventory = Inventory.builder()
                .product(smartphone)
                .onHand(25)
                .reserved(5) // Some reserved
                .build();
        smartphone.setInventory(smartphoneInventory);
        inventoryRepository.saveAndFlush(smartphoneInventory);

        Inventory tabletInventory = Inventory.builder()
                .product(tablet)
                .onHand(5)
                .reserved(0)
                .build();
        tablet.setInventory(tabletInventory);
        inventoryRepository.saveAndFlush(tabletInventory);

        Inventory tshirtInventory = Inventory.builder()
                .product(tshirt)
                .onHand(50)
                .reserved(10)
                .build();
        tshirt.setInventory(tshirtInventory);
        inventoryRepository.saveAndFlush(tshirtInventory);

        Inventory outOfStockInventory = Inventory.builder()
                .product(outOfStockProduct)
                .onHand(0) // Out of stock
                .reserved(0)
                .build();
        outOfStockProduct.setInventory(outOfStockInventory);
        inventoryRepository.saveAndFlush(outOfStockInventory);

        // Create a deal for testing
        activeDeal = Deal.builder()
                .name("Electronics Sale")
                .dealCode("ELECTRONICS20")
                .dealType(Deal.DealType.SIMPLE_DISCOUNT)
                .discountType(Deal.DiscountType.PERCENTAGE)
                .discountAmount(20.0)
                .startAt(LocalDateTime.now().minusDays(1))
                .endAt(LocalDateTime.now().plusDays(7))
                .usageLimit(100)
                .usageCount(0)
                .build();
        dealRepository.saveAndFlush(activeDeal);

        DealProduct dealProduct = DealProduct.builder()
                .deal(activeDeal)
                .product(smartphone)
                .build();
        dealProductRepository.saveAndFlush(dealProduct);
    }

    @Test
    @DisplayName("Should successfully add item to cart")
    void shouldSuccessfullyAddItemToCart() throws Exception {
        // Given
        CartItemUpsertForm cartItemForm = new CartItemUpsertForm();
        cartItemForm.setProductId(laptop.getId());
        cartItemForm.setQuantity(2);

        // When & Then
        mockMvc.perform(post("/user/cart/add")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemForm)))
                .andExpect(status().isOk());

        // Verify cart item was created
        Cart updatedCart = cartRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedCart.getCartItems()).hasSize(1);
        
        CartItem cartItem = updatedCart.getCartItems().get(0);
        assertThat(cartItem.getProduct().getId()).isEqualTo(laptop.getId());
        assertThat(cartItem.getQuantity()).isEqualTo(2);
        assertThat(cartItem.getUnitPrice()).isEqualTo(1500.0); // Original price, no deal
        assertThat(cartItem.getDeal()).isNull(); // No deal applied to laptop
    }

    @Test
    @DisplayName("Should add item to cart with deal applied")
    void shouldAddItemToCartWithDealApplied() throws Exception {
        // Given
        CartItemUpsertForm cartItemForm = new CartItemUpsertForm();
        cartItemForm.setProductId(smartphone.getId());
        cartItemForm.setQuantity(1);

        // When & Then
        mockMvc.perform(post("/user/cart/add")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemForm)))
                .andExpect(status().isOk());

        // Verify cart item was created with deal applied
        Cart updatedCart = cartRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedCart.getCartItems()).hasSize(1);
        
        CartItem cartItem = updatedCart.getCartItems().get(0);
        assertThat(cartItem.getProduct().getId()).isEqualTo(smartphone.getId());
        assertThat(cartItem.getQuantity()).isEqualTo(1);
        assertThat(cartItem.getDeal()).isNotNull();
        assertThat(cartItem.getDeal().getId()).isEqualTo(activeDeal.getId());
        // Unit price should be discounted: 800 * 0.8 = 640
        assertThat(cartItem.getUnitPrice()).isEqualTo(640.0);
    }

    @Test
    @DisplayName("Should update quantity when adding same product again")
    void shouldUpdateQuantityWhenAddingSameProductAgain() throws Exception {
        // Given - Add item first time
        CartItemUpsertForm firstAdd = new CartItemUpsertForm();
        firstAdd.setProductId(tablet.getId());
        firstAdd.setQuantity(2);

        mockMvc.perform(post("/user/cart/add")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstAdd)))
                .andExpect(status().isOk());

        // When - Add same item again
        CartItemUpsertForm secondAdd = new CartItemUpsertForm();
        secondAdd.setProductId(tablet.getId());
        secondAdd.setQuantity(1);

        mockMvc.perform(post("/user/cart/add")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondAdd)))
                .andExpect(status().isOk());

        // Then - Verify quantity was updated, not duplicated
        Cart updatedCart = cartRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedCart.getCartItems()).hasSize(1); // Still only one item
        
        CartItem cartItem = updatedCart.getCartItems().get(0);
        assertThat(cartItem.getProduct().getId()).isEqualTo(tablet.getId());
        assertThat(cartItem.getQuantity()).isEqualTo(3); // 2 + 1 = 3
    }

    @Test
    @DisplayName("Should handle adding multiple different products")
    void shouldHandleAddingMultipleDifferentProducts() throws Exception {
        // Given - Add first product
        CartItemUpsertForm laptopForm = new CartItemUpsertForm();
        laptopForm.setProductId(laptop.getId());
        laptopForm.setQuantity(1);

        mockMvc.perform(post("/user/cart/add")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(laptopForm)))
                .andExpect(status().isOk());

        // When - Add second product
        CartItemUpsertForm tshirtForm = new CartItemUpsertForm();
        tshirtForm.setProductId(tshirt.getId());
        tshirtForm.setQuantity(3);

        mockMvc.perform(post("/user/cart/add")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tshirtForm)))
                .andExpect(status().isOk());

        // Then - Verify both items are in cart
        Cart updatedCart = cartRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedCart.getCartItems()).hasSize(2);
        
        // Verify laptop item
        CartItem laptopItem = updatedCart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(laptop.getId()))
                .findFirst().orElseThrow();
        assertThat(laptopItem.getQuantity()).isEqualTo(1);
        assertThat(laptopItem.getUnitPrice()).isEqualTo(1500.0);

        // Verify t-shirt item
        CartItem tshirtItem = updatedCart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(tshirt.getId()))
                .findFirst().orElseThrow();
        assertThat(tshirtItem.getQuantity()).isEqualTo(3);
        assertThat(tshirtItem.getUnitPrice()).isEqualTo(25.0);
    }

    @Test
    @DisplayName("Should return 401 when not authenticated")
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        // Given
        CartItemUpsertForm cartItemForm = new CartItemUpsertForm();
        cartItemForm.setProductId(laptop.getId());
        cartItemForm.setQuantity(1);

        // When & Then - No Authorization header
        mockMvc.perform(post("/user/cart/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemForm)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 401 when using invalid token")
    void shouldReturn401WhenUsingInvalidToken() throws Exception {
        // Given
        CartItemUpsertForm cartItemForm = new CartItemUpsertForm();
        cartItemForm.setProductId(laptop.getId());
        cartItemForm.setQuantity(1);

        // When & Then - Invalid token
        mockMvc.perform(post("/user/cart/add")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemForm)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 401 when using malformed JWT token")
    void shouldReturn401WhenUsingMalformedJwtToken() throws Exception {
        // Given
        CartItemUpsertForm cartItemForm = new CartItemUpsertForm();
        cartItemForm.setProductId(laptop.getId());
        cartItemForm.setQuantity(1);

        // When & Then - Malformed JWT (missing periods)
        mockMvc.perform(post("/user/cart/add")
                .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiIsInR5cCImalformed")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemForm)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 401 when using expired token")
    void shouldReturn401WhenUsingExpiredToken() throws Exception {
        // Given
        CartItemUpsertForm cartItemForm = new CartItemUpsertForm();
        cartItemForm.setProductId(laptop.getId());
        cartItemForm.setQuantity(1);

        // When & Then - Use a deliberately expired JWT token format (this will be invalid/malformed for testing)
        mockMvc.perform(post("/user/cart/add")
                .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.invalid_signature")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemForm)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 400 when request body is malformed")
    void shouldReturn400WhenRequestBodyIsMalformed() throws Exception {
        // Given - Malformed JSON
        String malformedJson = "{ invalid json }";

        // When & Then
        mockMvc.perform(post("/user/cart/add")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when required fields are missing")
    void shouldReturn400WhenRequiredFieldsAreMissing() throws Exception {
        // Given - Missing productId
        CartItemUpsertForm cartItemForm = new CartItemUpsertForm();
        cartItemForm.setQuantity(1);
        // productId is null

        // When & Then
        mockMvc.perform(post("/user/cart/add")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemForm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when quantity is invalid")
    void shouldReturn400WhenQuantityIsInvalid() throws Exception {
        // Given - Quantity is 0 (should be minimum 1)
        CartItemUpsertForm cartItemForm = new CartItemUpsertForm();
        cartItemForm.setProductId(laptop.getId());
        cartItemForm.setQuantity(0);

        // When & Then
        mockMvc.perform(post("/user/cart/add")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemForm)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 404 when product does not exist")
    void shouldReturn404WhenProductDoesNotExist() throws Exception {
        // Given
        CartItemUpsertForm cartItemForm = new CartItemUpsertForm();
        cartItemForm.setProductId("non-existent-product-id");
        cartItemForm.setQuantity(1);

        // When & Then
        mockMvc.perform(post("/user/cart/add")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemForm)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should handle insufficient stock gracefully")
    void shouldHandleInsufficientStockGracefully() throws Exception {
        // Given - Try to add more than available stock (tablet has 5 available)
        CartItemUpsertForm cartItemForm = new CartItemUpsertForm();
        cartItemForm.setProductId(tablet.getId());
        cartItemForm.setQuantity(10); // More than available (5)

        // When & Then - Depends on business logic: could return 400 or still add with available quantity
        // Assuming business logic allows partial fulfillment or returns error
        mockMvc.perform(post("/user/cart/add")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemForm)))
                .andExpect(status().isConflict()); // Assuming it returns 400 for insufficient stock
    }

    @Test
    @DisplayName("Should handle out of stock product")
    void shouldHandleOutOfStockProduct() throws Exception {
        // Given
        CartItemUpsertForm cartItemForm = new CartItemUpsertForm();
        cartItemForm.setProductId(outOfStockProduct.getId());
        cartItemForm.setQuantity(1);

        // When & Then
        mockMvc.perform(post("/user/cart/add")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemForm)))
                .andExpect(status().isConflict()); // Assuming it returns 400 for out of stock
    }

    @Test
    @DisplayName("Should handle large quantity addition")
    void shouldHandleLargeQuantityAddition() throws Exception {
        // Given - Add large quantity within stock limits
        CartItemUpsertForm cartItemForm = new CartItemUpsertForm();
        cartItemForm.setProductId(tshirt.getId());
        cartItemForm.setQuantity(40); // tshirt has 40 available (50 - 10 reserved)

        // When & Then
        mockMvc.perform(post("/user/cart/add")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemForm)))
                .andExpect(status().isOk());

        // Verify cart item was created with correct quantity
        Cart updatedCart = cartRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedCart.getCartItems()).hasSize(1);
        
        CartItem cartItem = updatedCart.getCartItems().get(0);
        assertThat(cartItem.getProduct().getId()).isEqualTo(tshirt.getId());
        assertThat(cartItem.getQuantity()).isEqualTo(40);
    }

    @Test
    @DisplayName("Should handle empty cart initially")
    void shouldHandleEmptyCartInitially() throws Exception {
        // Given - Cart doesn't exist initially (will be created when first item added)
        Optional<Cart> initialCart = cartRepository.findById(testUser.getId());
        assertThat(initialCart).isEmpty(); // No cart exists initially

        CartItemUpsertForm cartItemForm = new CartItemUpsertForm();
        cartItemForm.setProductId(laptop.getId());
        cartItemForm.setQuantity(1);

        // When & Then
        mockMvc.perform(post("/user/cart/add")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemForm)))
                .andExpect(status().isOk());

        // Verify first item was added successfully and cart was created
        Cart updatedCart = cartRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedCart.getCartItems()).hasSize(1);
        assertThat(updatedCart.getCartItems().get(0).getProduct().getId()).isEqualTo(laptop.getId());
    }

    @Test
    @DisplayName("Should handle cart total calculations")
    void shouldHandleCartTotalCalculations() throws Exception {
        // Given - Add multiple items
        CartItemUpsertForm laptopForm = new CartItemUpsertForm();
        laptopForm.setProductId(laptop.getId());
        laptopForm.setQuantity(1);

        CartItemUpsertForm smartphoneForm = new CartItemUpsertForm();
        smartphoneForm.setProductId(smartphone.getId());
        smartphoneForm.setQuantity(2);

        // When - Add both items
        mockMvc.perform(post("/user/cart/add")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(laptopForm)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/user/cart/add")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(smartphoneForm)))
                .andExpect(status().isOk());

        // Then - Verify cart totals
        Cart updatedCart = cartRepository.findById(testUser.getId()).orElseThrow();
        assertThat(updatedCart.getCartItems()).hasSize(2);
        
        // Calculate expected total: laptop (1 * 1500) + smartphone with deal (2 * 640)
        double expectedTotal = 1500.0 + (2 * 640.0); // 1500 + 1280 = 2780
        double actualTotal = updatedCart.getTotalAmount();
        assertThat(actualTotal).isEqualTo(expectedTotal);
        
        // Verify total items count
        int expectedItemCount = 2; // 2 different products
        int actualItemCount = updatedCart.getTotalItems();
        assertThat(actualItemCount).isEqualTo(expectedItemCount);
    }
}