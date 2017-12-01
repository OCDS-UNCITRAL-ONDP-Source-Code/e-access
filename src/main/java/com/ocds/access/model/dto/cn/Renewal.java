
package com.ocds.access.model.dto.cn;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Getter
@JsonPropertyOrder({
    "hasRenewals"
})
public class Renewal {
    @JsonProperty("hasRenewals")
    @JsonPropertyDescription("A True/False field to indicate whether contract renewals are allowed.")
    @NotNull
    private final Boolean hasRenewals;

    @JsonCreator
    public Renewal(@JsonProperty("hasRenewals") final Boolean hasRenewals) {
        this.hasRenewals = hasRenewals;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(hasRenewals)
                                    .toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Renewal)) {
            return false;
        }
        final Renewal rhs = (Renewal) other;
        return new EqualsBuilder().append(hasRenewals, rhs.hasRenewals)
                                  .isEquals();
    }
}
