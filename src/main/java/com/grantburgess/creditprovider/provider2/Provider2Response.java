package com.grantburgess.creditprovider.provider2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Provider2Response {
    private String card;
    @JsonProperty("apply-url")
    private String applyUrl;
    @JsonProperty("annual-percentage-rate")
    private BigDecimal annualPercentageRate;
    @JsonProperty("approval-rating")
    private BigDecimal approvalRating;
    private List<String> attributes;
    @JsonProperty("introductory-offers")
    private List<String> introductoryOffers;
}