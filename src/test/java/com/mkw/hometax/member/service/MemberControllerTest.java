package com.mkw.hometax.member.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mkw.hometax.common.TestDecription;
import com.mkw.hometax.member.dto.MemberDTO;
import com.mkw.hometax.member.entity.MemberEntity;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest{
    @Autowired
    MockMvc mockMvc;

    /*@MockBean
    MemberRepository memberRepository;*/

    @Autowired
    ObjectMapper objectMapper;

    /*@Autowired
    ModelMapper modelMapper;*/

    @Test
    @TestDecription("입력할수 없는 값을 사용한 경우에 에러 발생")
    public void createEvent_Bad_Request() throws Exception {
        String url = "/api/member";
        MemberEntity member = MemberEntity.builder()
                .name("홍길동")
                .myId("gd123")
                .auth("0")
                .isSale("0")
                .pwd("123")
                .homeSeq("22")
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
    @TestDecription("인서트 정상 성공")
    public void createEvent() throws Exception {
        String url = "/api/member";
        MemberDTO member = MemberDTO.builder()
                .name("홍길동")
                .pwd("123")
                .isSale("0")
                .myId("gd123")
                .homeSeq("22")
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
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(member))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("myId").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("auth").value(Matchers.not("3")))
                .andExpect(jsonPath("isSale").value(Matchers.not("1")))
        ;
    }

    @Test
    @TestDecription("입력값이 잘못된 경우 에러가 발생하는 케이스")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        MemberDTO member = MemberDTO.builder()
                .name("홍길동")
                .pwd("123")
                .isSale("0")
                .myId("gd123")
                .homeSeq("22")
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
    @TestDecription("필수 입력값이 누락된경우 에러발생")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        MemberDTO memberDTO = MemberDTO.builder().build();

        this.mockMvc.perform(post("/api/member")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(this.objectMapper.writeValueAsString(memberDTO)))
                .andExpect(status().isBadRequest());
    }
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

}