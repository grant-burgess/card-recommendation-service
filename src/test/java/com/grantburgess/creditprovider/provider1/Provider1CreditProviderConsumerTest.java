package com.grantburgess.creditprovider.provider1;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import com.grantburgess.creditprovider.CreditProviderRequest;
import com.grantburgess.creditprovider.CreditProviderResponse;
import com.grantburgess.helper.FileLoader;
import org.apache.http.entity.ContentType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Provider1CreditProviderConsumerTest {
    @Autowired
    private Provider1CreditProvider provider;

    @Rule
    public PactProviderRuleMk2 provider1 = new PactProviderRuleMk2
            ("provider1", "localhost", 8100, this);

    @Pact(consumer="credit-card-recommendation-service")
    public RequestResponsePact createPact(PactDslWithProvider builder) throws IOException {
        return builder
                .given("Find credit cards user is eligible for")
                .uponReceiving("retrieve card recommendations from Provider1")
                    .path("/v1/cards")
                    .method("POST")
                    .body(FileLoader.read("classpath:provider1-good-request.json"), ContentType.APPLICATION_JSON)
                .willRespondWith()
                    .status(200)
                    .body(FileLoader.read("classpath:provider1-good-response.json"), ContentType.APPLICATION_JSON)
                .toPact();
    }

    @Test
    @PactVerification("provider1")
    public void testVerifyContractWithProvider1() throws Exception {
        // given/when
        List<CreditProviderResponse> cardRecommendations = provider.getCardRecommendations(
                CreditProviderRequest
                        .builder()
                        .firstName("Robert C.")
                        .lastName("Martin")
                        .dateOfBirth("1952/10/04")
                        .creditScore(341)
                        .employmentStatus("PART_TIME")
                        .salary(18500)
                        .build()
        );

        // then
        assertThat(cardRecommendations.size(), is(2));
        assertThat(cardRecommendations.get(0).getName(), is("SuperSaver Card"));
        assertThat(cardRecommendations.get(0).getApplyUrl(), is("http://www.example.com/apply"));
        assertThat(cardRecommendations.get(0).getApr(), is(BigDecimal.valueOf(21.4)));
        assertThat(cardRecommendations.get(0).getEligibility(), is(BigDecimal.valueOf(6.3)));

        assertThat(cardRecommendations.get(1).getName(), is("SuperSpender Card"));
        assertThat(cardRecommendations.get(1).getApplyUrl(), is("http://www.example.com/apply"));
        assertThat(cardRecommendations.get(1).getApr(), is(BigDecimal.valueOf(19.2)));
        assertThat(cardRecommendations.get(1).getEligibility(), is(BigDecimal.valueOf(5.0)));
        assertThat(cardRecommendations.get(1).getFeatures(), containsInAnyOrder("Interest free purchases for 6 months"));
    }
}