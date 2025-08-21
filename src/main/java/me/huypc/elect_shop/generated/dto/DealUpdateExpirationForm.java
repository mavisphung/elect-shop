package me.huypc.elect_shop.generated.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * DealUpdateExpirationForm
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class DealUpdateExpirationForm {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime newStartAt;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime newEndAt;

  public DealUpdateExpirationForm() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DealUpdateExpirationForm(OffsetDateTime newStartAt, OffsetDateTime newEndAt) {
    this.newStartAt = newStartAt;
    this.newEndAt = newEndAt;
  }

  public DealUpdateExpirationForm newStartAt(OffsetDateTime newStartAt) {
    this.newStartAt = newStartAt;
    return this;
  }

  /**
   * New start date and time for the deal
   * @return newStartAt
   */
  @NotNull @Valid 
  @Schema(name = "newStartAt", example = "2024-01-15T10:30:00Z", description = "New start date and time for the deal", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("newStartAt")
  public OffsetDateTime getNewStartAt() {
    return newStartAt;
  }

  public void setNewStartAt(OffsetDateTime newStartAt) {
    this.newStartAt = newStartAt;
  }

  public DealUpdateExpirationForm newEndAt(OffsetDateTime newEndAt) {
    this.newEndAt = newEndAt;
    return this;
  }

  /**
   * New end date and time for the deal
   * @return newEndAt
   */
  @NotNull @Valid 
  @Schema(name = "newEndAt", example = "2024-01-31T23:59:59Z", description = "New end date and time for the deal", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("newEndAt")
  public OffsetDateTime getNewEndAt() {
    return newEndAt;
  }

  public void setNewEndAt(OffsetDateTime newEndAt) {
    this.newEndAt = newEndAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DealUpdateExpirationForm dealUpdateExpirationForm = (DealUpdateExpirationForm) o;
    return Objects.equals(this.newStartAt, dealUpdateExpirationForm.newStartAt) &&
        Objects.equals(this.newEndAt, dealUpdateExpirationForm.newEndAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(newStartAt, newEndAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DealUpdateExpirationForm {\n");
    sb.append("    newStartAt: ").append(toIndentedString(newStartAt)).append("\n");
    sb.append("    newEndAt: ").append(toIndentedString(newEndAt)).append("\n");
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

