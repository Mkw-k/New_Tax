package com.mkw.hometax.tax.controller;

import com.mkw.hometax.Accounts.Account;
import com.mkw.hometax.Accounts.AccountRepository;
import com.mkw.hometax.Accounts.AccountService;
import com.mkw.hometax.Accounts.AccuontRole;
import com.mkw.hometax.common.AppProperties;
import com.mkw.hometax.common.Constant;
import com.mkw.hometax.configs.BaseControllerTest;
import com.mkw.hometax.tax.dto.HomeTaxMasterDTO;
import com.mkw.hometax.tax.entity.HomeTaxMasterEntity;
import com.mkw.hometax.tax.repository.HomeTaxMasterRepository;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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

import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
class HomeTaxMasterControllerTest extends BaseControllerTest {

    @Autowired
    AppProperties appProperties;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    HomeTaxMasterRepository homeTaxMasterRepository;

    @BeforeEach
    public void setUp(){
        homeTaxMasterRepository.deleteAll();
        accountRepository.deleteAll();
    }

    //TODO C
    @Test
    @DisplayName("홈텍스 마스터 정상 인서트")
    public void createHomeTaxMaster() throws Exception {
        //given
        HomeTaxMasterDTO masterDTO = HomeTaxMasterDTO.builder()
                .day("2212")
                .elec("12000")
                .inter("12000")
                .gas("12000")
                .managerFee("12000")
                .monthFee("300000")
                .water("12000")
                .build();

        //when & then
        mockMvc.perform(post("/api/homtaxmaster")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(Constant.MediaType.HalJsonUtf8.getCode())
                .content(objectMapper.writeValueAsString(masterDTO))
        ).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("totalFee").exists())
                .andExpect(jsonPath("monthFee").value("300000"))
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, Constant.MediaType.HalJsonUtf8.getCode()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-tax").exists())
                .andExpect(jsonPath("_links.update-tax").exists())
                ;
    }

    @Test
    @DisplayName("필수값 부재시 인서트 실패")
    public void createHomeTaxMaster_EmptyValue() throws Exception {
        //given
        HomeTaxMasterDTO masterDTO = HomeTaxMasterDTO.builder()
                .day("2212")
                .elec("12000")
                .inter("12000")
                .gas("12000")
                .managerFee("12000")
                .water("12000")
                .build();

        //when & then
        mockMvc.perform(post("/api/homtaxmaster")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(Constant.MediaType.HalJsonUtf8.getCode())
                        .content(objectMapper.writeValueAsString(masterDTO))
                ).andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("입력값이 잘못되었을 경우 인서트 실패")
    public void createHomeTaxMaster_WrongValue() throws Exception {
        //given
        HomeTaxMasterDTO masterDTO = HomeTaxMasterDTO.builder()
                .day("2212")
                .elec("12000")
                .inter("12000")
                .gas("12000")
                .managerFee("12000")
                .monthFee("310000")
                .water("12000")
                .build();

        //when & then
        mockMvc.perform(post("/api/homtaxmaster")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(Constant.MediaType.HalJsonUtf8.getCode())
                        .content(objectMapper.writeValueAsString(masterDTO))
                ).andDo(print())
                .andExpect(status().isBadRequest())
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

    //TODO R
    @Test
    @DisplayName("월세 정보를 연월순으로 정렬하여 10개당 1페이지로 첫번째 페이지 불러오기")
    public void queryHomeTaxMaster() throws Exception {
        //given
        IntStream.range(1, 13).forEach(this::generateHomeTaxMaster);

        //when & then
        mockMvc.perform(get("/api/homtaxmaster")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "day,DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.homeTaxMasterEntityList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
//                .andExpect(jsonPath("_links.create-hometaxmaster").exists())
//                .andDo(document("query-members"))
        ;

    }

    private void generateHomeTaxMaster(int i) {
        String day = "22"+ (i < 10? "0"+i : i);

        HomeTaxMasterDTO masterDTO = HomeTaxMasterDTO.builder()
                .day(day)
                .elec("12000")
                .inter("12000")
                .gas("12000")
                .managerFee("12000")
                .monthFee("310000")
                .water("12000")
                .build();
        masterDTO.calculateTotalFee();

        homeTaxMasterRepository.save(modelMapper.map(masterDTO, HomeTaxMasterEntity.class));
    }

    //TODO U

}