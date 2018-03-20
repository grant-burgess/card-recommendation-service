package com.grantburgess.creditprovider.provider2;

import com.grantburgess.creditprovider.CreditProvider;
import com.grantburgess.creditprovider.CreditProviderRequest;
import com.grantburgess.creditprovider.CreditProviderResponse;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class Provider2CreditProvider implements CreditProvider {
    private final String endpoint;
    private final RestTemplate restTemplate;
    private final Logger log = Logger.getLogger(Provider2CreditProvider.class.getName());

    public Provider2CreditProvider(@Value("${creditprovider.provider2.endpoint}") final String endpoint, final RestTemplate restTemplate) {
        this.endpoint = endpoint;
        this.restTemplate = restTemplate;
    }

    @HystrixCommand(fallbackMethod = "getCardRecommendationsFallback", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
    })
    @Override
    public List<CreditProviderResponse> getCardRecommendations(final CreditProviderRequest creditProviderRequest) {
        String url = String.format("%s/v2/creditcards", endpoint);
        ResponseEntity<List<Provider2Response>> recommendations = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(
                        Provider2Request
                                .builder()
                                .firstName(creditProviderRequest.getFirstName())
                                .lastName(creditProviderRequest.getLastName())
                                .dateOfBirth(creditProviderRequest.getDateOfBirth())
                                .score(creditProviderRequest.getCreditScore())
                                .employmentStatus(creditProviderRequest.getEmploymentStatus())
                                .salary(creditProviderRequest.getSalary())
                                .build()
                ),
                new ParameterizedTypeReference<List<Provider2Response>>() {
                }
        );

        if (recommendations.getStatusCode().is2xxSuccessful() && recommendations.hasBody()) {
            return Objects.requireNonNull(recommendations.getBody()).stream()
                    .map(provider2Response ->
                            CreditProviderResponse
                                    .builder()
                                    .provider(getProviderName())
                                    .name(provider2Response.getCard())
                                    .applyUrl(provider2Response.getApplyUrl())
                                    .apr(provider2Response.getAnnualPercentageRate())
                                    .eligibility(provider2Response.getApprovalRating().multiply(BigDecimal.TEN))
                                    .features(
                                            Stream.of(provider2Response.getAttributes(), provider2Response.getIntroductoryOffers())
                                                    .flatMap(Collection::stream)
                                                    .collect(Collectors.toList())
                                    )
                                    .build()
                    )
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    public List<CreditProviderResponse> getCardRecommendationsFallback(CreditProviderRequest creditProviderRequest, Throwable throwable) {
        logFallback(throwable, log);

        // alternatives to returning an empty list could be
        // - use a cache
        // - retry depending on the error
        // - fail fast, though the steady state of the system will suffer
        // - show a friendly message to the API consumer

        return Collections.emptyList();
    }

    @Override
    public String getProviderName() {
        return "Provider2";
    }
}