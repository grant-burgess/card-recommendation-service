package com.grantburgess.api.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditCardRequest {
    @NotNull
    @NotBlank
    @JsonProperty("firstname")
    private String firstName;

    @JsonProperty("lastname")
    @NotNull
    @NotBlank
    private String lastName;

    @NotNull
    @Past
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonProperty("dob")
    private LocalDate dateOfBirth;

    @JsonProperty("credit-score")
    @NotNull
    @Min(0)
    private Integer creditScore;

    @NotNull
    @NotBlank
    @JsonProperty("employment-status")
    private String employmentStatus;

    @NotNull
    @Min(1)
    private Integer salary;
}