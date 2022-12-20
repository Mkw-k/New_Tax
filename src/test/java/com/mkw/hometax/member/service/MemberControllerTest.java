package com.mkw.hometax.member.service;

import com.mkw.hometax.common.Constant;
import com.mkw.hometax.common.TestDecription;
import com.mkw.hometax.configs.BaseControllerTest;
import com.mkw.hometax.member.MemberRepository;
import com.mkw.hometax.member.dto.MemberDTO;
import com.mkw.hometax.member.entity.MemberEntity;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
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

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(member)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
        ;
    }

    @Test
    @DisplayName("필수 입력값이 누락된경우 에러발생")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        MemberDTO memberDTO = MemberDTO.builder().build();

        this.mockMvc.perform(post("/api/member")
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
    
    //TODO 수정
    
    //TODO 삭제(삭제를 진짜로 추가할지 아니면 코드값만 변경할지 결정해야함)

   /* @Test
    @Deprecated
    @DisplayName("왜 실패하는지 이유를 알수가 없는 테스트임")
    public void setTest() throws Exception {
        MemberEntity member = MemberEntity.builder()
                .name("홍길동")
                .myId("gd123")
                .auth("1")
                .isSale("0")
                .pwd("123")
                .homeSeq("2")
                .build();

        Mockito.when(memberRepository.save(member)).thenReturn(member);

        mockMvc.perform(post("/api/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(member))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("myId").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
        ;
    }*/

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

    private MemberEntity generateMember(int index) {
        MemberEntity memberEntity = MemberEntity.builder()
                .myId("member " + index)
                .name("홍길동")
                .build();

        return this.memberRepository.save(memberEntity);
    }

    private MemberEntity generateMemberForUpdate(int index) {
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
                .build();

        return this.memberRepository.save(memberEntity);
    }

    @Test
    @DisplayName("기존의 멤버 하나를 조회하기")
    public void getMember() throws Exception{
        //Given
        MemberEntity member = this.generateMember(100);

        //When & Then
        this.mockMvc.perform(get("/api/member/{id}", member.getMyId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andDo(document("get-event",
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
        MemberEntity member = this.generateMemberForUpdate(100);
        MemberDTO memberDTO = this.modelMapper.map(member, MemberDTO.class);
        String updateName = "update Name";
        memberDTO.setName(updateName);

        //When & Then
        this.mockMvc.perform(put("/api/member/{id}", member.getMyId())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(memberDTO))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(updateName))
                .andExpect(jsonPath("_links.self").exists());


    }

    @Test
    @DisplayName("입력값이 비어있는경우 수정 실패")
    public void updateMember400_Empty() throws Exception{
        //Given
        MemberEntity member = this.generateMember(100);
        MemberDTO memberDTO = new MemberDTO();

        //When & Then
        this.mockMvc.perform(put("/api/member/{id}", member.getMyId())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
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
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
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
                        .contentType(Constant.MediaType.HalJsonUtf8.getCode())
                        .content(this.objectMapper.writeValueAsString(memberDTO))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                ;
    }



}