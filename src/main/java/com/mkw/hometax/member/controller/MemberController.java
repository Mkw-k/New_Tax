package com.mkw.hometax.member.controller;

import com.mkw.hometax.Accounts.Account;
import com.mkw.hometax.Accounts.CurrentUser;
import com.mkw.hometax.common.ErrorsResource;
import com.mkw.hometax.member.MemberRepository;
import com.mkw.hometax.member.MemberResource;
import com.mkw.hometax.member.MemberValidator;
import com.mkw.hometax.member.dto.MemberDTO;
import com.mkw.hometax.member.entity.MemberEntity;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

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
@Deprecated
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
    @PostMapping(produces = "application/hal+json; charset=UTF-8")
    public ResponseEntity createEvent(@RequestBody @Valid MemberDTO memberDTO, @NotNull Errors errors
            , HttpServletRequest httpServletRequest, @CurrentUser Account currentUser){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(errors);

        memberValidator.validate(memberDTO, errors, httpServletRequest);
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(errors);

        log.debug(">>> createEvent 실행됨 !!!");
        MemberEntity member = modelMapper.map(memberDTO, MemberEntity.class);
        member.setManager(currentUser);
        MemberEntity newMember = this.memberRepository.save(member);
        newMember.update();
//        URI createUri = linkTo(MemberController.class).slash(member.getMyId()).toUri();

        WebMvcLinkBuilder memberLinkBuilder = linkTo(MemberController.class);
        URI createUri = memberLinkBuilder.toUri();

        log.debug("멤버 엔터티 확인 >>> " + newMember);

        MemberResource memberResource = new MemberResource(newMember);
        memberResource.add(linkTo(MemberController.class).withRel("query-events"));
        memberResource.add(memberLinkBuilder.withRel("update-events"));
        return ResponseEntity.created(createUri).body(memberResource);
    }

    @GetMapping
    public ResponseEntity queryMembers(Pageable pageable, PagedResourcesAssembler<MemberEntity> assembler
            , @CurrentUser Account account){

        Page<MemberEntity> page = this.memberRepository.findAll(pageable);
        PagedModel<MemberResource> pagedResources = assembler.toModel(page, e -> new MemberResource(e));
        pagedResources.add(new Link("/docs/index.html#resources-member-list").withRel("profile"));

        if(account != null){
            pagedResources.add(linkTo(MemberController.class).withRel("create-events"));
        }
        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping(value = "/{id}", produces = "application/hal+json; charset=UTF-8")
    public ResponseEntity getEvent(@PathVariable String id,
                                   @CurrentUser Account currentUser){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Optional<MemberEntity> optionalMember = this.memberRepository.findById(id);
        if(optionalMember.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        MemberEntity member = optionalMember.get();
        MemberResource memberResource = new MemberResource(member);
//        memberResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
        memberResource.add(new Link("/docs/index.html#resources-events-update").withRel("update-events"));
        if(member.getManager().equals(currentUser)){
            memberResource.add(linkTo(MemberController.class).slash(member.getMyId()).withRel("update-event"));
        }
        return ResponseEntity.ok(memberResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateMember(@PathVariable String id, @RequestBody @Valid MemberDTO memberDTO,
                                       Errors errors,
                                       @CurrentUser Account currentUser){

        Optional<MemberEntity> optionalMember = this.memberRepository.findById(id);
        if(optionalMember.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        if(errors.hasErrors()){
            return badRequest(errors);
        }

        this.memberValidator.validate(memberDTO, errors);
        if(errors.hasErrors()){
            return badRequest(errors);
        }

        //TODO 테스트 작성해야함
        MemberEntity exsitngMemberEntity = optionalMember.get();
        if(!exsitngMemberEntity.getManager().equals(currentUser)){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        this.modelMapper.map(memberDTO, exsitngMemberEntity);
        MemberEntity saveMemberEntity = this.memberRepository.save(exsitngMemberEntity);

        MemberResource memberResource = new MemberResource(saveMemberEntity);
        memberResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(memberResource);
    }

    private ResponseEntity badRequest(Errors errors){
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }




}
