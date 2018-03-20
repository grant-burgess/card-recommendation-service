package com.grantburgess.api.v1;

import com.grantburgess.creditprovider.CreditProvider;
import com.grantburgess.creditprovider.CreditProviderRequest;
import com.grantburgess.creditprovider.CreditProviderResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreditCardServiceV1 {
    private final List<CreditProvider> creditProviders;

    public CreditCardServiceV1(List<CreditProvider> creditProviders) {
        this.creditProviders = creditProviders;
    }

    public List<CreditCardResponse> processRequest(final CreditCardRequest creditCardRequest) {
        CreditProviderRequest creditProviderRequest = CreditProviderRequest
                .builder()
                .firstName(creditCardRequest.getFirstName())
                .lastName(creditCardRequest.getLastName())
                .dateOfBirth(creditCardRequest.getDateOfBirth().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
                .employmentStatus(creditCardRequest.getEmploymentStatus())
                .salary(creditCardRequest.getSalary())
                .creditScore(creditCardRequest.getCreditScore())
                .build();

        // loop through all providers
        // map the results and sort in descending order of card score
        return creditProviders.parallelStream()
                .map(provider -> provider.getCardRecommendations(creditProviderRequest))
                .flatMap(Collection::stream)
                .map(CreditCardServiceV1::mapToCreditCardResponse)
                .sorted(Comparator.comparing(CreditCardResponse::getCardScore).reversed())
                .collect(Collectors.toList());
    }

    private static CreditCardResponse mapToCreditCardResponse(CreditProviderResponse provider) {
        return CreditCardResponse
                .builder()
                .provider(provider.getProvider())
                .name(provider.getName())
                .applyUrl(provider.getApplyUrl())
                .apr(provider.getApr())
                .cardScore(
                        // formula: score = eligibility * ((1/apr)**2)
                        (
                                provider.getEligibility()
                                        .multiply(
                                                (
                                                    BigDecimal.ONE
                                                            .divide(provider.getApr(), MathContext.DECIMAL32)
                                                )
                                                .pow(2)
                                        )
                        )
                        .multiply(BigDecimal.TEN)
                        .setScale(3, RoundingMode.DOWN)
                )
                .features(provider.getFeatures())
                .build();
    }
}