package com.grantburgess.api.v1;

import com.grantburgess.creditprovider.CreditProvider;
import com.grantburgess.creditprovider.CreditProviderResponse;
import com.grantburgess.creditprovider.provider1.Provider1CreditProvider;
import com.grantburgess.creditprovider.provider2.Provider2CreditProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class CreditCardServiceV1Test {
    @Spy
    private List<CreditProvider> creditProviders = new ArrayList<>();

    @Mock
    private Provider1CreditProvider provider1CreditProvider;

    @Mock
    private Provider2CreditProvider provider2CreditProvider;

    @InjectMocks
    private CreditCardServiceV1 creditCardService;
    private CreditCardRequest creditCardRequest = CreditCardRequest
            .builder()
            .firstName("Robert C.")
            .lastName("Martin")
            .creditScore(341)
            .employmentStatus("PART_TIME")
            .dateOfBirth(LocalDate.of(1952, 10, 4))
            .salary(18500)
            .build();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(provider1CreditProvider.getCardRecommendations(ArgumentMatchers.any()))
                .thenReturn(
                        Arrays.asList(
                                CreditProviderResponse
                                        .builder()
                                        .provider("Provider1")
                                        .name("SuperSaver Card")
                                        .applyUrl("http://www.example.com/apply")
                                        .apr(BigDecimal.valueOf(21.4))
                                        .eligibility(BigDecimal.valueOf(6.3))
                                        .features(Collections.emptyList())
                                        .build(),
                                CreditProviderResponse
                                        .builder()
                                        .provider("Provider1")
                                        .name("SuperSpender Card")
                                        .applyUrl("http://www.example.com/apply")
                                        .apr(BigDecimal.valueOf(19.2))
                                        .eligibility(BigDecimal.valueOf(5.0))
                                        .features(Collections.singletonList("Interest free purchases for 6 months"))
                                        .build())
                );

        when(provider2CreditProvider.getCardRecommendations(ArgumentMatchers.any()))
                .thenReturn(
                        Collections.singletonList(
                                CreditProviderResponse
                                        .builder()
                                        .provider("Provider2")
                                        .name("Provider2 Builder")
                                        .applyUrl("http://www.example.com/apply")
                                        .apr(BigDecimal.valueOf(19.4))
                                        .eligibility(BigDecimal.valueOf(8.0))
                                        .features(Arrays.asList("Supports ApplePay", "Interest free purchases for 1 month"))
                                        .build()
                        )
                );

        creditProviders.add(provider1CreditProvider);
        creditProviders.add(provider2CreditProvider);
    }

    @Test
    public void testProcessCardRecommendationRequest() {
        // when
        List<CreditCardResponse> creditCardResponseList = creditCardService.processRequest(creditCardRequest);

        // then

        assertThat(
                creditCardResponseList.stream().map(CreditCardResponse::getCardScore).collect(Collectors.toList()),
                containsInAnyOrder(
                        BigDecimal.valueOf(0.212),
                        BigDecimal.valueOf(0.137),
                        BigDecimal.valueOf(0.135)
                )
        );
        assertThat(creditCardResponseList.get(0).getCardScore(), is(BigDecimal.valueOf(0.212)));
        assertThat(creditCardResponseList.get(0).getProvider(), is("Provider2"));
        assertThat(creditCardResponseList.get(0).getName(), is("Provider2 Builder"));
        assertThat(creditCardResponseList.get(0).getApplyUrl(), is("http://www.example.com/apply"));
        assertThat(creditCardResponseList.get(0).getApr(), is(BigDecimal.valueOf(19.4)));
        assertThat(creditCardResponseList.get(0).getFeatures(), containsInAnyOrder("Supports ApplePay", "Interest free purchases for 1 month"));

        assertThat(creditCardResponseList.get(1).getCardScore(), is(BigDecimal.valueOf(0.137)));
        assertThat(creditCardResponseList.get(1).getProvider(), is("Provider1"));
        assertThat(creditCardResponseList.get(1).getName(), is("SuperSaver Card"));
        assertThat(creditCardResponseList.get(1).getApplyUrl(), is("http://www.example.com/apply"));
        assertThat(creditCardResponseList.get(1).getApr(), is(BigDecimal.valueOf(21.4)));

        assertThat(creditCardResponseList.get(2).getCardScore(), is(BigDecimal.valueOf(0.135)));
        assertThat(creditCardResponseList.get(2).getProvider(), is("Provider1"));
        assertThat(creditCardResponseList.get(2).getName(), is("SuperSpender Card"));
        assertThat(creditCardResponseList.get(2).getApplyUrl(), is("http://www.example.com/apply"));
        assertThat(creditCardResponseList.get(2).getApr(), is(BigDecimal.valueOf(19.2)));
        assertThat(creditCardResponseList.get(2).getFeatures(), containsInAnyOrder("Interest free purchases for 6 months"));
    }
}