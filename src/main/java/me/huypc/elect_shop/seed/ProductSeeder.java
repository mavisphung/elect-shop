package me.huypc.elect_shop.seed;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.huypc.elect_shop.entity.Category;
import me.huypc.elect_shop.entity.Product;
import me.huypc.elect_shop.repository.CategoryRepository;
import me.huypc.elect_shop.repository.ProductRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
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
            seedProducts(categories);
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

    private void seedProducts(List<Category> categories) {
        List<Product> products = List.of(
                Product.builder().name("Laptop").stock(10).unitPrice(999.99).category(categories.get(0)).build(),
                Product.builder().name("Smartphone").stock(20).unitPrice(499.99).category(categories.get(0)).build(),
                Product.builder().name("Tablet").stock(15).unitPrice(299.99).category(categories.get(0)).build(),
                Product.builder().name("TV").stock(5).unitPrice(799.99).category(categories.get(1)).build(),
                Product.builder().name("Refrigerator").stock(3).unitPrice(1299.99).category(categories.get(1)).build(),
                Product.builder().name("Washing Machine").stock(7).unitPrice(499.99).category(categories.get(1)).build(),
                Product.builder().name("Fiction Book").stock(25).unitPrice(19.99).category(categories.get(2)).build(),
                Product.builder().name("Non-Fiction Book").stock(30).unitPrice(29.99).category(categories.get(2)).build(),
                Product.builder().name("Children's Book").stock(20).unitPrice(14.99).category(categories.get(2)).build());

        productRepository.saveAll(products);
    }
}
