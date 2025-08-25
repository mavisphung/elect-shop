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
 * OrderPlaceForm
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class OrderPlaceForm {

  private String shippingAddress;

  public OrderPlaceForm() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public OrderPlaceForm(String shippingAddress) {
    this.shippingAddress = shippingAddress;
  }

  public OrderPlaceForm shippingAddress(String shippingAddress) {
    this.shippingAddress = shippingAddress;
    return this;
  }

  /**
   * Shipping address for the order
   * @return shippingAddress
   */
  @NotNull 
  @Schema(name = "shippingAddress", example = "123 Main St, Anytown, USA", description = "Shipping address for the order", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("shippingAddress")
  public String getShippingAddress() {
    return shippingAddress;
  }

  public void setShippingAddress(String shippingAddress) {
    this.shippingAddress = shippingAddress;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrderPlaceForm orderPlaceForm = (OrderPlaceForm) o;
    return Objects.equals(this.shippingAddress, orderPlaceForm.shippingAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(shippingAddress);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrderPlaceForm {\n");
    sb.append("    shippingAddress: ").append(toIndentedString(shippingAddress)).append("\n");
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

