package com.grantburgess.creditprovider.provider2;

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
public class Provider2CreditProviderConsumerTest {
    @Autowired
    private Provider2CreditProvider provider;

    @Rule
    public PactProviderRuleMk2 provider2 = new PactProviderRuleMk2
            ("provider2", "localhost", 8200, this);

    @Pact(consumer="credit-card-recommendation-service")
    public RequestResponsePact createPact(PactDslWithProvider builder) throws IOException {
        return builder
                .given("Find card recommendations")
                .uponReceiving("retrieve card recommendations from Provider2")
                    .path("/v2/creditcards")
                    .method("POST")
                    .body(FileLoader.read("classpath:provider2-good-request.json"), ContentType.APPLICATION_JSON)
                .willRespondWith()
                    .status(200)
                    .body(FileLoader.read("classpath:provider2-good-response.json"), ContentType.APPLICATION_JSON)
                .toPact();
    }

    @Test
    @PactVerification("provider2")
    public void testVerifyContractWithProvider2() throws Exception {
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

        assertThat(cardRecommendations.get(0).getName(), is("Provider2 Builder"));
        assertThat(cardRecommendations.get(0).getApplyUrl(), is("http://www.example.com/apply"));
        assertThat(cardRecommendations.get(0).getApr(), is(BigDecimal.valueOf(19.4)));
        assertThat(cardRecommendations.get(0).getEligibility(), is(BigDecimal.valueOf(8.0)));
        assertThat(cardRecommendations.get(0).getFeatures(), containsInAnyOrder("Interest free purchases for 1 month", "Supports ApplePay"));
    }
}