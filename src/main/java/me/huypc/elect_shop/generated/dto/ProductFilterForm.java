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
 * ProductFilterForm
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ProductFilterForm {

  private Integer page = 1;

  private Integer size = 10;

  private @Nullable String searchText;

  private @Nullable String category;

  private @Nullable Double minPrice;

  private @Nullable Double maxPrice;

  private @Nullable Boolean isAvailable;

  public ProductFilterForm page(Integer page) {
    this.page = page;
    return this;
  }

  /**
   * Page number to retrieve
   * @return page
   */
  
  @Schema(name = "page", example = "1", description = "Page number to retrieve", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("page")
  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public ProductFilterForm size(Integer size) {
    this.size = size;
    return this;
  }

  /**
   * Number of items per page
   * @return size
   */
  
  @Schema(name = "size", example = "10", description = "Number of items per page", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("size")
  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  public ProductFilterForm searchText(@Nullable String searchText) {
    this.searchText = searchText;
    return this;
  }

  /**
   * Search term to filter products by name
   * @return searchText
   */
  
  @Schema(name = "searchText", example = "Smart TV", description = "Search term to filter products by name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("searchText")
  public @Nullable String getSearchText() {
    return searchText;
  }

  public void setSearchText(@Nullable String searchText) {
    this.searchText = searchText;
  }

  public ProductFilterForm category(@Nullable String category) {
    this.category = category;
    return this;
  }

  /**
   * Filter products by category
   * @return category
   */
  
  @Schema(name = "category", example = "category-id", description = "Filter products by category", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("category")
  public @Nullable String getCategory() {
    return category;
  }

  public void setCategory(@Nullable String category) {
    this.category = category;
  }

  public ProductFilterForm minPrice(@Nullable Double minPrice) {
    this.minPrice = minPrice;
    return this;
  }

  /**
   * Minimum price filter
   * minimum: 0.0
   * @return minPrice
   */
  @DecimalMin("0.0") 
  @Schema(name = "minPrice", example = "100.0", description = "Minimum price filter", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("minPrice")
  public @Nullable Double getMinPrice() {
    return minPrice;
  }

  public void setMinPrice(@Nullable Double minPrice) {
    this.minPrice = minPrice;
  }

  public ProductFilterForm maxPrice(@Nullable Double maxPrice) {
    this.maxPrice = maxPrice;
    return this;
  }

  /**
   * Maximum price filter
   * minimum: 0.0
   * @return maxPrice
   */
  @DecimalMin("0.0") 
  @Schema(name = "maxPrice", example = "1000.0", description = "Maximum price filter", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxPrice")
  public @Nullable Double getMaxPrice() {
    return maxPrice;
  }

  public void setMaxPrice(@Nullable Double maxPrice) {
    this.maxPrice = maxPrice;
  }

  public ProductFilterForm isAvailable(@Nullable Boolean isAvailable) {
    this.isAvailable = isAvailable;
    return this;
  }

  /**
   * Filter products by quantity > 0
   * @return isAvailable
   */
  
  @Schema(name = "isAvailable", example = "true", description = "Filter products by quantity > 0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isAvailable")
  public @Nullable Boolean getIsAvailable() {
    return isAvailable;
  }

  public void setIsAvailable(@Nullable Boolean isAvailable) {
    this.isAvailable = isAvailable;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProductFilterForm productFilterForm = (ProductFilterForm) o;
    return Objects.equals(this.page, productFilterForm.page) &&
        Objects.equals(this.size, productFilterForm.size) &&
        Objects.equals(this.searchText, productFilterForm.searchText) &&
        Objects.equals(this.category, productFilterForm.category) &&
        Objects.equals(this.minPrice, productFilterForm.minPrice) &&
        Objects.equals(this.maxPrice, productFilterForm.maxPrice) &&
        Objects.equals(this.isAvailable, productFilterForm.isAvailable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(page, size, searchText, category, minPrice, maxPrice, isAvailable);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProductFilterForm {\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    searchText: ").append(toIndentedString(searchText)).append("\n");
    sb.append("    category: ").append(toIndentedString(category)).append("\n");
    sb.append("    minPrice: ").append(toIndentedString(minPrice)).append("\n");
    sb.append("    maxPrice: ").append(toIndentedString(maxPrice)).append("\n");
    sb.append("    isAvailable: ").append(toIndentedString(isAvailable)).append("\n");
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

