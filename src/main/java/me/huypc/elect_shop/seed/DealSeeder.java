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
                        .dealType(Deal.DealType.SIMPLE_DISCOUNT)
                        .startAt(LocalDateTime.now().minusDays(1))
                        .endAt(LocalDateTime.now().plusDays(30))
                        .usageCount(0)
                        .usageLimit(100)
                        .build(),
                Deal.builder()
                        .name("Winter deal UPTO 50%")
                        .dealCode("WINTER50")
                        .discountType(Deal.DiscountType.PERCENTAGE)
                        .dealType(Deal.DealType.SIMPLE_DISCOUNT)
                        .discountAmount(50.0)
                        .startAt(LocalDateTime.now().minusDays(1))
                        .endAt(LocalDateTime.now().plusDays(30))
                        .usageCount(0)
                        .usageLimit(50)
                        .build(),
                Deal.builder()
                        .name("Spring deal Buy 1 Get 1 Free")
                        .dealCode("SPRINGBOGO")
                        .dealType(Deal.DealType.BUY_ONE_GET_ONE)
                        .buyQuantity(1)
                        .getQuantity(1)
                        .startAt(LocalDateTime.now().minusDays(1))
                        .endAt(LocalDateTime.now().plusDays(30))
                        .usageCount(0)
                        .usageLimit(10)
                        .build(),
                Deal.builder()
                        .name("Spring deal Buy 1 50% off on 2nd")
                        .dealCode("SPRING1502")
                        .dealType(Deal.DealType.BUY_X_GET_Y_OFF)
                        .buyQuantity(1)
                        .getQuantity(1)
                        .discountType(Deal.DiscountType.PERCENTAGE)
                        .discountAmount(50.0)
                        .startAt(LocalDateTime.now().minusDays(1))
                        .endAt(LocalDateTime.now().plusDays(30))
                        .usageCount(0)
                        .usageLimit(10)
                        .build()
        );

        dealRepository.saveAll(deals);
    }

}
