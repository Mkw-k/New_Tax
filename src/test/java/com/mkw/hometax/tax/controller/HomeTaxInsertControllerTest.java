package com.mkw.hometax.tax.controller;

import com.mkw.hometax.Accounts.Account;
import com.mkw.hometax.Accounts.AccountRepository;
import com.mkw.hometax.Accounts.AccountService;
import com.mkw.hometax.Accounts.AccuontRole;
import com.mkw.hometax.common.AppProperties;
import com.mkw.hometax.common.Constant;
import com.mkw.hometax.configs.BaseControllerTest;
import com.mkw.hometax.tax.dto.HomeTaxInsertDTO;
import com.mkw.hometax.tax.entity.HomeTaxInsertEntity;
import com.mkw.hometax.tax.repository.HomeTaxInsertReepository;
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
import java.time.LocalDateTime;
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

class HomeTaxInsertControllerTest extends BaseControllerTest {
    @Autowired
    AppProperties appProperties;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    HomeTaxInsertReepository homeTaxInsertReepository;

    @BeforeEach
    public void setUp(){
        homeTaxInsertReepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("홈텍스 개인 납부내역 인서트")
    public void createHomeTaxInsert() throws Exception {
        //given
        HomeTaxInsertDTO homeTaxInsertDTO = HomeTaxInsertDTO.builder()
                .day("1911")
                .insertDate(LocalDateTime.now())
                .confirmYn("N")
                .myId("gd")
                .inputFee("200000")
                .delYn("N")
                .build();

        //when & then
        mockMvc.perform(post("/api/hometaxinsert")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(Constant.MediaType.HalJsonUtf8.getCode())
                .content(objectMapper.writeValueAsString(homeTaxInsertDTO))
        ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("day").exists())
                .andExpect(jsonPath("confirmYn").value("N"))
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, Constant.MediaType.HalJsonUtf8.getCode()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-hometaxinserts").exists())
                .andExpect(jsonPath("_links.update-hometaxinsert").exists())
                .andDo(document("create-hometaxinsert",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-hometaxinserts").description("link to query hometaxinserts"),
                                linkWithRel("update-hometaxinsert").description("link to update an existing hometaxinsert")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("day").description("해당월(월세)"),
                                fieldWithPath("insertDate").description("납부일시"),
                                fieldWithPath("confirmYn").description("승인여부"),
                                fieldWithPath("myId").description("아이디"),
                                fieldWithPath("inputFee").description("납부금액"),
                                fieldWithPath("delYn").description("삭제여부")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("day").description("해당월(월세)"),
                                fieldWithPath("insertDate").description("납부일시"),
                                fieldWithPath("confirmYn").description("승인여부"),
                                fieldWithPath("myId").description("아이디"),
                                fieldWithPath("inputFee").description("납부금액"),
                                fieldWithPath("delYn").description("삭제여부"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-hometaxinserts.href").description("link to query hometaxinsert list"),
                                fieldWithPath("_links.update-hometaxinsert.href").description("link to update existing hometaxinsert")
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
    @DisplayName("월세 납부 개인내역을 연월순으로 정렬하여 10개당 1페이지로 첫번째 페이지 불러오기")
    public void queryHomeTaxInsert() throws Exception {
        //given
        IntStream.range(1, 13).forEach(this::generateHomeTaxInsert);

        //when & then
        mockMvc.perform(get("/api/hometaxinsert")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "day,DESC")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(Constant.MediaType.HalJsonUtf8.getCode())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.homeTaxInsertEntityList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-hometaxinserts",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("next").description("link to self"),
                                linkWithRel("last").description("link to self"),
                                linkWithRel("first").description("link to self"),
                                linkWithRel("profile").description("link to profile of hometaxinsert"),
                                linkWithRel("create-homeTaxInsert").description("link to create hometaxinsert")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("_embedded.homeTaxInsertEntityList[0].seq").description("개인월세납부내역시퀀스"),
                                fieldWithPath("_embedded.homeTaxInsertEntityList[0].day").description("해당월(월세)"),
                                fieldWithPath("_embedded.homeTaxInsertEntityList[0].insertDate").description("납부일시"),
                                fieldWithPath("_embedded.homeTaxInsertEntityList[0].confirmYn").description("승인여부"),
                                fieldWithPath("_embedded.homeTaxInsertEntityList[0].myId").description("아이디"),
                                fieldWithPath("_embedded.homeTaxInsertEntityList[0].inputFee").description("납부금액"),
                                fieldWithPath("_embedded.homeTaxInsertEntityList[0].delYn").description("삭제여부"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.create-homeTaxInsert.href").description("link to create homeTaxInsert")
                        )
                ))
        ;
    }

    private void generateHomeTaxInsert(int i) {
        String day = "22"+ (i < 10? "0"+i : i);

        HomeTaxInsertDTO homeTaxInsertDTO = HomeTaxInsertDTO.builder()
                .day(day)
                .insertDate(LocalDateTime.now())
                .confirmYn("N")
                .myId("gd")
                .inputFee("200000")
                .delYn("N")
                .build();

        homeTaxInsertReepository.save(modelMapper.map(homeTaxInsertDTO, HomeTaxInsertEntity.class));
    }
    private HomeTaxInsertEntity generateHomeTaxInsert(int i, Account account) {
        String day = "22"+ (i < 10? "0"+i : i);

        HomeTaxInsertDTO homeTaxInsertDTO = HomeTaxInsertDTO.builder()
                .day(day)
                .insertDate(LocalDateTime.now())
                .confirmYn("N")
                .myId(account.getMyId())
                .inputFee("200000")
                .delYn("N")
                .build();

        return homeTaxInsertReepository.save(modelMapper.map(homeTaxInsertDTO, HomeTaxInsertEntity.class));
    }


    @Test
    @DisplayName("월세 납부내역을 연월 파라미터로 하나만 조회하기")
    public void getHomeTaxInsertByMyId() throws Exception {
        String myId = "gd";
        int month = 11;
        generateHomeTaxInsert(month);

        //when & then
        mockMvc.perform(get("/api/hometaxinsert/{myId}", myId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(Constant.MediaType.HalJsonUtf8.getCode())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-hometaxinsert",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile of hometaxinsert"),
                                linkWithRel("update-hometaxinsert").description("link to update hometaxinsert")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("seq").description("개인월세납부내역시퀀스"),
                                fieldWithPath("day").description("해당월(월세)"),
                                fieldWithPath("insertDate").description("납부일시"),
                                fieldWithPath("confirmYn").description("승인여부"),
                                fieldWithPath("myId").description("아이디"),
                                fieldWithPath("inputFee").description("납부금액"),
                                fieldWithPath("delYn").description("삭제여부"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.update-hometaxinsert.href").description("link to create hometaxinsert")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("월세 납부내역을 정상적으로 수정")
    public void updateHomeTaxInsert() throws Exception {
        int month = 11;
        Account newAccount = this.createAccount();
        HomeTaxInsertEntity savedHomeTaxInsertEntity = generateHomeTaxInsert(month, newAccount);
        HomeTaxInsertDTO homeTaxInsertDTO = modelMapper.map(savedHomeTaxInsertEntity, HomeTaxInsertDTO.class);

        //given
        homeTaxInsertDTO.setInputFee("30000");
        homeTaxInsertDTO.setConfirmYn("Y");

        //when & then
        mockMvc.perform(put("/api/hometaxinsert/{myId}", newAccount.getMyId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(Constant.MediaType.HalJsonUtf8.getCode())
                        .content(objectMapper.writeValueAsString(homeTaxInsertDTO))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("update-hometaxinsert",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile of hometaxinsert")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("day").description("해당월(월세)"),
                                fieldWithPath("insertDate").description("납부일시"),
                                fieldWithPath("confirmYn").description("승인여부"),
                                fieldWithPath("myId").description("아이디"),
                                fieldWithPath("inputFee").description("납부금액"),
                                fieldWithPath("delYn").description("삭제여부")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("seq").description("개인월세납부내역시퀀스"),
                                fieldWithPath("day").description("해당월(월세)"),
                                fieldWithPath("insertDate").description("납부일시"),
                                fieldWithPath("myId").description("아이디"),
                                fieldWithPath("inputFee").description("납부금액"),
                                fieldWithPath("delYn").description("삭제여부"),
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ))
        ;
    }

    private Account createAccount() {
        Account mkw = Account.builder()
                .email(appProperties.getUserUserName())
                .password(appProperties.getUserPassword())
                .myId("gd")
                .roles(Set.of(AccuontRole.Admin, AccuontRole.User, AccuontRole.HomeMember))
                .auth("3") //3: 관리자, 1: 홈멤버, 9: 일반회원
                .build();
        return this.accountService.saveAccount(mkw);
    }
}