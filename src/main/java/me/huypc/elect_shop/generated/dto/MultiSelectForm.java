package me.huypc.elect_shop.generated.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * MultiSelectForm
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class MultiSelectForm {

  @Valid
  private List<String> selectedItems = new ArrayList<>();

  public MultiSelectForm() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public MultiSelectForm(List<String> selectedItems) {
    this.selectedItems = selectedItems;
  }

  public MultiSelectForm selectedItems(List<String> selectedItems) {
    this.selectedItems = selectedItems;
    return this;
  }

  public MultiSelectForm addSelectedItemsItem(String selectedItemsItem) {
    if (this.selectedItems == null) {
      this.selectedItems = new ArrayList<>();
    }
    this.selectedItems.add(selectedItemsItem);
    return this;
  }

  /**
   * List of selected item IDs
   * @return selectedItems
   */
  @NotNull 
  @Schema(name = "selectedItems", example = "[12345, 67890]", description = "List of selected item IDs", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("selectedItems")
  public List<String> getSelectedItems() {
    return selectedItems;
  }

  public void setSelectedItems(List<String> selectedItems) {
    this.selectedItems = selectedItems;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MultiSelectForm multiSelectForm = (MultiSelectForm) o;
    return Objects.equals(this.selectedItems, multiSelectForm.selectedItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(selectedItems);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MultiSelectForm {\n");
    sb.append("    selectedItems: ").append(toIndentedString(selectedItems)).append("\n");
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

