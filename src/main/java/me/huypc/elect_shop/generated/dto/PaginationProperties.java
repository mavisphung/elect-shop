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
 * PaginationProperties
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class PaginationProperties {

  private Integer page = 1;

  private Integer size = 10;

  private Integer total;

  public PaginationProperties() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public PaginationProperties(Integer page, Integer size, Integer total) {
    this.page = page;
    this.size = size;
    this.total = total;
  }

  public PaginationProperties page(Integer page) {
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

  public PaginationProperties size(Integer size) {
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

  public PaginationProperties total(Integer total) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaginationProperties paginationProperties = (PaginationProperties) o;
    return Objects.equals(this.page, paginationProperties.page) &&
        Objects.equals(this.size, paginationProperties.size) &&
        Objects.equals(this.total, paginationProperties.total);
  }

  @Override
  public int hashCode() {
    return Objects.hash(page, size, total);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaginationProperties {\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    total: ").append(toIndentedString(total)).append("\n");
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

