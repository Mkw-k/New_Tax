package com.mkw.hometax.tax.controller;

import com.mkw.hometax.Accounts.Account;
import com.mkw.hometax.Accounts.CurrentUser;
import com.mkw.hometax.common.ErrorsResource;
import com.mkw.hometax.tax.dto.HomeTaxMasterDTO;
import com.mkw.hometax.tax.entity.HomeTaxMasterEntity;
import com.mkw.hometax.tax.repository.HomeTaxMasterRepository;
import com.mkw.hometax.tax.resource.HomeTaxMasterResource;
import com.mkw.hometax.tax.service.HomeTaxMasterService;
import com.mkw.hometax.tax.validator.HomeTaxMasterValidator;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
    private final HomeTaxMasterService homeTaxMasterService;

    public HomeTaxMasterController(HomeTaxMasterRepository homeTaxMasterRepository,
                                   ModelMapper modelMapper,
                                   HomeTaxMasterValidator homeTaxMasterValidator,
                                   HomeTaxMasterService homeTaxMasterService) {
        this.homeTaxMasterRepository = homeTaxMasterRepository;
        this.modelMapper = modelMapper;
        this.homeTaxMasterValidator = homeTaxMasterValidator;
        this.homeTaxMasterService = homeTaxMasterService;
    }

    @PostMapping(produces = "application/hal+json; charset=UTF-8")
    public ResponseEntity createHomeTaxMaster(@RequestBody @Valid HomeTaxMasterDTO homeTaxMasterDTO, @NotNull Errors errors
            , HttpServletRequest httpServletRequest, @CurrentUser Account currentUser){
        return homeTaxMasterService.createHomeTaxMaster(homeTaxMasterDTO, errors, httpServletRequest, currentUser);
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
