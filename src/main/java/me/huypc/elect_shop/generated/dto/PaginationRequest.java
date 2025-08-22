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
 * PaginationRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class PaginationRequest {

  private Integer page = 1;

  private Integer size = 10;

  private @Nullable String searchText;

  public PaginationRequest page(Integer page) {
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

  public PaginationRequest size(Integer size) {
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

  public PaginationRequest searchText(@Nullable String searchText) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaginationRequest paginationRequest = (PaginationRequest) o;
    return Objects.equals(this.page, paginationRequest.page) &&
        Objects.equals(this.size, paginationRequest.size) &&
        Objects.equals(this.searchText, paginationRequest.searchText);
  }

  @Override
  public int hashCode() {
    return Objects.hash(page, size, searchText);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaginationRequest {\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
    sb.append("    searchText: ").append(toIndentedString(searchText)).append("\n");
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

