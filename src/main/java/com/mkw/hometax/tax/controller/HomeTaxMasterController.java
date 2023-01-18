package com.mkw.hometax.tax.controller;

import com.mkw.hometax.Accounts.Account;
import com.mkw.hometax.Accounts.CurrentUser;
import com.mkw.hometax.common.ErrorsResource;
import com.mkw.hometax.tax.validator.HomeTaxMasterValidator;
import com.mkw.hometax.tax.dto.HomeTaxMasterDTO;
import com.mkw.hometax.tax.entity.HomeTaxMasterEntity;
import com.mkw.hometax.tax.repository.HomeTaxMasterRepository;
import com.mkw.hometax.tax.resource.HomeTaxMasterResource;
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
@Slf4j
@Controller
@RequestMapping(value = "/api/homtaxmaster", produces = MediaTypes.HAL_JSON_VALUE)
public class HomeTaxMasterController {
    //service
//    private final MemberService memberService;

    private final HomeTaxMasterRepository homeTaxMasterRepository;

    private final ModelMapper modelMapper;

    private final HomeTaxMasterValidator homeTaxMasterValidator;

    public HomeTaxMasterController(HomeTaxMasterRepository homeTaxMasterRepository, ModelMapper modelMapper, HomeTaxMasterValidator homeTaxMasterValidator) {
        this.homeTaxMasterRepository = homeTaxMasterRepository;
        this.modelMapper = modelMapper;
        this.homeTaxMasterValidator = homeTaxMasterValidator;
    }

    /**
     * mockMvc테스트용 메서드
     * @return
     */
    @PostMapping(produces = "application/hal+json; charset=UTF-8")
    public ResponseEntity createHomeTaxMaster(@RequestBody @Valid HomeTaxMasterDTO homeTaxMasterDTO, @NotNull Errors errors
            , HttpServletRequest httpServletRequest, @CurrentUser Account currentUser){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(errors);

        //월세 총합 계산
        homeTaxMasterDTO.calculateTotalFee();

        homeTaxMasterValidator.validate(homeTaxMasterDTO, errors, httpServletRequest);
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(errors);

        log.debug(">>> createEvent 실행됨 !!!");
        HomeTaxMasterEntity taxMasterEntity = modelMapper.map(homeTaxMasterDTO, HomeTaxMasterEntity.class);
        taxMasterEntity.setManager(currentUser);
        HomeTaxMasterEntity savedTaxMasterEntity = this.homeTaxMasterRepository.save(taxMasterEntity);
//        URI createUri = linkTo(MemberController.class).slash(member.getMyId()).toUri();

        WebMvcLinkBuilder memberLinkBuilder = linkTo(HomeTaxMasterController.class);
        URI createUri = memberLinkBuilder.toUri();

        log.debug("멤버 엔터티 확인 >>> " + savedTaxMasterEntity);

        HomeTaxMasterResource homeTaxMasterResource = new HomeTaxMasterResource(savedTaxMasterEntity);
        homeTaxMasterResource.add(linkTo(HomeTaxMasterController.class).withRel("query-hometaxmasters"));
        homeTaxMasterResource.add(memberLinkBuilder.withRel("update-hometaxmaster"));
        return ResponseEntity.created(createUri).body(homeTaxMasterResource);
    }

    @GetMapping
    public ResponseEntity queryMembers(Pageable pageable, PagedResourcesAssembler<HomeTaxMasterEntity> assembler
            , @CurrentUser Account account){

        Page<HomeTaxMasterEntity> page = this.homeTaxMasterRepository.findAll(pageable);
        PagedModel<HomeTaxMasterResource> pagedResources = assembler.toModel(page, e -> new HomeTaxMasterResource(e));
        pagedResources.add(new Link("/docs/index.html#resources-hometaxmaster-list").withRel("profile"));

        if(account != null){
            pagedResources.add(linkTo(HomeTaxMasterController.class).withRel("create-hometaxmaster"));
        }
        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping(value = "/{day}", produces = "application/hal+json; charset=UTF-8")
    public ResponseEntity getEvent(@PathVariable String day,
                                   @CurrentUser Account currentUser){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Optional<HomeTaxMasterEntity> optionalHomeTaxMaster = this.homeTaxMasterRepository.findByDay(day);
        if(optionalHomeTaxMaster.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        HomeTaxMasterEntity taxMasterEntity = optionalHomeTaxMaster.get();
        HomeTaxMasterResource homeTaxMasterResource = new HomeTaxMasterResource(taxMasterEntity);
//        memberResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
        homeTaxMasterResource.add(new Link("/docs/index.html#resources-hometaxmaster-update").withRel("update-hometaxmaster"));
        if(taxMasterEntity.getManager().equals(currentUser)){
            homeTaxMasterResource.add(linkTo(HomeTaxMasterController.class).slash(taxMasterEntity.getDay()).withRel("update-hometaxmaster"));
        }
        return ResponseEntity.ok(homeTaxMasterResource);
    }

    @PutMapping("/{day}")
    public ResponseEntity updateMember(@PathVariable String day, @RequestBody @Valid HomeTaxMasterDTO taxMasterDTO,
                                       Errors errors,
                                       @CurrentUser Account currentUser,
                                       HttpServletRequest httpServletRequest){

        Optional<HomeTaxMasterEntity> optionalHomeTaxMaster = this.homeTaxMasterRepository.findByDay(day);
        if(optionalHomeTaxMaster.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        if(errors.hasErrors()){
            return badRequest(errors);
        }

        this.homeTaxMasterValidator.validate(taxMasterDTO, errors, httpServletRequest);
        if(errors.hasErrors()){
            return badRequest(errors);
        }

        //TODO 테스트 작성해야함
        HomeTaxMasterEntity exsitngMemberEntity = optionalHomeTaxMaster.get();
        if(!exsitngMemberEntity.getManager().equals(currentUser)){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        this.modelMapper.map(taxMasterDTO, exsitngMemberEntity);
        HomeTaxMasterEntity savedHomeTaxMasterEntity = this.homeTaxMasterRepository.save(exsitngMemberEntity);

        HomeTaxMasterResource homeTaxMasterResource = new HomeTaxMasterResource(savedHomeTaxMasterEntity);
        homeTaxMasterResource.add(new Link("/docs/index.html#resources-members-update").withRel("profile"));

        return ResponseEntity.ok(homeTaxMasterResource);
    }

    private ResponseEntity badRequest(Errors errors){
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }




}
