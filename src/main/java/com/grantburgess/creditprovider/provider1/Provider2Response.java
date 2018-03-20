package com.grantburgess.creditprovider.provider1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private String cardName;
    private String url;
    private BigDecimal apr;
    private BigDecimal eligibility;
    private List<String> features;
}