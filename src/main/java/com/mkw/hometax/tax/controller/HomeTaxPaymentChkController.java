package com.mkw.hometax.tax.controller;

import com.mkw.hometax.tax.resource.HomeTaxPaymentChkResource;
import com.mkw.hometax.tax.validator.HomeTaxPaymentChkValidator;
import com.mkw.hometax.tax.dto.HomeTaxPaymentChkDTO;
import com.mkw.hometax.tax.entity.HomeTaxPaymentChkEntity;
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
@RequestMapping(value = "/api/homtaxpaychk", produces = MediaTypes.HAL_JSON_VALUE)
@Slf4j
public class HomeTaxPaymentChkController {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    HomeTaxPaymentChkRepository homeTaxPaymentChkRepository;

    @Autowired
    HomeTaxPaymentChkValidator homeTaxPaymentChkValidator;

    @PostMapping
    public ResponseEntity createHomeTaxPaymentChk(@RequestBody @Valid HomeTaxPaymentChkDTO homeTaxPaymentChkDTO,
                                              Errors errors,
                                              HttpServletRequest httpServletRequest){
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }
        HomeTaxPaymentChkEntity taxPaymentChkEntity = modelMapper.map(homeTaxPaymentChkDTO, HomeTaxPaymentChkEntity.class);

        homeTaxPaymentChkValidator.validate(homeTaxPaymentChkDTO, errors, httpServletRequest);
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }

        HomeTaxPaymentChkEntity savedHomeTaxPaymentChkEntity = homeTaxPaymentChkRepository.save(taxPaymentChkEntity);

        WebMvcLinkBuilder memberLinkBuilder = linkTo(HomeTaxPaymentChkController.class);
        URI createUri = memberLinkBuilder.toUri();
        HomeTaxPaymentChkResource taxMasterResource = new HomeTaxPaymentChkResource(savedHomeTaxPaymentChkEntity);
        taxMasterResource.add(linkTo(HomeTaxPaymentChkController.class).withRel("query-hometaxpaymentchks"));
        taxMasterResource.add(memberLinkBuilder.withRel("update-hometaxpaymentchk"));

        return ResponseEntity.created(createUri).body(taxMasterResource);
    }

    @GetMapping
    public ResponseEntity queryHomeTaxPaymentChk(Pageable pageable,
                                             PagedResourcesAssembler<HomeTaxPaymentChkEntity> assembler){
        Page<HomeTaxPaymentChkEntity> page = this.homeTaxPaymentChkRepository.findAll(pageable);
        PagedModel<HomeTaxPaymentChkResource> pagedResources = assembler.toModel(page, e -> new HomeTaxPaymentChkResource(e));

        pagedResources.add(new Link("/docs/index.html#resources-hometaxpaymentchk-list").withRel("profile"));

        /*if(account != null){
        }*/
        pagedResources.add(linkTo(HomeTaxMasterController.class).withRel("create-homeTaxPaymentChk"));

        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping(value = "/{day}", produces = "application/hal+json; charset=UTF-8")
    public ResponseEntity getAnHomeTaxPaymentChk(@PathVariable String day){
        Optional<HomeTaxPaymentChkEntity> optionalHomeTaxPaymentChkEntity = this.homeTaxPaymentChkRepository.findByDay(day);
        if(optionalHomeTaxPaymentChkEntity.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        HomeTaxPaymentChkEntity taxPaymentChkEntity = optionalHomeTaxPaymentChkEntity.get();
        HomeTaxPaymentChkResource masterResource = new HomeTaxPaymentChkResource(taxPaymentChkEntity);
        masterResource.add(new Link("/docs/index.html#resources-hometaxpaymentchk-get").withRel("profile"));
        masterResource.add(new Link("/docs/index.html#resources-hometaxpaymentchk-update").withRel("update-hometaxpaymentchk"));

        return ResponseEntity.ok(masterResource);
    }

    @PutMapping(value = "/{day}")
    public ResponseEntity updateHomeTaxPaymentChk(@PathVariable String day,
                                              @RequestBody HomeTaxPaymentChkDTO taxMasterDTO,
                                              Errors errors,
                                              HttpServletRequest httpServletRequest){
        Optional<HomeTaxPaymentChkEntity> taxPaymentChkEntity = homeTaxPaymentChkRepository.findByDay(day);
        if(taxPaymentChkEntity.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        homeTaxPaymentChkValidator.validate(taxMasterDTO, errors, httpServletRequest);
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().build();
        }
        HomeTaxPaymentChkEntity homeTaxPaymentChkEntity = taxPaymentChkEntity.get();

        HomeTaxPaymentChkEntity savedhomeTaxPaymentChkEntity = this.homeTaxPaymentChkRepository.save(homeTaxPaymentChkEntity);

        HomeTaxPaymentChkResource taxMasterResource = new HomeTaxPaymentChkResource(savedhomeTaxPaymentChkEntity);
        taxMasterResource.add(new Link("/docs/index.html#resources-hometaxpaymentchk-update").withRel("profile"));

        return ResponseEntity.ok(taxMasterResource);
    }
}
