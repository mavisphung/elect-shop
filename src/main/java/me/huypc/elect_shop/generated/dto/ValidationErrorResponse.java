package me.huypc.elect_shop.generated.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.huypc.elect_shop.generated.dto.ValidationErrorResponseDetailsInner;
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
 * Validation error response with field-specific details
 */

@Schema(name = "ValidationErrorResponse", description = "Validation error response with field-specific details")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ValidationErrorResponse {

  private String error;

  @Valid
  private List<@Valid ValidationErrorResponseDetailsInner> details = new ArrayList<>();

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable OffsetDateTime timestamp;

  public ValidationErrorResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ValidationErrorResponse(String error, List<@Valid ValidationErrorResponseDetailsInner> details) {
    this.error = error;
    this.details = details;
  }

  public ValidationErrorResponse error(String error) {
    this.error = error;
    return this;
  }

  /**
   * General error message
   * @return error
   */
  @NotNull 
  @Schema(name = "error", example = "Validation failed", description = "General error message", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("error")
  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public ValidationErrorResponse details(List<@Valid ValidationErrorResponseDetailsInner> details) {
    this.details = details;
    return this;
  }

  public ValidationErrorResponse addDetailsItem(ValidationErrorResponseDetailsInner detailsItem) {
    if (this.details == null) {
      this.details = new ArrayList<>();
    }
    this.details.add(detailsItem);
    return this;
  }

  /**
   * Detailed validation errors
   * @return details
   */
  @NotNull @Valid 
  @Schema(name = "details", description = "Detailed validation errors", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("details")
  public List<@Valid ValidationErrorResponseDetailsInner> getDetails() {
    return details;
  }

  public void setDetails(List<@Valid ValidationErrorResponseDetailsInner> details) {
    this.details = details;
  }

  public ValidationErrorResponse timestamp(@Nullable OffsetDateTime timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * Error timestamp
   * @return timestamp
   */
  @Valid 
  @Schema(name = "timestamp", example = "2024-01-15T10:30:00Z", description = "Error timestamp", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("timestamp")
  public @Nullable OffsetDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(@Nullable OffsetDateTime timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ValidationErrorResponse validationErrorResponse = (ValidationErrorResponse) o;
    return Objects.equals(this.error, validationErrorResponse.error) &&
        Objects.equals(this.details, validationErrorResponse.details) &&
        Objects.equals(this.timestamp, validationErrorResponse.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(error, details, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ValidationErrorResponse {\n");
    sb.append("    error: ").append(toIndentedString(error)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
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

