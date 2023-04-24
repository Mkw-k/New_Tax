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

    @Test
    @DisplayName("홈텍스 마스터 정상 인서트")
    public void createHomeTaxMaster() throws Exception {
        //given
        Account newAccount = this.createAccount();
        HomeTaxMasterDTO masterDTO = HomeTaxMasterDTO.builder()
                .day("2212")
                .elec("12000")
                .inter("12000")
                .gas("12000")
                .managerFee("12000")
                .monthFee("300000")
                .water("12000")
                .manager(newAccount)
                .build();

        //when & then
        mockMvc.perform(post("/api/homtaxmaster")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
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
                .andExpect(jsonPath("_links.query-hometaxmasters").exists())
                .andExpect(jsonPath("_links.update-hometaxmaster").exists())
                .andDo(document("create-hometaxmasters",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-hometaxmasters").description("link to query hometaxmasters"),
                                linkWithRel("update-hometaxmaster").description("link to update an existing hometaxmaster")
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
                                fieldWithPath("inter").description("인터넷비"),
                                fieldWithPath("inptDttm").description("입력일시"),
                                fieldWithPath("updtDttm").description("수정일시"),
                                fieldWithPath("managerFee").description("관리비"),
                                fieldWithPath("monthFee").description("주인에게 직접내는 공과금을 제외한 월세"),
                                fieldWithPath("totalFee").description("월세 총액_나중에 계산되어 입력 처리 되므로 직접 입력할 필요가 없음"),
                                fieldWithPath("manager.id").description("작성자 시퀀스"),
                                fieldWithPath("manager.email").description("작성자 이메일"),
                                fieldWithPath("manager.password").description("작성자 패스워드"),
                                fieldWithPath("manager.name").description("작성자 이름"),
                                fieldWithPath("manager.phone").description("작성자 전화번호"),
                                fieldWithPath("manager.isSale").description("작성자 홈멤버 할인회원여부"),
                                fieldWithPath("manager.myId").description("작성자 아이디"),
                                fieldWithPath("manager.auth").description("작성자 권한여부"),
                                fieldWithPath("manager.fileName").description("작성자 파일네임"),
                                fieldWithPath("manager.newFileName").description("작성자 신규 파일네임"),
                                fieldWithPath("manager.del").description("작성자 삭제여부"),
                                fieldWithPath("manager.inptDttm").description("등록일시"),
                                fieldWithPath("manager.updtDttm").description("수정일시"),
                                fieldWithPath("manager.roles").description("회원 역할구분[운영자, 일반유저, 홈멤버]")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("홈텍스 마스터 시퀀스"),
                                fieldWithPath("inptDttm").description("홈텍스 마스터 등록일시"),
                                fieldWithPath("updtDttm").description("홈텍스 마스터 수정일시"),
                                fieldWithPath("day").description("월세 연월을 뜻함"),
                                fieldWithPath("water").description("수도세"),
                                fieldWithPath("elec").description("전기세"),
                                fieldWithPath("gas").description("가스비"),
                                fieldWithPath("inter").description("인터넷비"),
                                fieldWithPath("managerFee").description("관리비"),
                                fieldWithPath("monthFee").description("주인에게 직접내는 공과금을 제외한 월세"),
                                fieldWithPath("totalFee").description("월세총액"),
                                fieldWithPath("manager.id").description("작성자 시퀀스"),
                                fieldWithPath("manager.email").description("작성자 이메일"),
                                fieldWithPath("manager.password").description("작성자 패스워드"),
                                fieldWithPath("manager.name").description("작성자 이름"),
                                fieldWithPath("manager.phone").description("작성자 전화번호"),
                                fieldWithPath("manager.isSale").description("작성자 홈멤버 할인회원여부"),
                                fieldWithPath("manager.myId").description("작성자 아이디"),
                                fieldWithPath("manager.auth").description("작성자 권한여부"),
                                fieldWithPath("manager.fileName").description("작성자 파일네임"),
                                fieldWithPath("manager.newFileName").description("작성자 신규 파일네임"),
                                fieldWithPath("manager.del").description("작성자 삭제여부"),
                                fieldWithPath("manager.inptDttm").description("매니저 회원 등록일시"),
                                fieldWithPath("manager.updtDttm").description("매니저 회원 수정일시"),
                                fieldWithPath("manager.roles").description("회원 역할구분[운영자, 일반유저, 홈멤버]"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-hometaxmasters.href").description("link to query hometaxmaster list"),
                                fieldWithPath("_links.update-hometaxmaster.href").description("link to update existing hometaxmaster")
                        )
                ))
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
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
                .andExpect(jsonPath("$[0].field").exists())
                .andDo(document("errors",
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("day").description("월세 연월을 뜻함"),
                                fieldWithPath("water").description("수도세"),
                                fieldWithPath("elec").description("전기세"),
                                fieldWithPath("gas").description("가스비"),
                                fieldWithPath("inter").description("인터넷비"),
                                fieldWithPath("inptDttm").description("입력일시"),
                                fieldWithPath("updtDttm").description("수정일시"),
                                fieldWithPath("managerFee").description("관리비"),
                                fieldWithPath("monthFee").description("주인에게 직접내는 공과금을 제외한 월세"),
                                fieldWithPath("totalFee").description("월세 총액_나중에 계산되어 입력 처리 되므로 직접 입력할 필요가 없음"),
                                fieldWithPath("manager").description("관리자(작성자)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("[].field").description("에러컬럼명"),
                                fieldWithPath("[].objectName").description("에러객체명"),
                                fieldWithPath("[].code").description("에러코드"),
                                fieldWithPath("[].defaultMessage").description("에러메시지")
                        )
                ))
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
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
                .andExpect(jsonPath("$[0].rejectValue").exists())
        ;
    }

    @Test
    @DisplayName("인증정보가 없을경우 인서트 실패")
    public void createHomeTaxMaster_UnAuthentication() throws Exception {
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
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(Constant.MediaType.HalJsonUtf8.getCode())
                        .content(objectMapper.writeValueAsString(masterDTO))
                ).andDo(print())
                .andExpect(status().isUnauthorized())
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
    @DisplayName("월세 정보를 연월순으로 정렬하여 10개당 1페이지로 첫번째 페이지 불러오기")
    public void queryHomeTaxMaster() throws Exception {
        //given
        Account newAccount = this.createAccount();
        IntStream.range(1, 13).forEach(value -> {
            generateHomeTaxMaster(value, newAccount);
        });

        //when & then
        mockMvc.perform(get("/api/homtaxmaster")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "day,DESC")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(Constant.MediaType.HalJsonUtf8.getCode())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.homeTaxMasterEntityList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-hometaxmasters",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("next").description("link to self"),
                                linkWithRel("last").description("link to self"),
                                linkWithRel("first").description("link to self"),
                                linkWithRel("profile").description("link to profile of hometaxmaster")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].day").description("월세 연월을 뜻함"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].water").description("수도세"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].elec").description("전기세"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].gas").description("가스비"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].inter").description("인터넷비"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].managerFee").description("관리비"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].monthFee").description("주인에게 직접내는 공과금을 제외한 월세"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].totalFee").description("월세총액"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].id").description("홈텍스마스터 시퀀스"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].inptDttm").description("홈텍스마스터 입력일시"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].updtDttm").description("홈텍스마스터 수정일시"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].manager.id").description("작성자 시퀀스"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].manager.email").description("작성자 이메일"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].manager.password").description("작성자 패스워드"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].manager.name").description("작성자 이름"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].manager.phone").description("작성자 전화번호"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].manager.isSale").description("작성자 홈멤버 할인회원여부"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].manager.myId").description("작성자 아이디"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].manager.auth").description("작성자 권한여부"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].manager.fileName").description("작성자 파일네임"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].manager.newFileName").description("작성자 신규 파일네임"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].manager.del").description("작성자 삭제여부"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].manager.inptDttm").description("매니저 회원 등록일시"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].manager.updtDttm").description("매니저 회원 수정일시"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0].manager.roles").description("회원 역할구분[운영자, 일반유저, 홈멤버]"),
                                fieldWithPath("_embedded.homeTaxMasterEntityList[0]._links.self.href").description("link to self"),
                                fieldWithPath("_links.first.href").description("link to first"),
                                fieldWithPath("_links.next.href").description("link to next"),
                                fieldWithPath("_links.last.href").description("link to last"),
                                fieldWithPath("_links.profile.href").description("link to profile"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("page.size").description("size of page"),
                                fieldWithPath("page.totalElements").description("totalElements of page"),
                                fieldWithPath("page.totalPages").description("totalPages of page"),
                                fieldWithPath("page.number").description("number of page")
                        )
                ))
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
                .monthFee("300000")
                .water("12000")
                .build();
        masterDTO.calculateTotalFee();

        homeTaxMasterRepository.save(modelMapper.map(masterDTO, HomeTaxMasterEntity.class));
    }

    private HomeTaxMasterEntity generateHomeTaxMaster(int i, Account account) {
        String day = "22"+ (i < 10? "0"+i : i);

        HomeTaxMasterDTO masterDTO = HomeTaxMasterDTO.builder()
                .day(day)
                .elec("12000")
                .inter("12000")
                .gas("12000")
                .managerFee("12000")
                .monthFee("300000")
                .water("12000")
                .manager(account)
                .build();
        masterDTO.calculateTotalFee();

        HomeTaxMasterEntity savedTaxMasterEntity = homeTaxMasterRepository.save(modelMapper.map(masterDTO, HomeTaxMasterEntity.class));
        return savedTaxMasterEntity;
    }

    @Test
    @DisplayName("월세 내역을 연월 파라미터로 하나만 조회하기")
    public void getAnHomeTaxMasterByYearMonth() throws Exception {
        int month = 11;
        Account account = this.createAccount();
        generateHomeTaxMaster(month, account);
        //when & then
        mockMvc.perform(get("/api/homtaxmaster/{day}", "22"+month)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(Constant.MediaType.HalJsonUtf8.getCode())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").exists())
//                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("monthFee").value("300000"))
                .andDo(document("get-an-hometaxmaster",
                        links(
                                linkWithRel("self").description("link to self"),
//                                linkWithRel("profile").description("link to profile of hometaxmaster"),
                                linkWithRel("update-hometaxmaster").description("link to update hometaxmaster")
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
                                fieldWithPath("_links.update-hometaxmaster.href").description("link to create hometaxmaster")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("월세 내역을 정상적으로 수정")
    public void updateHomeTaxMaster() throws Exception {
        int month = 11;
        Account newAccount = this.createAccount();
        HomeTaxMasterEntity savedHomeTaxMasterEntity = generateHomeTaxMaster(month, newAccount);
        HomeTaxMasterDTO taxMasterDTO = modelMapper.map(savedHomeTaxMasterEntity, HomeTaxMasterDTO.class);

        //given
        taxMasterDTO.setInter("99999");
        taxMasterDTO.setManagerFee("12000");
        taxMasterDTO.setMonthFee("300000");
        taxMasterDTO.setWater("12000");

        //when & then
        mockMvc.perform(put("/api/homtaxmaster/{day}", "22"+month)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(Constant.MediaType.HalJsonUtf8.getCode())
                        .content(objectMapper.writeValueAsString(taxMasterDTO))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("monthFee").value("300000"))
                .andDo(document("update-hometaxmaster",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile of hometaxmaster")
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
                                fieldWithPath("inter").description("인터넷비"),
                                fieldWithPath("managerFee").description("관리비"),
                                fieldWithPath("inptDttm").description("입력일시"),
                                fieldWithPath("updtDttm").description("수정일시"),
                                fieldWithPath("monthFee").description("주인에게 직접내는 공과금을 제외한 월세"),
                                fieldWithPath("totalFee").description("월세 총액_나중에 계산되어 입력 처리 되므로 직접 입력할 필요가 없음"),
                                fieldWithPath("manager.id").description("작성자 시퀀스"),
                                fieldWithPath("manager.email").description("작성자 이메일"),
                                fieldWithPath("manager.password").description("작성자 패스워드"),
                                fieldWithPath("manager.name").description("작성자 이름"),
                                fieldWithPath("manager.phone").description("작성자 전화번호"),
                                fieldWithPath("manager.isSale").description("작성자 홈멤버 할인회원여부"),
                                fieldWithPath("manager.myId").description("작성자 아이디"),
                                fieldWithPath("manager.auth").description("작성자 권한여부"),
                                fieldWithPath("manager.fileName").description("작성자 파일네임"),
                                fieldWithPath("manager.newFileName").description("작성자 신규 파일네임"),
                                fieldWithPath("manager.del").description("작성자 삭제여부"),
                                fieldWithPath("manager.inptDttm").description("등록일시"),
                                fieldWithPath("manager.updtDttm").description("수정일시"),
                                fieldWithPath("manager.roles").description("회원 역할구분[운영자, 일반유저, 홈멤버]")
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
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ))
        ;
    }

    private Account createAccount() {
        Account keesun = Account.builder()
                .email(appProperties.getUserUserName())
                .password(appProperties.getUserPassword())
                .roles(Set.of(AccuontRole.Admin, AccuontRole.User, AccuontRole.HomeMember))
                .auth("3") //3: 관리자, 1: 홈멤버, 9: 일반회원
                .build();
        return this.accountService.saveAccount(keesun);
    }
}