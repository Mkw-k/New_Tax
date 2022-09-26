package com.mkw.hometax.member.controller;

import com.mkw.hometax.member.MemberRepository;
import com.mkw.hometax.member.MemberValidator;
import com.mkw.hometax.member.dto.MemberDTO;
import com.mkw.hometax.member.entity.MemberEntity;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
* MemberController에 대한 설명을 여기에 적는다
* <pre>
* </pre>
* @author : K
* @class : MemberController
* @date : 2022-05-28
* <pre>
* No Date        Time       Author  Desc
* 1  2022-05-28  오전 10:34  K       최초작성
* </pre>
*/
@Slf4j
@Controller
@RequestMapping(value = "/api/member", produces = MediaTypes.HAL_JSON_VALUE)
public class MemberController {
    //service
//    private final MemberService memberService;

    private final MemberRepository memberRepository;

    private final ModelMapper modelMapper;

    private final MemberValidator memberValidator;

    public MemberController(MemberRepository memberRepository, ModelMapper modelMapper, MemberValidator memberValidator) {
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
        this.memberValidator = memberValidator;
    }

    /**
     * mockMvc테스트용 메서드
     * @return
     */
    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid MemberDTO memberDTO, Errors errors){
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(errors);

        memberValidator.validate(memberDTO, errors);
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(errors);

        log.debug(">>> createEvent 실행됨 !!!");
        MemberEntity member = modelMapper.map(memberDTO, MemberEntity.class);
        MemberEntity newMember = this.memberRepository.save(member);
        newMember.update();
//        URI createUri = linkTo(MemberController.class).slash(member.getMyId()).toUri();
        URI createUri = linkTo(MemberController.class).toUri();
        log.debug("멤버 엔터티 확인 >>> " + newMember);
        return ResponseEntity.created(createUri).body(newMember);
    }

    /**
    * <PRE>
    * </PRE>
    * @MethodName : regiMember
    * @Author : K
    * @ModifiedDate : 2022-05-28 오전 10:34
    * @returnType : 
    */
   /* @PostMapping
    public void regiMember(@RequestBody MemberEntity member){
        memberService.regiMember(member);
    }*/


}
