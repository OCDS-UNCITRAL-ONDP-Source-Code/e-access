package com.procurement.access.model.dto.bpe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.access.model.dto.databinding.LocalDateTimeDeserializer;
import com.procurement.access.model.dto.databinding.LocalDateTimeSerializer;
import com.procurement.access.model.dto.ein.*;
import com.procurement.access.model.dto.enums.InitiationType;
import com.procurement.access.model.dto.enums.Tag;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({
        "token",
        "ocid",
        "id",
        "date",
        "tag",
        "initiationType",
        "language",
        "planning",
        "tender",
        "parties",
        "buyer",
        "relatedProcesses"
})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class EinResponseDto {
    @JsonProperty("token")
    private final String token;
    @JsonProperty("planning")
    private final EinPlanningDto planning;
    @JsonProperty("tender")
    private final EinTenderDto tender;
    @JsonProperty("parties")
    private final List<EinOrganizationDto> parties;
    @JsonProperty("buyer")
    private final EinOrganizationReferenceDto buyer;
    @JsonProperty("ocid")
    private String ocId;
    @JsonProperty("id")
    private String id;
    @JsonProperty("date")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime date;
    @JsonProperty("tag")
    private List<Tag> tag;
    @JsonProperty("initiationType")
    private InitiationType initiationType;
    @JsonProperty("language")
    private String language;
    @JsonProperty("relatedProcesses")
    private List<EinRelatedProcessDto> relatedProcesses;

    @JsonCreator
    public EinResponseDto(@JsonProperty("token") final String token,
                          @JsonProperty("ocid") final String ocId,
                          @JsonProperty("id") final String id,
                          @JsonProperty("date") final LocalDateTime date,
                          @JsonProperty("tag") final List<Tag> tag,
                          @JsonProperty("initiationType") final InitiationType initiationType,
                          @JsonProperty("language") final String language,
                          @JsonProperty("planning") final EinPlanningDto planning,
                          @JsonProperty("tender") final EinTenderDto tender,
                          @JsonProperty("parties") final List<EinOrganizationDto> parties,
                          @JsonProperty("buyer") final EinOrganizationReferenceDto buyer,
                          @JsonProperty("relatedProcesses") final List<EinRelatedProcessDto> relatedProcesses) {
        this.token = token;
        this.ocId = ocId;
        this.id = id;
        this.date = date;
        this.tag = tag;
        this.initiationType = initiationType;
        this.language = language;
        this.planning = planning;
        this.tender = tender;
        this.parties = parties;
        this.buyer = buyer;
        this.relatedProcesses = relatedProcesses;
    }
}
