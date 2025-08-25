package me.huypc.elect_shop.generated.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ProductUpsertDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ProductUpsertDto {

  private String name;

  private Integer stock;

  private Double unitPrice;

  public ProductUpsertDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ProductUpsertDto(String name, Integer stock, Double unitPrice) {
    this.name = name;
    this.stock = stock;
    this.unitPrice = unitPrice;
  }

  public ProductUpsertDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the product
   * @return name
   */
  @NotNull 
  @Schema(name = "name", example = "Smart TV 55\"", description = "Name of the product", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ProductUpsertDto stock(Integer stock) {
    this.stock = stock;
    return this;
  }

  /**
   * Available stock quantity
   * minimum: 1
   * @return stock
   */
  @NotNull @Min(1) 
  @Schema(name = "stock", example = "50", description = "Available stock quantity", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("stock")
  public Integer getStock() {
    return stock;
  }

  public void setStock(Integer stock) {
    this.stock = stock;
  }

  public ProductUpsertDto unitPrice(Double unitPrice) {
    this.unitPrice = unitPrice;
    return this;
  }

  /**
   * Price per unit of the product
   * minimum: 1
   * @return unitPrice
   */
  @NotNull @DecimalMin("1") 
  @Schema(name = "unitPrice", example = "499.99", description = "Price per unit of the product", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("unitPrice")
  public Double getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(Double unitPrice) {
    this.unitPrice = unitPrice;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProductUpsertDto productUpsertDto = (ProductUpsertDto) o;
    return Objects.equals(this.name, productUpsertDto.name) &&
        Objects.equals(this.stock, productUpsertDto.stock) &&
        Objects.equals(this.unitPrice, productUpsertDto.unitPrice);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, stock, unitPrice);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProductUpsertDto {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    stock: ").append(toIndentedString(stock)).append("\n");
    sb.append("    unitPrice: ").append(toIndentedString(unitPrice)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

