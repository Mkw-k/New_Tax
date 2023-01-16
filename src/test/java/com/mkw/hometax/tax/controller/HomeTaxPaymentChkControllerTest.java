package com.mkw.hometax.tax.controller;

import com.mkw.hometax.Accounts.Account;
import com.mkw.hometax.Accounts.AccountRepository;
import com.mkw.hometax.Accounts.AccountService;
import com.mkw.hometax.Accounts.AccuontRole;
import com.mkw.hometax.common.AppProperties;
import com.mkw.hometax.common.Constant;
import com.mkw.hometax.configs.BaseControllerTest;
import com.mkw.hometax.tax.dto.HomeTaxPaymentChkDTO;
import com.mkw.hometax.tax.entity.HomeTaxPaymentChkEntity;
import com.mkw.hometax.tax.repository.HomeTaxPaymentChkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HomeTaxPaymentChkControllerTest extends BaseControllerTest {
    @Autowired
    AppProperties appProperties;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    HomeTaxPaymentChkRepository homeTaxPaymentChkRepository;

    @BeforeEach
    public void setUp(){
        homeTaxPaymentChkRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("홈텍스 납부내역 인서트")
    public void createHomeTaxPaymentChk() throws Exception {
        //given
        HomeTaxPaymentChkDTO homeTaxPaymentChkDTO = HomeTaxPaymentChkDTO.builder()
                .day("2212")
                .elec("N")
                .inter("N")
                .gas("N")
                .managerFee("N")
                .monthFee("N")
                .water("N")
                .build();

        //when & then
        mockMvc.perform(post("/api/homtaxpaychk")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(Constant.MediaType.HalJsonUtf8.getCode())
                .content(objectMapper.writeValueAsString(homeTaxPaymentChkDTO))
        ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("day").exists())
                .andExpect(jsonPath("monthFee").value("N"))
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, Constant.MediaType.HalJsonUtf8.getCode()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-hometaxpaymentchks").exists())
                .andExpect(jsonPath("_links.update-hometaxpaymentchk").exists())
                .andDo(document("create-hometaxpaymentchk",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-hometaxpaymentchks").description("link to query hometaxpaymentchks"),
                                linkWithRel("update-hometaxpaymentchk").description("link to update an existing hometaxpaymentchk")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("day").description("월세 연월을 뜻함"),
                                fieldWithPath("elec").description("전기세 납부여부"),
                                fieldWithPath("water").description("수도세 납부여부"),
                                fieldWithPath("inter").description("인터넷비 납부여부"),
                                fieldWithPath("gas").description("가스비 납부여부"),
                                fieldWithPath("managerFee").description("관리비 납부여부"),
                                fieldWithPath("monthFee").description("월세 납부여부"),
                                fieldWithPath("del").description("삭제여부")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("day").description("월세 연월을 뜻함"),
                                fieldWithPath("elec").description("전기세 납부여부"),
                                fieldWithPath("water").description("수도세 납부여부"),
                                fieldWithPath("inter").description("인터넷비 납부여부"),
                                fieldWithPath("gas").description("가스비 납부여부"),
                                fieldWithPath("managerFee").description("관리비 납부여부"),
                                fieldWithPath("monthFee").description("월세 납부여부"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-hometaxpaymentchks.href").description("link to query hometaxpaymentchk list"),
                                fieldWithPath("_links.update-hometaxpaymentchk.href").description("link to update existing hometaxpaymentchk")
                        )
                ))
        ;
    }

    @NotNull
    private String getBearerToken(boolean needToGenerateAccount) throws Exception {
        return "Bearer" + getAccessToken(needToGenerateAccount);
    }

    private String getAccessToken(boolean needToGenerateAccount) throws Exception {
        //Given
        if(needToGenerateAccount){
            Account mkw = Account.builder()
                    .email(appProperties.getUserUserName())
                    .password(appProperties.getUserPassword())
                    .roles(Set.of(AccuontRole.Admin, AccuontRole.User))
                    .build();
            this.accountService.saveAccount(mkw);
        }

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("username", appProperties.getUserUserName());
        params.add("password", appProperties.getUserPassword());

        String clientId = "myApp";
        String clientSecret = "pass";

        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientSecret))
                .params(params));

        var responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(responseBody).get("access_token").toString();
    }

    @Test
    @DisplayName("월세 납부내역을 연월순으로 정렬하여 10개당 1페이지로 첫번째 페이지 불러오기")
    public void queryHomeTaxPaymentChk() throws Exception {
        //given
        IntStream.range(1, 13).forEach(this::generateHomeTaxPaymentChk);

        //when & then
        mockMvc.perform(get("/api/homtaxpaychk")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "day,DESC")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(Constant.MediaType.HalJsonUtf8.getCode())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.homeTaxPaymentChkEntityList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-hometaxpaymentchks",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("next").description("link to self"),
                                linkWithRel("last").description("link to self"),
                                linkWithRel("first").description("link to self"),
                                linkWithRel("profile").description("link to profile of hometaxpaymentchk"),
                                linkWithRel("create-homeTaxPaymentChk").description("link to create hometaxpaymentchk")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("_embedded.homeTaxPaymentChkEntityList[0].day").description("월세 연월을 뜻함"),
                                fieldWithPath("_embedded.homeTaxPaymentChkEntityList[0].elec").description("전기세 납부여부"),
                                fieldWithPath("_embedded.homeTaxPaymentChkEntityList[0].water").description("수도세 납부여부"),
                                fieldWithPath("_embedded.homeTaxPaymentChkEntityList[0].inter").description("인터넷비 납부여부"),
                                fieldWithPath("_embedded.homeTaxPaymentChkEntityList[0].gas").description("가스비 납부여부"),
                                fieldWithPath("_embedded.homeTaxPaymentChkEntityList[0].managerFee").description("관리비 납부여부"),
                                fieldWithPath("_embedded.homeTaxPaymentChkEntityList[0].monthFee").description("공과금을 제외한 월세"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.create-homeTaxPaymentChk.href").description("link to create homeTaxPaymentChk")
                        )
                ))
        ;
    }

    private void generateHomeTaxPaymentChk(int i) {
        String day = "22"+ (i < 10? "0"+i : i);

        HomeTaxPaymentChkDTO homeTaxPaymentChkDTO = HomeTaxPaymentChkDTO.builder()
                .day(day)
                .elec("N")
                .inter("N")
                .gas("N")
                .managerFee("N")
                .monthFee("N")
                .water("N")
                .build();

        homeTaxPaymentChkRepository.save(modelMapper.map(homeTaxPaymentChkDTO, HomeTaxPaymentChkEntity.class));
    }

    @Test
    @DisplayName("월세 납부내역을 연월 파라미터로 하나만 조회하기")
    public void getAnHomeTaxPaymentChkByYearMonth() throws Exception {
        int month = 11;
        generateHomeTaxPaymentChk(month);
        //when & then
        mockMvc.perform(get("/api/homtaxpaychk/{day}", "22"+month)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(Constant.MediaType.HalJsonUtf8.getCode())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("monthFee").value("N"))
                .andDo(document("get-an-hometaxpaymentchk",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile of hometaxpaymentchk"),
                                linkWithRel("update-hometaxpaymentchk").description("link to update hometaxpaymentchk")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("day").description("월세 연월을 뜻함"),
                                fieldWithPath("elec").description("전기세 납부여부"),
                                fieldWithPath("water").description("수도세 납부여부"),
                                fieldWithPath("inter").description("인터넷비 납부여부"),
                                fieldWithPath("gas").description("가스비 납부여부"),
                                fieldWithPath("managerFee").description("관리비 납부여부"),
                                fieldWithPath("monthFee").description("월세 납부여부"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.update-hometaxpaymentchk.href").description("link to create hometaxpaymentchk")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("월세 납부내역을 정상적으로 수정")
    public void updateHomeTaxPaymentChk() throws Exception {
        int month = 11;
        generateHomeTaxPaymentChk(month);

        //given
        HomeTaxPaymentChkDTO homeTaxPaymentChkDTO = HomeTaxPaymentChkDTO.builder()
                .day("22"+month)
                .elec("N")
                .inter("N")
                .gas("N")
                .managerFee("N")
                .monthFee("N")
                .water("N")
                .build();

        //when & then
        mockMvc.perform(put("/api/homtaxpaychk/{day}", "22"+month)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(Constant.MediaType.HalJsonUtf8.getCode())
                        .content(objectMapper.writeValueAsString(homeTaxPaymentChkDTO))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("monthFee").value("N"))
                .andDo(document("update-hometaxpaymentchk",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile of hometaxpaymentchk")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("day").description("월세 연월을 뜻함"),
                                fieldWithPath("elec").description("전기세 납부여부"),
                                fieldWithPath("water").description("수도세 납부여부"),
                                fieldWithPath("inter").description("인터넷비 납부여부"),
                                fieldWithPath("gas").description("가스비 납부여부"),
                                fieldWithPath("managerFee").description("관리비 납부여부"),
                                fieldWithPath("monthFee").description("월세 납부여부"),
                                fieldWithPath("del").description("삭제여부")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("day").description("월세 연월을 뜻함"),
                                fieldWithPath("elec").description("전기세 납부여부"),
                                fieldWithPath("water").description("수도세 납부여부"),
                                fieldWithPath("inter").description("인터넷비 납부여부"),
                                fieldWithPath("gas").description("가스비 납부여부"),
                                fieldWithPath("managerFee").description("관리비 납부여부"),
                                fieldWithPath("monthFee").description("월세 납부여부"),
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ))
        ;
    }
}