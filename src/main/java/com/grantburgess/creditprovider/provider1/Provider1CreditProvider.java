package com.grantburgess.creditprovider.provider1;

import com.grantburgess.creditprovider.CreditProvider;
import com.grantburgess.creditprovider.CreditProviderRequest;
import com.grantburgess.creditprovider.CreditProviderResponse;
import com.grantburgess.creditprovider.provider2.Provider2CreditProvider;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class Provider1CreditProvider implements CreditProvider {
    private final String endpoint;
    private final RestTemplate restTemplate;
    private final Logger log = Logger.getLogger(Provider2CreditProvider.class.getName());

    public Provider1CreditProvider(@Value("${creditprovider.provider1.endpoint}") final String endpoint, final RestTemplate restTemplate) {
        this.endpoint = endpoint;
        this.restTemplate = restTemplate;
    }

    @HystrixCommand(fallbackMethod = "getCardRecommendationsFallback", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000")
    })
    @Override
    public List<CreditProviderResponse> getCardRecommendations(final CreditProviderRequest creditProviderRequest) {
        String url = String.format("%s/v1/cards", endpoint);
        ResponseEntity<List<Provider2Response>> recommendations = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(
                        Provider1Request
                                .builder()
                                .fullName(
                                        String.format("%s %s",
                                                creditProviderRequest.getFirstName(),
                                                creditProviderRequest.getLastName())
                                )
                                .dateOfBirth(creditProviderRequest.getDateOfBirth())
                                .creditScore(creditProviderRequest.getCreditScore())
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
                                    .name(provider2Response.getCardName())
                                    .applyUrl(provider2Response.getUrl())
                                    .apr(provider2Response.getApr())
                                    .eligibility(provider2Response.getEligibility())
                                    .features(provider2Response.getFeatures())
                                    .build()
                    )
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    public List<CreditProviderResponse> getCardRecommendationsFallback(CreditProviderRequest creditProviderRequest, Throwable throwable) {
        logFallback(throwable, log);

        return Collections.emptyList();
    }

    @Override
    public String getProviderName() {
        return "Provider1";
    }
}