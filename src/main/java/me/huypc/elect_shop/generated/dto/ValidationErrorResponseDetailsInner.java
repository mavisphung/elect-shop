package me.huypc.elect_shop.generated.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ValidationErrorResponseDetailsInner
 */

@JsonTypeName("ValidationErrorResponse_details_inner")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ValidationErrorResponseDetailsInner {

  private @Nullable String field;

  private @Nullable String message;

  public ValidationErrorResponseDetailsInner field(@Nullable String field) {
    this.field = field;
    return this;
  }

  /**
   * Field that failed validation
   * @return field
   */
  
  @Schema(name = "field", example = "email", description = "Field that failed validation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("field")
  public @Nullable String getField() {
    return field;
  }

  public void setField(@Nullable String field) {
    this.field = field;
  }

  public ValidationErrorResponseDetailsInner message(@Nullable String message) {
    this.message = message;
    return this;
  }

  /**
   * Validation error message
   * @return message
   */
  
  @Schema(name = "message", example = "must be a valid email address", description = "Validation error message", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("message")
  public @Nullable String getMessage() {
    return message;
  }

  public void setMessage(@Nullable String message) {
    this.message = message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ValidationErrorResponseDetailsInner validationErrorResponseDetailsInner = (ValidationErrorResponseDetailsInner) o;
    return Objects.equals(this.field, validationErrorResponseDetailsInner.field) &&
        Objects.equals(this.message, validationErrorResponseDetailsInner.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(field, message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ValidationErrorResponseDetailsInner {\n");
    sb.append("    field: ").append(toIndentedString(field)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
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

