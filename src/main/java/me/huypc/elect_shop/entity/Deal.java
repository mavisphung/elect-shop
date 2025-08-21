package me.huypc.elect_shop.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "deals")
public class Deal {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String dealCode;

    @Column
    private Double discountAmount;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    public enum DiscountType {
        PERCENTAGE,
        AMOUNT,
    }

    @Column
    private LocalDateTime startAt;

    @Column
    private LocalDateTime endAt;

    @Column
    private Integer usageLimit;

    @Column
    private Integer usageCount;

    @ToString.Exclude
    @OneToMany(mappedBy = "deal", fetch = FetchType.LAZY)
    private List<DealProduct> dealProducts;
}
