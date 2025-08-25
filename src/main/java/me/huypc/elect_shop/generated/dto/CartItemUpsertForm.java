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
 * CartItemUpsertForm
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class CartItemUpsertForm {

  private String productId;

  private Integer quantity;

  public CartItemUpsertForm() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CartItemUpsertForm(String productId, Integer quantity) {
    this.productId = productId;
    this.quantity = quantity;
  }

  public CartItemUpsertForm productId(String productId) {
    this.productId = productId;
    return this;
  }

  /**
   * Unique identifier of the product to add to the cart
   * @return productId
   */
  @NotNull 
  @Schema(name = "productId", example = "12345", description = "Unique identifier of the product to add to the cart", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("productId")
  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public CartItemUpsertForm quantity(Integer quantity) {
    this.quantity = quantity;
    return this;
  }

  /**
   * Quantity of the product to add
   * minimum: 1
   * @return quantity
   */
  @NotNull @Min(1) 
  @Schema(name = "quantity", example = "2", description = "Quantity of the product to add", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("quantity")
  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CartItemUpsertForm cartItemUpsertForm = (CartItemUpsertForm) o;
    return Objects.equals(this.productId, cartItemUpsertForm.productId) &&
        Objects.equals(this.quantity, cartItemUpsertForm.quantity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(productId, quantity);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CartItemUpsertForm {\n");
    sb.append("    productId: ").append(toIndentedString(productId)).append("\n");
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
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

