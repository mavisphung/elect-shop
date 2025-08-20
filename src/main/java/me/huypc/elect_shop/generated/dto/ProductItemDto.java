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
 * ProductItemDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ProductItemDto {

  private String id;

  private String name;

  private Integer stock;

  private Double unitPrice;

  public ProductItemDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ProductItemDto(String id, String name, Integer stock, Double unitPrice) {
    this.id = id;
    this.name = name;
    this.stock = stock;
    this.unitPrice = unitPrice;
  }

  public ProductItemDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique identifier for the product
   * @return id
   */
  @NotNull 
  @Schema(name = "id", example = "12345", description = "Unique identifier for the product", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ProductItemDto name(String name) {
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

  public ProductItemDto stock(Integer stock) {
    this.stock = stock;
    return this;
  }

  /**
   * Available stock quantity
   * @return stock
   */
  @NotNull 
  @Schema(name = "stock", example = "50", description = "Available stock quantity", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("stock")
  public Integer getStock() {
    return stock;
  }

  public void setStock(Integer stock) {
    this.stock = stock;
  }

  public ProductItemDto unitPrice(Double unitPrice) {
    this.unitPrice = unitPrice;
    return this;
  }

  /**
   * Price per unit of the product
   * @return unitPrice
   */
  @NotNull 
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
    ProductItemDto productItemDto = (ProductItemDto) o;
    return Objects.equals(this.id, productItemDto.id) &&
        Objects.equals(this.name, productItemDto.name) &&
        Objects.equals(this.stock, productItemDto.stock) &&
        Objects.equals(this.unitPrice, productItemDto.unitPrice);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, stock, unitPrice);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProductItemDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

