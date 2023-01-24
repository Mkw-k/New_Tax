package com.mkw.hometax.tax.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    @DisplayName("개인 월세 내역 인서트 성공")
    //TODO 스트링이 인티저로 변환 되고 있음
    public void createHomeTaxSuccess() throws Exception, JsonProcessingException {
        //given
        HomeTaxDTO homeTaxDTO = HomeTaxDTO.builder()
                .day("2212")
                .elec("28000")
                .myId("123") //TODO 문제가 있음
                .gas("28000")
                .inter("28000")
                .managerFee("28000")
                .monthFee("300000")
                .water("28000")
                .build();

        //when & then
        mockMvc.perform(post("/api/hometax")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(Constant.MediaType.HalJsonUtf8.getCode())
                .content(objectMapper.writeValueAsString(homeTaxDTO))
        ).andDo(print())
                .andExpect(status().isCreated())
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
                                fieldWithPath("day").description("월세 연월을 뜻함"),
                                fieldWithPath("water").description("수도세"),
                                fieldWithPath("elec").description("전기세"),
                                fieldWithPath("gas").description("가스비"),
                                fieldWithPath("myId").description("아이디"),
                                fieldWithPath("inter").description("인터넷비"),
                                fieldWithPath("managerFee").description("관리비"),
                                fieldWithPath("monthFee").description("주인에게 직접내는 공과금을 제외한 월세"),
                                fieldWithPath("totalFee").description("월세 총액_나중에 계산되어 입력 처리 되므로 직접 입력할 필요가 없음")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("day").description("월세 연월을 뜻함"),
                                fieldWithPath("water").description("수도세"),
                                fieldWithPath("elec").description("전기세"),
                                fieldWithPath("gas").description("가스비"),
                                fieldWithPath("myId").description("아이디"),
                                fieldWithPath("inter").description("인터넷비"),
                                fieldWithPath("managerFee").description("관리비"),
                                fieldWithPath("monthFee").description("주인에게 직접내는 공과금을 제외한 월세"),
                                fieldWithPath("totalFee").description("월세총액"),
                                fieldWithPath("inptDttm").description("등록일시"),
                                fieldWithPath("updtDttm").description("수정일시"),
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
    @DisplayName("개인 월세 정보를 연월순으로 정렬하여 10개당 1페이지로 첫번째 페이지 불러오기")
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
                                fieldWithPath("_embedded.homeTaxEntityList[0].day").description("월세 연월을 뜻함"),
                                fieldWithPath("_embedded.homeTaxEntityList[0].elec").description("전기세"),
                                fieldWithPath("_embedded.homeTaxEntityList[0].water").description("수도세"),
                                fieldWithPath("_embedded.homeTaxEntityList[0].gas").description("가스비"),
                                fieldWithPath("_embedded.homeTaxEntityList[0].inter").description("인터넷비"),
                                fieldWithPath("_embedded.homeTaxEntityList[0].managerFee").description("관리비"),
                                fieldWithPath("_embedded.homeTaxEntityList[0].monthFee").description("주인에게 직접내는 공과금을 제외한 월세"),
                                fieldWithPath("_embedded.homeTaxEntityList[0].totalFee").description("월세총액"),
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
                .myId("123")    //TODO 이게 엔터티에서 숫자로 인식되는 이유를 찾아야함
                .gas("12000")
                .managerFee("12000")
                .monthFee("300000")
                .water("12000")
                .build();
        taxDTO.calculateTotalFee();

        homeTaxRepository.save(modelMapper.map(taxDTO, HomeTaxEntity.class));
    }

    @Test
    @DisplayName("월세 내역을 연월 파라미터로 하나만 조회하기")
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
                                fieldWithPath("day").description("월세 연월을 뜻함"),
                                fieldWithPath("water").description("수도세"),
                                fieldWithPath("elec").description("전기세"),
                                fieldWithPath("gas").description("가스비"),
                                fieldWithPath("inter").description("인터넷비"),
                                fieldWithPath("managerFee").description("관리비"),
                                fieldWithPath("monthFee").description("주인에게 직접내는 공과금을 제외한 월세"),
                                fieldWithPath("totalFee").description("월세총액"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.update-hometax.href").description("link to create hometax")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("월세 내역을 정상적으로 수정")
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
                                fieldWithPath("day").description("월세 연월을 뜻함"),
                                fieldWithPath("water").description("수도세"),
                                fieldWithPath("elec").description("전기세"),
                                fieldWithPath("myId").description("아이디"),
                                fieldWithPath("gas").description("가스비"),
                                fieldWithPath("inter").description("인터넷비"),
                                fieldWithPath("managerFee").description("관리비"),
                                fieldWithPath("monthFee").description("주인에게 직접내는 공과금을 제외한 월세"),
                                fieldWithPath("totalFee").description("월세 총액_나중에 계산되어 입력 처리 되므로 직접 입력할 필요가 없음")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("day").description("월세 연월을 뜻함"),
                                fieldWithPath("water").description("수도세"),
                                fieldWithPath("elec").description("전기세"),
                                fieldWithPath("gas").description("가스비"),
                                fieldWithPath("myId").description("아이디"),
                                fieldWithPath("inter").description("인터넷비"),
                                fieldWithPath("managerFee").description("관리비"),
                                fieldWithPath("monthFee").description("주인에게 직접내는 공과금을 제외한 월세"),
                                fieldWithPath("totalFee").description("월세총액"),
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ))
        ;
    }

    //TODO 이미 있는 연월의 데이터를 인서트 하려 하면 에러가 발생하야함
}