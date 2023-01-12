package com.mkw.hometax.member.service;

import com.mkw.hometax.Accounts.Account;
import com.mkw.hometax.Accounts.AccountRepository;
import com.mkw.hometax.Accounts.AccountService;
import com.mkw.hometax.Accounts.AccuontRole;
import com.mkw.hometax.common.AppProperties;
import com.mkw.hometax.common.Constant;
import com.mkw.hometax.common.TestDecription;
import com.mkw.hometax.configs.BaseControllerTest;
import com.mkw.hometax.member.MemberRepository;
import com.mkw.hometax.member.dto.MemberDTO;
import com.mkw.hometax.member.entity.MemberEntity;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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

@Slf4j
public class MemberControllerTest extends BaseControllerTest {
    /*@MockBean
    MemberRepository memberRepository;*/

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AppProperties appProperties;

    @BeforeEach
    public void setUp(){
        this.memberRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

    @Test
    @DisplayName("입력할수 없는 값을 사용한 경우에 에러 발생")
    public void createEvent_Bad_Request() throws Exception {
        String url = "/api/member";
        MemberEntity member = MemberEntity.builder()
                .name("홍길동")
                .myId("gd123")
                .auth("0")
                .isSale("0")
                .pwd("123")
                .del("0")
                .build();

        mockMvc.perform(post(url)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(member))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("인서트 정상 성공")
    public void createEvent() throws Exception {
        String url = "/api/member";
        MemberDTO member = MemberDTO.builder()
                .name("홍길동")
                .pwd("123")
                .isSale("0")
                .myId("gd123")
                .classify("mko")
                .email("reqwe@mko")
                .phone("1234")
                .fileName("123412")
                .newFileName("1231413214")
                .auth("9")
                .updtDttm(LocalDateTime.now())  //해당값이 없으면 memberValidator에서 체크시 에러발생
                .inptDttm(LocalDateTime.now())  //해당값이 없으면 memberValidator에서 체크시 에러발생
                .build();

        mockMvc.perform(post(url)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(Constant.MediaType.HalJsonUtf8.getCode())
                        .content(objectMapper.writeValueAsString(member))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("myId").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, Constant.MediaType.HalJsonUtf8.getCode()))
                .andExpect(jsonPath("auth").value(Matchers.not("3")))
                .andExpect(jsonPath("isSale").value(Matchers.not("1")))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-events").exists())
                .andDo(MockMvcRestDocumentation.document("create-member",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-events").description("link to update an existing event")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new Member"),
                                fieldWithPath("classify").description("classify of new Member"),
                                fieldWithPath("email").description("email of close of new Member"),
                                fieldWithPath("phone").description("phone of close of new Member"),
                                fieldWithPath("pwd").description("pwd of close of new Member"),
                                fieldWithPath("isSale").description("isSale of close of new Member"),
                                fieldWithPath("myId").description("myId of close of new Member"),
                                fieldWithPath("auth").description("auth of close of new Member"),
                                fieldWithPath("fileName").description("fileName of close of new Member"),
                                fieldWithPath("newFileName").description("newFileName of close of new Member"),
                                fieldWithPath("inptDttm").description("inptDttm of close of new Member"),
                                fieldWithPath("updtDttm").description("updtDttm of close of new Member")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("name").description("Name of new Member"),
                                fieldWithPath("classify").description("classify of new Member"),
                                fieldWithPath("email").description("email of close of new Member"),
                                fieldWithPath("phone").description("phone of close of new Member"),
                                fieldWithPath("pwd").description("pwd of close of new Member"),
                                fieldWithPath("isSale").description("isSale of close of new Member"),
                                fieldWithPath("myId").description("myId of close of new Member"),
                                fieldWithPath("auth").description("auth of close of new Member"),
                                fieldWithPath("fileName").description("fileName of close of new Member"),
                                fieldWithPath("newFileName").description("newFileName of close of new Member"),
                                fieldWithPath("del").description("del of close of new Member"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-events.href").description("link to update existing event")
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
    @DisplayName("입력값이 잘못된 경우 에러가 발생하는 케이스")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        MemberDTO member = MemberDTO.builder()
                .name("홍길동")
                .pwd("123")
                .isSale("0")
                .myId("gd123")
                .classify("mko")
                .email("reqwe@mko")
                .phone("1234")
                .fileName("123412")
                .newFileName("1231413214")
                .auth("3")
                .inptDttm(LocalDateTime.of(2023, 1, 1, 0, 0, 0))
                .updtDttm(LocalDateTime.now())
                .build();

        this.mockMvc.perform(post("/api/member")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(member)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("$[0].rejectValue").exists())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
                .andDo(MockMvcRestDocumentation.document("errors",
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new Member"),
                                fieldWithPath("classify").description("classify of new Member"),
                                fieldWithPath("email").description("email of close of new Member"),
                                fieldWithPath("phone").description("phone of close of new Member"),
                                fieldWithPath("pwd").description("pwd of close of new Member"),
                                fieldWithPath("isSale").description("isSale of close of new Member"),
                                fieldWithPath("myId").description("myId of close of new Member"),
                                fieldWithPath("auth").description("auth of close of new Member"),
                                fieldWithPath("fileName").description("fileName of close of new Member"),
                                fieldWithPath("newFileName").description("newFileName of close of new Member"),
                                fieldWithPath("inptDttm").description("inptDttm of close of new Member"),
                                fieldWithPath("updtDttm").description("updtDttm of close of new Member")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("[0].field").description("어떤 에러인지 알려주는 컬럼").optional(),
                                fieldWithPath("[0].objectName").description("어떤 객체인지").optional(),
                                fieldWithPath("[0].code").description("어떤 에러인지 확인").optional(),
                                fieldWithPath("[0].defaultMessage").description("에러메시지").optional(),
                                fieldWithPath("[0].rejectValue").description("잘못된 값").optional()
                        )
                ))
        ;
    }

    @Test
    @DisplayName("필수 입력값이 누락된경우 에러발생")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        MemberDTO memberDTO = MemberDTO.builder().build();

        this.mockMvc.perform(post("/api/member")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(memberDTO)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("testIsSaleParams")
    @DisplayName("할인여부테스트")
    public void testIsSale(String isSale){
        //given
        MemberEntity memberDTO = MemberEntity.builder()
                .isSale(isSale)
                .build();

        //when
        memberDTO.update();

        log.debug("확인 >>> " + memberDTO.isSaleBool());

        //then
        Assertions.assertThat(memberDTO.isSaleBool());
    }
    //static 있어야 동작함
    private static Object[] testIsSaleParams(){
        return new Object[]{
                new Object[] {"1"},
                new Object[] {"0"}
        };
    }
    
    @Test
    @TestDecription("30개의 멤버를 10개씩 두번째 페이지 조회하기")
    public void queryMembers() throws Exception{
        //Given
        IntStream.range(0, 30).forEach(this::generateMember);

        //when
        this.mockMvc.perform(get("/api/member")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "myId,DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.memberEntityList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
//                .andExpect(jsonPath("_links.create-event").exists())
                .andDo(document("query-members"))
                ;

    }

    @Test
    @TestDecription("30개의 멤버를 10개씩 두번째 페이지 조회하기")
    public void queryMembersWithAuthentication() throws Exception{
        //Given
        IntStream.range(0, 30).forEach(this::generateMember);

        //when
        this.mockMvc.perform(get("/api/member")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "myId,DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.memberEntityList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.create-members").exists())
                .andDo(document("get-members",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("prev").description("link to prev"),
                                linkWithRel("next").description("link to next"),
                                linkWithRel("last").description("link to last"),
                                linkWithRel("first").description("link to first"),
                                linkWithRel("profile").description("link to profile of home members"),
                                linkWithRel("create-members").description("link to create home members")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("_embedded.memberEntityList[0].name").description("Name of new Member"),
                                fieldWithPath("_embedded.memberEntityList[0].classify").description("classify of new Member"),
                                fieldWithPath("_embedded.memberEntityList[0].email").description("email of close of new Member"),
                                fieldWithPath("_embedded.memberEntityList[0].phone").description("phone of close of new Member"),
                                fieldWithPath("_embedded.memberEntityList[0].pwd").description("pwd of close of new Member"),
                                fieldWithPath("_embedded.memberEntityList[0].isSale").description("isSale of close of new Member"),
                                fieldWithPath("_embedded.memberEntityList[0].myId").description("myId of close of new Member"),
                                fieldWithPath("_embedded.memberEntityList[0].auth").description("auth of close of new Member"),
                                fieldWithPath("_embedded.memberEntityList[0].fileName").description("fileName of close of new Member"),
                                fieldWithPath("_embedded.memberEntityList[0].newFileName").description("newFileName of close of new Member"),
                                fieldWithPath("_embedded.memberEntityList[0].del").description("del of close of new Member"),
                                fieldWithPath("_embedded.memberEntityList[0].inptDttm").description("data input datetime"),
                                fieldWithPath("_embedded.memberEntityList[0].updtDttm").description("data update datetime"),
                                fieldWithPath("_embedded.memberEntityList[0].manager").description("manager infomation of new Member"),
                                fieldWithPath("_embedded.memberEntityList[0].saleBool").description("Recognize discount membership"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.create-members.href").description("link to create home members")
                        )
                ))
        ;

    }


    private MemberEntity generateMember(int index) {
        MemberEntity memberEntity = MemberEntity.builder()
                .myId("member " + index)
                .name("홍길동")
                .build();

        return this.memberRepository.save(memberEntity);
    }

    private MemberEntity generateMember(int index, Account account) {
        MemberEntity memberEntity = MemberEntity.builder()
                .myId("member " + index)
                .name("홍길동")
                .pwd("123")
                .isSale("0")
                .myId("gd123")
                .classify("mko")
                .email("reqwe@mko")
                .phone("1234")
                .fileName("123412")
                .newFileName("1231413214")
                .auth("9")
                .updtDttm(LocalDateTime.now())  //해당값이 없으면 memberValidator에서 체크시 에러발생
                .inptDttm(LocalDateTime.now())  //해당값이 없으면 memberValidator에서 체크시 에러발생
                .manager(account)
                .build();

        return this.memberRepository.save(memberEntity);
    }

    @Test
    @DisplayName("기존의 멤버 하나를 조회하기")
    public void getMember() throws Exception{
        //Given
        Account account = this.createAccount();
        MemberEntity member = this.generateMember(100, account);

        //When & Then
        this.mockMvc.perform(get("/api/member/{id}", member.getMyId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andDo(document("get-member",
                        links(
                                linkWithRel("self").description("link to self"),
//                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-events").description("link to update an existing event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("name").description("Name of new Member"),
                                fieldWithPath("classify").description("classify of new Member"),
                                fieldWithPath("email").description("email of close of new Member"),
                                fieldWithPath("phone").description("phone of close of new Member"),
                                fieldWithPath("pwd").description("pwd of close of new Member"),
                                fieldWithPath("isSale").description("isSale of close of new Member"),
                                fieldWithPath("myId").description("myId of close of new Member"),
                                fieldWithPath("auth").description("auth of close of new Member"),
                                fieldWithPath("fileName").description("fileName of close of new Member"),
                                fieldWithPath("newFileName").description("newFileName of close of new Member"),
                                fieldWithPath("del").description("del of close of new Member"),
                                fieldWithPath("_links.self.href").description("link to self"),
//                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-events.href").description("link to update existing event")
                        )
                ))
        ;
    }

    @Test
    @DisplayName("없는 멤버를 조회했을때 404 응답받기")
    public void getMember404() throws Exception{
        //When & Then
        this.mockMvc.perform(get("/api/member/{id}", 11803))
                .andExpect(status().isNotFound())
        ;

    }

    @Test
    @DisplayName("이벤트를 정상적으로 수정하기")
    public void updateMember() throws Exception{
        //Given
        Account account = createAccount();
        MemberEntity member = this.generateMember(100, account);
        
        MemberDTO memberDTO = this.modelMapper.map(member, MemberDTO.class);
        String updateName = "update Name";
        memberDTO.setName(updateName);

        //When & Then
        this.mockMvc.perform(put("/api/member/{id}", member.getMyId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
                        .contentType(Constant.MediaType.ApplicationJsonUtf8.getCode())
                        .content(this.objectMapper.writeValueAsString(memberDTO))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(updateName))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("update-members",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile of hometax")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of Member"),
                                fieldWithPath("classify").description("classify of Member"),
                                fieldWithPath("email").description("email of close of Member"),
                                fieldWithPath("phone").description("phone of close of Member"),
                                fieldWithPath("pwd").description("pwd of close of Member"),
                                fieldWithPath("isSale").description("isSale of close of Member"),
                                fieldWithPath("myId").description("myId of close of Member"),
                                fieldWithPath("auth").description("auth of close of Member"),
                                fieldWithPath("fileName").description("fileName of close of Member"),
                                fieldWithPath("newFileName").description("newFileName of close of Member"),
                                fieldWithPath("inptDttm").description("inptDttm of close of Member"),
                                fieldWithPath("updtDttm").description("updtDttm of close of Member")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("name").description("Name of new Member"),
                                fieldWithPath("classify").description("classify of new Member"),
                                fieldWithPath("email").description("email of close of new Member"),
                                fieldWithPath("phone").description("phone of close of new Member"),
                                fieldWithPath("pwd").description("pwd of close of new Member"),
                                fieldWithPath("isSale").description("isSale of close of new Member"),
                                fieldWithPath("myId").description("myId of close of new Member"),
                                fieldWithPath("auth").description("auth of close of new Member"),
                                fieldWithPath("fileName").description("fileName of close of new Member"),
                                fieldWithPath("newFileName").description("newFileName of close of new Member"),
                                fieldWithPath("del").description("del of close of new Member"),
                                fieldWithPath("_links.self.href").description("link to self")
                        )
                ))
        ;


    }

    private Account createAccount() {
        Account mkw = Account.builder()
                .email(appProperties.getUserUserName())
                .password(appProperties.getUserPassword())
                .roles(Set.of(AccuontRole.Admin, AccuontRole.User))
                .build();
        return this.accountService.saveAccount(mkw);
    }

    @Test
    @DisplayName("입력값이 비어있는경우 수정 실패")
    public void updateMember400_Empty() throws Exception{
        //Given
        MemberEntity member = this.generateMember(100);
        MemberDTO memberDTO = new MemberDTO();

        //When & Then
        this.mockMvc.perform(put("/api/member/{id}", member.getMyId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                        .contentType(Constant.MediaType.ApplicationJsonUtf8.getCode())
                        .content(this.objectMapper.writeValueAsString(memberDTO))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }

    @Test
    @DisplayName("입력값이 잘못되어 있는경우 수정 실패")
    public void updateMember400_Wrong() throws Exception{
        //Given
        MemberEntity member = this.generateMember(100);
        MemberDTO memberDTO = new MemberDTO();

        memberDTO.setInptDttm(LocalDateTime.now().plusHours(1));
        memberDTO.setUpdtDttm(LocalDateTime.now());

        //When & Then
        this.mockMvc.perform(put("/api/member/{id}", member.getMyId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                        .contentType(Constant.MediaType.ApplicationJsonUtf8.getCode())
                        .content(this.objectMapper.writeValueAsString(memberDTO))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }

    @Test
    @DisplayName("존재하지 않는 멤버 수정 실패")
    public void updateMember404() throws Exception{
        //Given
        MemberEntity member = this.generateMember(200);
        MemberDTO memberDTO = this.modelMapper.map(member, MemberDTO.class);

        //When & Then
        this.mockMvc.perform(put("/api/member/{id}", "123151")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                        .contentType(Constant.MediaType.HalJsonUtf8.getCode())
                        .content(this.objectMapper.writeValueAsString(memberDTO))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                ;
    }



}