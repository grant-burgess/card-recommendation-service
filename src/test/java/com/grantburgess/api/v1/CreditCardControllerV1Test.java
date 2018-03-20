package com.grantburgess.api.v1;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

@WebMvcTest
@RunWith(SpringRunner.class)
public class CreditCardControllerV1Test {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreditCardServiceV1 creditCardServiceV1;
    private String validRequest ="" +
            "{\n" +
            "\t\"firstname\": \"Robert C.\",\n" +
            "\t\"lastname\": \"Martin\",\n" +
            "\t\"dob\": \"1952-10-04\",\n" +
            "\t\"credit-score\": 500,\n" +
            "\t\"employment-status\": \"Full time\",\n" +
            "\t\"salary\": 12000\n" +
            "}" +
            "";
    private String requestWithMissingProperty ="" +
            "{\n" +
            "\t\"firstname\": \"Robert C.\",\n" +
            "\t\"dob\": \"1952-10-04\",\n" +
            "\t\"credit-score\": 500,\n" +
            "\t\"employment-status\": \"Full time\",\n" +
            "\t\"salary\": 12000\n" +
            "}" +
            "";
    private String requestWithMalformedDate ="" +
            "{\n" +
            "\t\"firstname\": \"Robert C.\",\n" +
            "\t\"lastname\": \"Martin\",\n" +
            "\t\"dob\": \"1952-13-04\",\n" +
            "\t\"credit-score\": 500,\n" +
            "\t\"employment-status\": \"Full time\",\n" +
            "\t\"salary\": 12000\n" +
            "}" +
            "";

    @Test
    public void testGetRecommendedCards() throws Exception {
        // given
        Mockito.when(
                creditCardServiceV1.processRequest(
                        CreditCardRequest
                                .builder()
                                .firstName("Robert C.")
                                .lastName("Martin")
                                .dateOfBirth(LocalDate.of(1952, 10, 4))
                                .creditScore(500)
                                .employmentStatus("Full time")
                                .salary(12000)
                                .build()
                )
        )
                .thenReturn(
                        Arrays.asList(
                                CreditCardResponse
                                        .builder()
                                        .provider("Provider2")
                                        .name("Provider2 Builder")
                                        .applyUrl("http://www.example.com/apply")
                                        .apr(BigDecimal.valueOf(19.4))
                                        .features(Arrays.asList("Supports ApplePay", "Interest free purchases for 1 month"))
                                        .cardScore(BigDecimal.valueOf(0.212))
                                        .build(),
                                CreditCardResponse
                                        .builder()
                                        .provider("Provider1")
                                        .name("SuperSaver Card")
                                        .applyUrl("http://www.example.com/apply")
                                        .apr(BigDecimal.valueOf(21.4))
                                        .features(Collections.emptyList())
                                        .cardScore(BigDecimal.valueOf(0.137))
                                        .build(),
                                CreditCardResponse
                                        .builder()
                                        .provider("Provider1")
                                        .name("SuperSpender Card")
                                        .applyUrl("http://www.example.com/apply")
                                        .apr(BigDecimal.valueOf(19.2))
                                        .features(Collections.singletonList("Interest free purchases for 6 months"))
                                        .cardScore(BigDecimal.valueOf(0.135))
                                        .build()
                        )
                );

        // when/then
        this.mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .post("/v1/creditcards")
                                .content(validRequest)
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))

                .andExpect(MockMvcResultMatchers.jsonPath("@.[0].provider").value("Provider2"))
                .andExpect(MockMvcResultMatchers.jsonPath("@.[0].name").value("Provider2 Builder"))
                .andExpect(MockMvcResultMatchers.jsonPath("@.[0].apply-url").value("http://www.example.com/apply"))
                .andExpect(MockMvcResultMatchers.jsonPath("@.[0].card-score").value(0.212))
                .andExpect(MockMvcResultMatchers.jsonPath("@.[0].apr").value(19.4))
                .andExpect(MockMvcResultMatchers.jsonPath("@.[0].features").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("@.[0].features.[0]").value("Supports ApplePay"))
                .andExpect(MockMvcResultMatchers.jsonPath("@.[0].features.[1]").value("Interest free purchases for 1 month"))

                .andExpect(MockMvcResultMatchers.jsonPath("@.[1].provider").value("Provider1"))
                .andExpect(MockMvcResultMatchers.jsonPath("@.[1].name").value("SuperSaver Card"))
                .andExpect(MockMvcResultMatchers.jsonPath("@.[1].apply-url").value("http://www.example.com/apply"))
                .andExpect(MockMvcResultMatchers.jsonPath("@.[1].card-score").value(0.137))
                .andExpect(MockMvcResultMatchers.jsonPath("@.[1].apr").value(21.4))

                .andExpect(MockMvcResultMatchers.jsonPath("@.[2].provider").value("Provider1"))
                .andExpect(MockMvcResultMatchers.jsonPath("@.[2].name").value("SuperSpender Card"))
                .andExpect(MockMvcResultMatchers.jsonPath("@.[2].apply-url").value("http://www.example.com/apply"))
                .andExpect(MockMvcResultMatchers.jsonPath("@.[2].card-score").value(0.135))
                .andExpect(MockMvcResultMatchers.jsonPath("@.[2].apr").value(19.2))
                .andExpect(MockMvcResultMatchers.jsonPath("@.[2].features").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("@.[2].features.[0]").value("Interest free purchases for 6 months"));
    }

    @Test
    public void testAttemptToGetRecommendedCardsWithAMissingParameter() throws Exception {
        // when/then
        this.mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .post("/v1/creditcards")
                                .content(requestWithMissingProperty)
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("@.error.message").value("'lastName' must not be null"))
                .andExpect(MockMvcResultMatchers.jsonPath("@.error.type").value("MethodArgumentNotValid"))
                .andExpect(MockMvcResultMatchers.jsonPath("@.error.code").value(15000));
    }

    @Test
    public void testAttemptToGetRecommendedCardsWithAMalformedDate() throws Exception {
        // when/then
        this.mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .post("/v1/creditcards")
                                .content(requestWithMalformedDate)
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("@.error.message").value("Invalid date format. Date should be in the format yyyy-MM-dd"))
                .andExpect(MockMvcResultMatchers.jsonPath("@.error.type").value("MethodArgumentNotValid"))
                .andExpect(MockMvcResultMatchers.jsonPath("@.error.code").value(15000));
    }
}