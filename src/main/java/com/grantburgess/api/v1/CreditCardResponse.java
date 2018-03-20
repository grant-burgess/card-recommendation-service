package com.grantburgess.api.v1;

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
public class CreditCardResponse {
    private String provider;
    private String name;
    @JsonProperty("apply-url")
    private String applyUrl;
    private BigDecimal apr;
    private List<String> features;
    @JsonProperty("card-score")
    private BigDecimal cardScore;
}