package me.huypc.elect_shop.seed;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.huypc.elect_shop.entity.Deal;
import me.huypc.elect_shop.repository.DealRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class DealSeeder implements CommandLineRunner {

    private final DealRepository dealRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Seeding deals...");

        if (dealRepository.count() == 0) {
            // seed
            seedDeals();
            log.info("Deals seeded successfully.");
        } else {
            log.info("Deals already seeded, skipping...");
        }
    }

    private void seedDeals() {
        List<Deal> deals = List.of(
                Deal.builder()
                        .name("Summer deal UPTO 20%")
                        .dealCode("SUMMER20")
                        .discountType(Deal.DiscountType.PERCENTAGE)
                        .discountAmount(20.0)
                        .startAt(LocalDateTime.now().minusDays(1))
                        .endAt(LocalDateTime.now().plusDays(30))
                        .usageCount(0)
                        .usageLimit(100)
                        .build(),
                Deal.builder()
                        .name("Winter deal UPTO 50%")
                        .dealCode("WINTER50")
                        .discountType(Deal.DiscountType.PERCENTAGE)
                        .discountAmount(50.0)
                        .startAt(LocalDateTime.now().minusDays(1))
                        .endAt(LocalDateTime.now().plusDays(30))
                        .usageCount(0)
                        .usageLimit(50)
                        .build()
        );

        dealRepository.saveAll(deals);
    }

}
