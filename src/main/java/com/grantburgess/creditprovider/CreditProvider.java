package com.grantburgess.creditprovider;

import com.netflix.hystrix.exception.HystrixTimeoutException;

import java.util.List;
import java.util.logging.Logger;

public interface CreditProvider {
    List<CreditProviderResponse> getCardRecommendations(CreditProviderRequest creditProviderRequest);
    List<CreditProviderResponse> getCardRecommendationsFallback(CreditProviderRequest creditProviderRequest, Throwable throwable);
    String getProviderName();

    default void logFallback(Throwable throwable, Logger log) {
        log.warning(() -> String.format("Fallback called! Our provider %s seems to be having some difficulty.", getProviderName()));

        if (throwable instanceof HystrixTimeoutException) {
            log.warning(() -> String.format("%s appears to have timed out.", getProviderName()));
        } else {
            log.warning(() -> String.format("The problem for %s appears to be: %s", getProviderName(), throwable.getMessage()));
        }
    }
}