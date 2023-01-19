package com.mkw.hometax.tax.controller;

import com.mkw.hometax.Accounts.Account;
import com.mkw.hometax.Accounts.AccountRepository;
import com.mkw.hometax.Accounts.AccountService;
import com.mkw.hometax.Accounts.AccuontRole;
import com.mkw.hometax.common.AppProperties;
import com.mkw.hometax.common.Constant;
import com.mkw.hometax.configs.BaseControllerTest;
import com.mkw.hometax.tax.dto.HomeTaxBalanceDTO;
import com.mkw.hometax.tax.entity.HomeTaxBalanceEntity;
import com.mkw.hometax.tax.repository.HomeTaxBalanceRepository;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
class HomeTaxBalanceControllerTest extends BaseControllerTest {

    @Autowired
    AppProperties appProperties;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    HomeTaxBalanceRepository homeTaxBalanceRepository;

    @BeforeEach
    public void setUp(){
        homeTaxBalanceRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("홈텍스 월세 잔여 생성")
    //XXX 생성은 회원 가입시[추후 홈멤버가입시로 변경예정] 생성됨
    public void createHomeTaxBalance() throws Exception {
        //given
        HomeTaxBalanceDTO homeTaxBalanceDTO = HomeTaxBalanceDTO.builder()
                .balance("500000")
                .myId("gd")
                .build();

        //when & then
        mockMvc.perform(post("/api/hometaxbalance")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(Constant.MediaType.HalJsonUtf8.getCode())
                .content(objectMapper.writeValueAsString(homeTaxBalanceDTO))
        ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("myId").exists())
                .andExpect(jsonPath("balance").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, Constant.MediaType.HalJsonUtf8.getCode()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-hometaxbalances").exists())
                .andExpect(jsonPath("_links.update-hometaxbalance").exists())
                .andDo(document("create-hometaxbalance",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-hometaxbalances").description("link to query hometaxbalances"),
                                linkWithRel("update-hometaxbalance").description("link to update an existing hometaxbalance")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("myId").description("아이디"),
                                fieldWithPath("balance").description("잔액")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("myId").description("아이디"),
                                fieldWithPath("balance").description("잔액"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-hometaxbalances.href").description("link to query hometaxbalance list"),
                                fieldWithPath("_links.update-hometaxbalance.href").description("link to update existing hometaxbalance")
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
    @DisplayName("모든 홈텍스 회원의 월세 잔여액 조회")
    public void queryHomeTaxBalance() throws Exception {
        //given
        IntStream.range(1, 13).forEach(this::generateHomeTaxBalance);

        //when & then
        mockMvc.perform(get("/api/hometaxbalance")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "myId,DESC")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(Constant.MediaType.HalJsonUtf8.getCode())
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.homeTaxBalanceEntityList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-hometaxbalances",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("next").description("link to self"),
                                linkWithRel("last").description("link to self"),
                                linkWithRel("first").description("link to self"),
                                linkWithRel("profile").description("link to profile of hometaxbalance"),
                                linkWithRel("create-hometaxbalance").description("link to create hometaxbalance")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("_embedded.homeTaxBalanceEntityList[0].balance").description("월세 잔여액"),
                                fieldWithPath("_embedded.homeTaxBalanceEntityList[0].myId").description("아이디"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.create-hometaxbalance.href").description("link to create hometaxbalance")
                        )
                ))
        ;
    }

    private HomeTaxBalanceEntity generateHomeTaxBalance(int i) {
        String myId = "member" + i;

        HomeTaxBalanceDTO homeTaxBalanceDTO = HomeTaxBalanceDTO.builder()
                .myId(myId)
                .balance("300000")
                .build();

        return homeTaxBalanceRepository.save(modelMapper.map(homeTaxBalanceDTO, HomeTaxBalanceEntity.class));
    }

    private HomeTaxBalanceEntity generateHomeTaxBalance(String myId) {
        HomeTaxBalanceDTO homeTaxBalanceDTO = HomeTaxBalanceDTO.builder()
                .myId(myId)
                .balance("300000")
                .build();

        return homeTaxBalanceRepository.save(modelMapper.map(homeTaxBalanceDTO, HomeTaxBalanceEntity.class));
    }

    @Test
    @DisplayName("특정 유저의 월세 잔여내역을 조회한다[단수]")
    public void getHomeTaxBalanceByMyId() throws Exception {
        String myId = "gd";
        generateHomeTaxBalance(myId);

        //when & then
        mockMvc.perform(get("/api/hometaxbalance/{myId}", myId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(Constant.MediaType.HalJsonUtf8.getCode())
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-hometaxbalance",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile of hometaxbalance"),
                                linkWithRel("update-hometaxbalance").description("link to update hometaxbalance")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("myId").description("아이디"),
                                fieldWithPath("balance").description("잔액"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.update-hometaxbalance.href").description("link to create hometaxbalance")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("월세를 납부할 경우 해당 유저의 월세 잔여액 수정이 일어나야한다")
    public void updateHomeTaxInsert() throws Exception {
        //given
        String myId = "gd";
        String inputFee = "200000";
        HomeTaxBalanceEntity savedHomeTaxBalanceEntity = generateHomeTaxBalance(myId);
        HomeTaxBalanceDTO homeTaxBalanceDTO = modelMapper.map(savedHomeTaxBalanceEntity, HomeTaxBalanceDTO.class);

        String totalBalance = String.valueOf(Integer.parseInt(homeTaxBalanceDTO.getBalance()) - Integer.parseInt(inputFee));
        log.info("totalBalance ▶▶▶ " + totalBalance);
        homeTaxBalanceDTO.setBalance(totalBalance);

        //when & then
        mockMvc.perform(put("/api/hometaxbalance/{myId}", myId)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(Constant.MediaType.HalJsonUtf8.getCode())
                .content(objectMapper.writeValueAsString(homeTaxBalanceDTO))
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
                                fieldWithPath("myId").description("아이디"),
                                fieldWithPath("balance").description("잔여액")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("myId").description("아이디"),
                                fieldWithPath("balance").description("잔여액"),
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ))
        ;
    }

}