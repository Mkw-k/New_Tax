package com.mkw.hometax.tax.controller;

import com.mkw.hometax.member.controller.MemberController;
import com.mkw.hometax.tax.HomeTaxMasterResource;
import com.mkw.hometax.tax.HomeTaxPaymentChkValidator;
import com.mkw.hometax.tax.dto.HomeTaxMasterDTO;
import com.mkw.hometax.tax.entity.HomeTaxMasterEntity;
import com.mkw.hometax.tax.repository.HomeTaxPaymentChkRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/homtaxmaster", produces = MediaTypes.HAL_JSON_VALUE)
@Slf4j
public class HomeTaxPaymentChk {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    HomeTaxPaymentChkRepository homeTaxPaymentChkRepository;

    @Autowired
    HomeTaxPaymentChkValidator homeTaxMasterValidator;

    @PostMapping
    public ResponseEntity createHomeTaxMaster(@RequestBody @Valid HomeTaxMasterDTO masterDTO,
                                              Errors errors,
                                              HttpServletRequest httpServletRequest){
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }
        masterDTO.calculateTotalFee();
        HomeTaxMasterEntity taxMasterEntity = modelMapper.map(masterDTO, HomeTaxMasterEntity.class);

        homeTaxMasterValidator.validate(masterDTO, errors, httpServletRequest);
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }

        HomeTaxMasterEntity savedTaxMaster = homeTaxPaymentChkRepository.save(taxMasterEntity);

        WebMvcLinkBuilder memberLinkBuilder = linkTo(HomeTaxPaymentChk.class);
        URI createUri = memberLinkBuilder.toUri();
        HomeTaxMasterResource taxMasterResource = new HomeTaxMasterResource(savedTaxMaster);
        taxMasterResource.add(linkTo(HomeTaxPaymentChk.class).withRel("query-hometaxmasters"));
        taxMasterResource.add(memberLinkBuilder.withRel("update-hometaxmaster"));

        return ResponseEntity.created(createUri).body(taxMasterResource);
    }

    @GetMapping
    public ResponseEntity queryHomeTaxMaster(Pageable pageable,
                                             PagedResourcesAssembler<HomeTaxMasterEntity> assembler){
        Page<HomeTaxMasterEntity> page = this.homeTaxPaymentChkRepository.findAll(pageable);
        PagedModel<HomeTaxMasterResource> pagedResources = assembler.toModel(page, e -> new HomeTaxMasterResource(e));

        pagedResources.add(new Link("/docs/index.html#resources-hometaxmaster-list").withRel("profile"));

        /*if(account != null){
        }*/
        pagedResources.add(linkTo(MemberController.class).withRel("create-hometaxmaster"));

        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping(value = "/{day}", produces = "application/hal+json; charset=UTF-8")
    public ResponseEntity getAnHomeTaxMaster(@PathVariable String day){
        Optional<HomeTaxMasterEntity> optionalHomeTaxMasterEntity = this.homeTaxPaymentChkRepository.findByDay(day);
        if(optionalHomeTaxMasterEntity.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        HomeTaxMasterEntity homeTaxMasterEntity = optionalHomeTaxMasterEntity.get();
        HomeTaxMasterResource masterResource = new HomeTaxMasterResource(homeTaxMasterEntity);
        masterResource.add(new Link("/docs/index.html#resources-hometaxmasters-get").withRel("profile"));
        masterResource.add(new Link("/docs/index.html#resources-hometaxmasters-update").withRel("update-hometaxmaster"));

        return ResponseEntity.ok(masterResource);
    }

    @PutMapping(value = "/{day}")
    public ResponseEntity updateHomeTaxMaster(@PathVariable String day,
                                              @RequestBody HomeTaxMasterDTO taxMasterDTO,
                                              Errors errors,
                                              HttpServletRequest httpServletRequest){

        Optional<HomeTaxMasterEntity> optionalHomeTaxMaster = homeTaxPaymentChkRepository.findByDay(day);
        if(optionalHomeTaxMaster.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        homeTaxMasterValidator.validate(taxMasterDTO, errors, httpServletRequest);
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().build();
        }
        HomeTaxMasterEntity homeTaxMasterEntity = optionalHomeTaxMaster.get();

        //TODO 하단의 코드는 개선이 필요함
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        this.modelMapper.map(taxMasterDTO, homeTaxMasterEntity);
        HomeTaxMasterDTO taxMasterDTOForCalculateTotalFee = this.modelMapper.map(homeTaxMasterEntity, HomeTaxMasterDTO.class);
        taxMasterDTOForCalculateTotalFee.calculateTotalFee();
        modelMapper.map(taxMasterDTOForCalculateTotalFee, homeTaxMasterEntity);

        HomeTaxMasterEntity savedHomeTaxMasterEntity = this.homeTaxPaymentChkRepository.save(homeTaxMasterEntity);

        HomeTaxMasterResource taxMasterResource = new HomeTaxMasterResource(savedHomeTaxMasterEntity);
        taxMasterResource.add(new Link("/docs/index.html#resources-hometaxmasters-update").withRel("profile"));

        return ResponseEntity.ok(taxMasterResource);
    }
}
