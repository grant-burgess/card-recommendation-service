package com.grantburgess.creditprovider.provider1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Provider1Request {
    private String fullName;
    private String dateOfBirth;
    private int creditScore;
}