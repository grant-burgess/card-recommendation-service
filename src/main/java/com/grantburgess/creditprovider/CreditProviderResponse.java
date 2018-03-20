package com.grantburgess.creditprovider;

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
public class CreditProviderResponse {
    private String provider;
    private String name;
    private String applyUrl;
    private BigDecimal apr;
    private BigDecimal eligibility;
    private List<String> features;
}