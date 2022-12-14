package com.mkw.hometax.tax.controller;

import com.mkw.hometax.Accounts.Account;
import com.mkw.hometax.Accounts.AccountService;
import com.mkw.hometax.Accounts.AccuontRole;
import com.mkw.hometax.common.AppProperties;
import com.mkw.hometax.common.Constant;
import com.mkw.hometax.configs.BaseControllerTest;
import com.mkw.hometax.tax.dto.HomeTaxDTO;
import com.mkw.hometax.tax.entity.HomeTaxEntity;
import com.mkw.hometax.tax.repository.HomeTaxRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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

class HomeTaxControllerTest extends BaseControllerTest {

    @Autowired
    AppProperties appProperties;

    @Autowired
    AccountService accountService;

    @Autowired
    HomeTaxRepository homeTaxRepository;

    @Test
    @DisplayName("?????? ?????? ?????? ????????? ??????")
    public void createHomeTaxSuccess() throws Exception {
        //given
        HomeTaxDTO homeTaxDTO = HomeTaxDTO.builder()
                .day("2212")
                .elec("28000")
                .gas("28000")
                .inter("28000")
                .managerFee("28000")
                .monthFee("300000")
                .water("28000")
                .build();

        //when & then
        mockMvc.perform(post("/api/hometax")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(Constant.MediaType.HalJsonUtf8.getCode())
                .content(objectMapper.writeValueAsString(homeTaxDTO))
        ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("totalFee").exists())
                .andExpect(jsonPath("monthFee").value("300000"))
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, Constant.MediaType.HalJsonUtf8.getCode()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-hometaxs").exists())
                .andExpect(jsonPath("_links.update-hometax").exists())
                .andDo(document("create-hometax",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-hometaxs").description("link to query hometaxs"),
                                linkWithRel("update-hometax").description("link to update an existing hometax")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("day").description("?????? ????????? ??????"),
                                fieldWithPath("water").description("?????????"),
                                fieldWithPath("elec").description("?????????"),
                                fieldWithPath("gas").description("?????????"),
                                fieldWithPath("inter").description("????????????"),
                                fieldWithPath("managerFee").description("?????????"),
                                fieldWithPath("monthFee").description("???????????? ???????????? ???????????? ????????? ??????"),
                                fieldWithPath("totalFee").description("?????? ??????_????????? ???????????? ?????? ?????? ????????? ?????? ????????? ????????? ??????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("day").description("?????? ????????? ??????"),
                                fieldWithPath("water").description("?????????"),
                                fieldWithPath("elec").description("?????????"),
                                fieldWithPath("gas").description("?????????"),
                                fieldWithPath("inter").description("????????????"),
                                fieldWithPath("managerFee").description("?????????"),
                                fieldWithPath("monthFee").description("???????????? ???????????? ???????????? ????????? ??????"),
                                fieldWithPath("totalFee").description("????????????"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-hometaxs.href").description("link to query hometax list"),
                                fieldWithPath("_links.update-hometax.href").description("link to update existing hometax")
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
    @DisplayName("?????? ?????? ????????? ??????????????? ???????????? 10?????? 1???????????? ????????? ????????? ????????????")
    public void queryHomeTax() throws Exception {
        //given
        IntStream.range(1, 13).forEach(this::generateHomeTax);

        //when & then
        mockMvc.perform(get("/api/hometax")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "day,DESC")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(Constant.MediaType.HalJsonUtf8.getCode())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.homeTaxEntityList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-hometaxs",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("next").description("link to self"),
                                linkWithRel("last").description("link to self"),
                                linkWithRel("first").description("link to self"),
                                linkWithRel("profile").description("link to profile of hometaxmaster"),
                                linkWithRel("create-hometax").description("link to create hometaxmaster")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("_embedded.homeTaxEntityList[0].day").description("?????? ????????? ??????"),
                                fieldWithPath("_embedded.homeTaxEntityList[0].elec").description("?????????"),
                                fieldWithPath("_embedded.homeTaxEntityList[0].water").description("?????????"),
                                fieldWithPath("_embedded.homeTaxEntityList[0].gas").description("?????????"),
                                fieldWithPath("_embedded.homeTaxEntityList[0].inter").description("????????????"),
                                fieldWithPath("_embedded.homeTaxEntityList[0].managerFee").description("?????????"),
                                fieldWithPath("_embedded.homeTaxEntityList[0].monthFee").description("???????????? ???????????? ???????????? ????????? ??????"),
                                fieldWithPath("_embedded.homeTaxEntityList[0].totalFee").description("????????????"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.create-hometax.href").description("link to create hometaxmaster")
                        )
                ))
        ;
    }

    private void generateHomeTax(int i) {
        String day = "";

        if(String.valueOf(i).length() > 2){
            day = String.valueOf(i);
        }else{
            day = "22"+ (i < 10? "0"+i : i);
        }

        HomeTaxDTO taxDTO = HomeTaxDTO.builder()
                .day(day)
                .elec("12000")
                .inter("12000")
                .gas("12000")
                .managerFee("12000")
                .monthFee("300000")
                .water("12000")
                .build();
        taxDTO.calculateTotalFee();

        homeTaxRepository.save(modelMapper.map(taxDTO, HomeTaxEntity.class));
    }

    @Test
    @DisplayName("?????? ????????? ?????? ??????????????? ????????? ????????????")
    public void getAnHomeTaxByYearMonth() throws Exception {
        int yearMonth = 2111;
        generateHomeTax(yearMonth);
        //when & then
        mockMvc.perform(get("/api/hometax/{day}", yearMonth)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(Constant.MediaType.HalJsonUtf8.getCode())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("monthFee").value("300000"))
                .andDo(document("get-an-hometax",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile of hometax"),
                                linkWithRel("update-hometax").description("link to update hometax")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("day").description("?????? ????????? ??????"),
                                fieldWithPath("water").description("?????????"),
                                fieldWithPath("elec").description("?????????"),
                                fieldWithPath("gas").description("?????????"),
                                fieldWithPath("inter").description("????????????"),
                                fieldWithPath("managerFee").description("?????????"),
                                fieldWithPath("monthFee").description("???????????? ???????????? ???????????? ????????? ??????"),
                                fieldWithPath("totalFee").description("????????????"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.update-hometax.href").description("link to create hometax")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("?????? ????????? ??????????????? ??????")
    public void updateHomeTax() throws Exception {
        int yearMonth = 2011;
        generateHomeTax(yearMonth);

        //given
        HomeTaxDTO taxDTO = HomeTaxDTO.builder()
                .inter("99999")
                .managerFee("12000")
                .monthFee("300000")
                .water("12000")
                .build();

        //when & then
        mockMvc.perform(put("/api/hometax/{day}", yearMonth)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(Constant.MediaType.HalJsonUtf8.getCode())
                        .content(objectMapper.writeValueAsString(taxDTO))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("monthFee").value("300000"))
                .andDo(document("update-hometax",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile of hometax")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("day").description("?????? ????????? ??????"),
                                fieldWithPath("water").description("?????????"),
                                fieldWithPath("elec").description("?????????"),
                                fieldWithPath("gas").description("?????????"),
                                fieldWithPath("inter").description("????????????"),
                                fieldWithPath("managerFee").description("?????????"),
                                fieldWithPath("monthFee").description("???????????? ???????????? ???????????? ????????? ??????"),
                                fieldWithPath("totalFee").description("?????? ??????_????????? ???????????? ?????? ?????? ????????? ?????? ????????? ????????? ??????")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("day").description("?????? ????????? ??????"),
                                fieldWithPath("water").description("?????????"),
                                fieldWithPath("elec").description("?????????"),
                                fieldWithPath("gas").description("?????????"),
                                fieldWithPath("inter").description("????????????"),
                                fieldWithPath("managerFee").description("?????????"),
                                fieldWithPath("monthFee").description("???????????? ???????????? ???????????? ????????? ??????"),
                                fieldWithPath("totalFee").description("????????????"),
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ))
        ;
    }

    //TODO ?????? ?????? ????????? ???????????? ????????? ?????? ?????? ????????? ???????????????
}