package com.procurement.access.model.dto.pin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder("optionToCombine")
public class PinLotGroupDto {

    @JsonProperty("optionToCombine")
    @JsonPropertyDescription("The buyer reserves the right to combine the lots in this group when awarding a contract.")
    @NotNull
    private final Boolean optionToCombine;

    @JsonCreator
    public PinLotGroupDto(@JsonProperty("optionToCombine") final Boolean optionToCombine) {

        this.optionToCombine = optionToCombine;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(optionToCombine)
                .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof PinLotGroupDto)) {
            return false;
        }
        final PinLotGroupDto rhs = (PinLotGroupDto) other;
        return new EqualsBuilder().append(optionToCombine, rhs.optionToCombine)
                .isEquals();
    }
}
