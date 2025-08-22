package me.huypc.elect_shop.seed;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.huypc.elect_shop.entity.Category;
import me.huypc.elect_shop.entity.Inventory;
import me.huypc.elect_shop.entity.Product;
import me.huypc.elect_shop.repository.CategoryRepository;
import me.huypc.elect_shop.repository.InventoryRepository;
import me.huypc.elect_shop.repository.ProductRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductSeeder implements CommandLineRunner {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Seeding products...");

        List<Category> categories = categoryRepository.findAll();

        if (categories.isEmpty()) {
            log.info("No categories found, seeding categories...");
            categories = seedCategories();
        } else {
            log.info("Categories already exist, skipping category seeding...");
        }

        if (productRepository.count() == 0) {
            // seed
            List<Product> seedProducts = seedProducts(categories);
            seedInventory(seedProducts);
            log.info("Products seeded successfully.");
        } else {
            log.info("Products already seeded, skipping...");
        }
    }

    private List<Category> seedCategories() {
        List<Category> categories = List.of(
                Category.builder().name("Electronics").build(),
                Category.builder().name("Home Appliances").build(),
                Category.builder().name("Books").build());

        return categoryRepository.saveAll(categories);
    }

    private List<Product> seedProducts(List<Category> categories) {
        List<Product> products = List.of(
                Product.builder().name("Laptop").unitPrice(999.99).category(categories.get(0)).build(),
                Product.builder().name("Smartphone").unitPrice(499.99).category(categories.get(0)).build(),
                Product.builder().name("Tablet").unitPrice(299.99).category(categories.get(0)).build(),
                Product.builder().name("TV").unitPrice(799.99).category(categories.get(1)).build(),
                Product.builder().name("Refrigerator").unitPrice(1299.99).category(categories.get(1)).build(),
                Product.builder().name("Washing Machine").unitPrice(499.99).category(categories.get(1)).build(),
                Product.builder().name("Fiction Book").unitPrice(19.99).category(categories.get(2)).build(),
                Product.builder().name("Non-Fiction Book").unitPrice(29.99).category(categories.get(2)).build(),
                Product.builder().name("Children's Book").unitPrice(14.99).category(categories.get(2)).build());

        return productRepository.saveAll(products);
    }

    private void seedInventory(List<Product> products) {
        List<Inventory> inventories = new ArrayList<>();
        for (Product product : products) {
            Inventory inventory = Inventory.builder().onHand(100).reserved(0).build();
            inventory.setProduct(product);
            inventories.add(inventory);
        }
        inventoryRepository.saveAll(inventories);
        log.info("Seeded {} inventory records", inventories.size());
    }

}
