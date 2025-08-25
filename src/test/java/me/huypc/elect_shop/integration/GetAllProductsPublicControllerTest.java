package me.huypc.elect_shop.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import me.huypc.elect_shop.entity.Category;
import me.huypc.elect_shop.entity.Deal;
import me.huypc.elect_shop.entity.DealProduct;
import me.huypc.elect_shop.entity.Inventory;
import me.huypc.elect_shop.entity.Product;
import me.huypc.elect_shop.generated.dto.ProductFilterForm;
import me.huypc.elect_shop.generated.dto.ProductListDto;
import me.huypc.elect_shop.repository.CategoryRepository;
import me.huypc.elect_shop.repository.DealProductRepository;
import me.huypc.elect_shop.repository.DealRepository;
import me.huypc.elect_shop.repository.InventoryRepository;
import me.huypc.elect_shop.repository.ProductRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Get All Products Public API Integration Tests")
class GetAllProductsPublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    private Category electronicsCategory;
    private Category clothingCategory;
    private Product laptop;
    private Product smartphone;
    private Product tablet;
    private Product tshirt;
    private Product expensiveProduct;

    private void cleanUp() {
        // Clean up any existing data in proper order (child to parent)
        dealProductRepository.deleteAll();
        dealRepository.deleteAll();
        inventoryRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @BeforeEach
    void setUp() {
        cleanUp();

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

        expensiveProduct = Product.builder()
                .name("Luxury Watch")
                .unitPrice(5000.0)
                .category(clothingCategory)
                .build();
        productRepository.saveAndFlush(expensiveProduct);

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
                .onHand(0) // Out of stock
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

        Inventory expensiveInventory = Inventory.builder()
                .product(expensiveProduct)
                .onHand(5)
                .reserved(0)
                .build();
        expensiveProduct.setInventory(expensiveInventory);
        inventoryRepository.saveAndFlush(expensiveInventory);

        // Create a deal for testing
        Deal testDeal = Deal.builder()
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
        dealRepository.saveAndFlush(testDeal);

        DealProduct dealProduct = DealProduct.builder()
                .deal(testDeal)
                .product(smartphone)
                .build();
        dealProductRepository.saveAndFlush(dealProduct);
    }

    @Test
    @DisplayName("Should return all products with default pagination")
    void shouldReturnAllProductsWithDefaultPagination() throws Exception {
        // Given
        ProductFilterForm filterForm = new ProductFilterForm();
        // Use defaults: page=1, size=10

        // When & Then
        MvcResult result = mockMvc.perform(post("/public/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filterForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.total").value(5)) // 5 products total
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(5))
                .andReturn();

        // Verify response structure
        String responseContent = result.getResponse().getContentAsString();
        ProductListDto response = objectMapper.readValue(responseContent, ProductListDto.class);
        
        assertThat(response.getItems()).hasSize(5);
        assertThat(response.getItems().stream().map(item -> item.getName()))
                .containsExactlyInAnyOrder("Gaming Laptop", "Smartphone Pro", "Tablet Device", "Cotton T-Shirt", "Luxury Watch");

        // Verify inventory stock calculation (available = onHand - reserved)
        response.getItems().forEach(item -> {
            switch (item.getName()) {
                case "Gaming Laptop" -> assertThat(item.getStock()).isEqualTo(10); // 10 - 0
                case "Smartphone Pro" -> assertThat(item.getStock()).isEqualTo(20); // 25 - 5
                case "Tablet Device" -> assertThat(item.getStock()).isEqualTo(0); // 0 - 0
                case "Cotton T-Shirt" -> assertThat(item.getStock()).isEqualTo(40); // 50 - 10
                case "Luxury Watch" -> assertThat(item.getStock()).isEqualTo(5); // 5 - 0
            }
        });
    }

    @Test
    @DisplayName("Should filter products by search text")
    void shouldFilterProductsBySearchText() throws Exception {
        // Given
        ProductFilterForm filterForm = new ProductFilterForm();
        filterForm.setSearchText("Smartphone");

        // When & Then
        mockMvc.perform(post("/public/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filterForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Smartphone Pro"))
                .andExpect(jsonPath("$.items[0].unitPrice").value(800.0))
                .andExpect(jsonPath("$.items[0].stock").value(20));
    }

    @Test
    @DisplayName("Should filter products by category")
    void shouldFilterProductsByCategory() throws Exception {
        // Given
        ProductFilterForm filterForm = new ProductFilterForm();
        filterForm.setCategory(electronicsCategory.getId());

        // When & Then
        MvcResult result = mockMvc.perform(post("/public/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filterForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3)) // 3 electronics products
                .andExpect(jsonPath("$.items.length()").value(3))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        ProductListDto response = objectMapper.readValue(responseContent, ProductListDto.class);
        
        assertThat(response.getItems().stream().map(item -> item.getName()))
                .containsExactlyInAnyOrder("Gaming Laptop", "Smartphone Pro", "Tablet Device");
    }

    @Test
    @DisplayName("Should filter products by price range")
    void shouldFilterProductsByPriceRange() throws Exception {
        // Given
        ProductFilterForm filterForm = new ProductFilterForm();
        filterForm.setMinPrice(100.0);
        filterForm.setMaxPrice(1000.0);

        // When & Then
        MvcResult result = mockMvc.perform(post("/public/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filterForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2)) // Smartphone and Tablet
                .andExpect(jsonPath("$.items.length()").value(2))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        ProductListDto response = objectMapper.readValue(responseContent, ProductListDto.class);
        
        assertThat(response.getItems().stream().map(item -> item.getName()))
                .containsExactlyInAnyOrder("Smartphone Pro", "Tablet Device");
        
        // Verify prices are within range
        response.getItems().forEach(item -> {
            assertThat(item.getUnitPrice()).isBetween(100.0, 1000.0);
        });
    }

    @Test
    @DisplayName("Should filter products by availability")
    void shouldFilterProductsByAvailability() throws Exception {
        // Given
        ProductFilterForm filterForm = new ProductFilterForm();
        filterForm.setIsAvailable(true); // Only available products

        // When & Then
        MvcResult result = mockMvc.perform(post("/public/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filterForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(4)) // All except tablet (out of stock)
                .andExpect(jsonPath("$.items.length()").value(4))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        ProductListDto response = objectMapper.readValue(responseContent, ProductListDto.class);
        
        // Verify all returned products have stock > 0
        response.getItems().forEach(item -> {
            assertThat(item.getStock()).isGreaterThan(0);
            assertThat(item.getName()).isNotEqualTo("Tablet Device"); // Should not include out of stock
        });
    }

    @Test
    @DisplayName("Should filter products by unavailability")
    void shouldFilterProductsByUnavailability() throws Exception {
        // Given
        ProductFilterForm filterForm = new ProductFilterForm();
        filterForm.setIsAvailable(false); // Only unavailable products

        // When & Then
        mockMvc.perform(post("/public/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filterForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1)) // Only tablet
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Tablet Device"))
                .andExpect(jsonPath("$.items[0].stock").value(0));
    }

    @Test
    @DisplayName("Should handle pagination correctly")
    void shouldHandlePaginationCorrectly() throws Exception {
        // Given - Request page 2 with size 2
        ProductFilterForm filterForm = new ProductFilterForm();
        filterForm.setPage(2);
        filterForm.setSize(2);

        // When & Then
        mockMvc.perform(post("/public/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filterForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.total").value(5))
                .andExpect(jsonPath("$.items.length()").value(2)); // 2 items on page 2
    }

    @Test
    @DisplayName("Should handle combined filters")
    void shouldHandleCombinedFilters() throws Exception {
        // Given - Combine multiple filters
        ProductFilterForm filterForm = new ProductFilterForm();
        filterForm.setCategory(electronicsCategory.getId());
        filterForm.setMinPrice(500.0);
        filterForm.setIsAvailable(true);

        // When & Then
        MvcResult result = mockMvc.perform(post("/public/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filterForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2)) // Laptop and Smartphone
                .andExpect(jsonPath("$.items.length()").value(2))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        ProductListDto response = objectMapper.readValue(responseContent, ProductListDto.class);
        
        // Verify all conditions are met
        response.getItems().forEach(item -> {
            assertThat(item.getUnitPrice()).isGreaterThanOrEqualTo(500.0);
            assertThat(item.getStock()).isGreaterThan(0);
        });
        
        assertThat(response.getItems().stream().map(item -> item.getName()))
                .containsExactlyInAnyOrder("Gaming Laptop", "Smartphone Pro");
    }

    @Test
    @DisplayName("Should return empty result when no products match filter")
    void shouldReturnEmptyResultWhenNoProductsMatchFilter() throws Exception {
        // Given
        ProductFilterForm filterForm = new ProductFilterForm();
        filterForm.setSearchText("NonExistentProduct");

        // When & Then
        mockMvc.perform(post("/public/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filterForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    @DisplayName("Should handle invalid pagination gracefully")
    void shouldHandleInvalidPaginationGracefully() throws Exception {
        // Given - Request page beyond available data
        ProductFilterForm filterForm = new ProductFilterForm();
        filterForm.setPage(10); // Page that doesn't exist
        filterForm.setSize(10);

        // When & Then
        mockMvc.perform(post("/public/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filterForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(10))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.total").value(5))
                .andExpect(jsonPath("$.items.length()").value(0)); // Empty page
    }

    @Test
    @DisplayName("Should validate required fields in response")
    void shouldValidateRequiredFieldsInResponse() throws Exception {
        // Given
        ProductFilterForm filterForm = new ProductFilterForm();
        filterForm.setSize(1); // Get just one product

        // When & Then
        MvcResult result = mockMvc.perform(post("/public/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filterForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$.size").exists())
                .andExpect(jsonPath("$.total").exists())
                .andExpect(jsonPath("$.items").exists())
                .andExpect(jsonPath("$.items[0].id").exists())
                .andExpect(jsonPath("$.items[0].name").exists())
                .andExpect(jsonPath("$.items[0].stock").exists())
                .andExpect(jsonPath("$.items[0].unitPrice").exists())
                .andReturn();

        // Verify data types
        String responseContent = result.getResponse().getContentAsString();
        ProductListDto response = objectMapper.readValue(responseContent, ProductListDto.class);
        
        assertThat(response.getPage()).isInstanceOf(Integer.class);
        assertThat(response.getSize()).isInstanceOf(Integer.class);
        assertThat(response.getTotal()).isInstanceOf(Integer.class);
        assertThat(response.getItems()).isNotNull();
        
        if (!response.getItems().isEmpty()) {
            var firstItem = response.getItems().get(0);
            assertThat(firstItem.getId()).isInstanceOf(String.class);
            assertThat(firstItem.getName()).isInstanceOf(String.class);
            assertThat(firstItem.getStock()).isInstanceOf(Integer.class);
            assertThat(firstItem.getUnitPrice()).isInstanceOf(Double.class);
        }
    }

    @Test
    @DisplayName("Should handle malformed request body")
    void shouldHandleMalformedRequestBody() throws Exception {
        // Given - Malformed JSON
        String malformedJson = "{ invalid json }";

        // When & Then
        mockMvc.perform(post("/public/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing request body")
    void shouldHandleMissingRequestBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/public/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}