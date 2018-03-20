package com.grantburgess.creditprovider.provider2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Provider2Request {
    // JSON properties named slightly different to
    // demonstrate differences in implementation between APIs
    @JsonProperty("first-name")
    private String firstName;
    @JsonProperty("last-name")
    private String lastName;
    @JsonProperty("date-of-birth")
    private String dateOfBirth;
    private int score;
    @JsonProperty("employment-status")
    private String employmentStatus;
    private int salary;
}