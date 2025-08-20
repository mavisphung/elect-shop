package me.huypc.elect_shop.generated.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.huypc.elect_shop.generated.dto.ProductItemDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ProductListDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ProductListDto {

  private Integer page = 1;

  private Integer size = 10;

  private Integer total;

  @Valid
  private List<@Valid ProductItemDto> items = new ArrayList<>();

  public ProductListDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ProductListDto(Integer page, Integer size, Integer total) {
    this.page = page;
    this.size = size;
    this.total = total;
  }

  public ProductListDto page(Integer page) {
    this.page = page;
    return this;
  }

  /**
   * Current page number
   * @return page
   */
  @NotNull 
  @Schema(name = "page", example = "1", description = "Current page number", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("page")
  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public ProductListDto size(Integer size) {
    this.size = size;
    return this;
  }

  /**
   * Number of items per page
   * @return size
   */
  @NotNull 
  @Schema(name = "size", example = "10", description = "Number of items per page", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("size")
  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  public ProductListDto total(Integer total) {
    this.total = total;
    return this;
  }

  /**
   * Total number of items available
   * @return total
   */
  @NotNull 
  @Schema(name = "total", example = "100", description = "Total number of items available", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("total")
  public Integer getTotal() {
    return total;
  }

  public void setTotal(Integer total) {
    this.total = total;
  }

  public ProductListDto items(List<@Valid ProductItemDto> items) {
    this.items = items;
    return this;
  }

  public ProductListDto addItemsItem(ProductItemDto itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(itemsItem);
    return this;
  }

  /**
   * Get items
   * @return items
   */
  @Valid 
  @Schema(name = "items", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("items")
  public List<@Valid ProductItemDto> getItems() {
    return items;
  }

  public void setItems(List<@Valid ProductItemDto> items) {
    this.items = items;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProductListDto productListDto = (ProductListDto) o;
    return Objects.equals(this.page, productListDto.page) &&
        Objects.equals(this.size, productListDto.size) &&
        Objects.equals(this.total, productListDto.total) &&
        Objects.equals(this.items, productListDto.items);
  }

  @Override
  public int hashCode() {
    return Objects.hash(page, size, total, items);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProductListDto {\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    total: ").append(toIndentedString(total)).append("\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
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

