package com.mkw.hometax.tax.controller;

import com.mkw.hometax.member.controller.MemberController;
import com.mkw.hometax.tax.resource.HomeTaxResource;
import com.mkw.hometax.tax.validator.HomeTaxValidator;
import com.mkw.hometax.tax.dto.HomeTaxDTO;
import com.mkw.hometax.tax.dto.HomeTaxMasterDTO;
import com.mkw.hometax.tax.entity.HomeTaxEntity;
import com.mkw.hometax.tax.repository.HomeTaxRepository;
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
@RequestMapping(value = "/api/hometax", produces = MediaTypes.HAL_JSON_VALUE)
@Slf4j
public class HomeTaxController {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    HomeTaxRepository homeTaxRepository;

    @Autowired
    HomeTaxValidator homeTaxValidator;

    @PostMapping
    public ResponseEntity createHomeTax(@RequestBody @Valid HomeTaxDTO taxDTO,
                                              Errors errors,
                                              HttpServletRequest httpServletRequest){
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }
        taxDTO.calculateTotalFee();
        HomeTaxEntity taxEntity = modelMapper.map(taxDTO, HomeTaxEntity.class);

        homeTaxValidator.validate(taxDTO, errors, httpServletRequest);
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }

        HomeTaxEntity savedTaxEntity = homeTaxRepository.save(taxEntity);

        WebMvcLinkBuilder memberLinkBuilder = linkTo(HomeTaxController.class);
        URI createUri = memberLinkBuilder.toUri();
        HomeTaxResource taxResource = new HomeTaxResource(savedTaxEntity);
        taxResource.add(linkTo(HomeTaxController.class).withRel("query-hometaxs"));
        taxResource.add(memberLinkBuilder.withRel("update-hometax"));

        return ResponseEntity.created(createUri).body(taxResource);
    }

    @GetMapping
    public ResponseEntity queryHomeTax(Pageable pageable,
                                             PagedResourcesAssembler<HomeTaxEntity> assembler){
        Page<HomeTaxEntity> page = this.homeTaxRepository.findAll(pageable);
        PagedModel<HomeTaxResource> pagedResources = assembler.toModel(page, e -> new HomeTaxResource(e));

        pagedResources.add(new Link("/docs/index.html#resources-hometax-list").withRel("profile"));

        /*if(account != null){
        }*/
        pagedResources.add(linkTo(MemberController.class).withRel("create-hometax"));

        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping(value = "/{day}", produces = "application/hal+json; charset=UTF-8")
    public ResponseEntity getAnHomeTax(@PathVariable String day){
        Optional<HomeTaxEntity> optionalHomeTaxEntity = this.homeTaxRepository.findByDay(day);
        if(optionalHomeTaxEntity.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        HomeTaxEntity homeTaxEntity = optionalHomeTaxEntity.get();
        HomeTaxResource homeTaxResource = new HomeTaxResource(homeTaxEntity);
        homeTaxResource.add(new Link("/docs/index.html#resources-hometaxs-get").withRel("profile"));
        homeTaxResource.add(new Link("/docs/index.html#resources-hometax-update").withRel("update-hometax"));

        return ResponseEntity.ok(homeTaxResource);
    }

    @PutMapping(value = "/{day}")
    public ResponseEntity updateHomeTax(@PathVariable String day,
                                              @RequestBody HomeTaxDTO taxDTO,
                                              Errors errors,
                                              HttpServletRequest httpServletRequest){

        Optional<HomeTaxEntity> optionalHomeTax = homeTaxRepository.findByDay(day);
        if(optionalHomeTax.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        homeTaxValidator.validate(taxDTO, errors, httpServletRequest);
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().build();
        }
        HomeTaxEntity homeTaxEntity = optionalHomeTax.get();

        //TODO 하단의 코드는 개선이 필요함
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        this.modelMapper.map(taxDTO, homeTaxEntity);
        HomeTaxMasterDTO taxMasterDTOForCalculateTotalFee = this.modelMapper.map(homeTaxEntity, HomeTaxMasterDTO.class);
        taxMasterDTOForCalculateTotalFee.calculateTotalFee();
        modelMapper.map(taxMasterDTOForCalculateTotalFee, homeTaxEntity);

        HomeTaxEntity savedHomeTaxEntity = this.homeTaxRepository.save(homeTaxEntity);

        HomeTaxResource taxResource = new HomeTaxResource(savedHomeTaxEntity);
        taxResource.add(new Link("/docs/index.html#resources-hometax-update").withRel("profile"));

        return ResponseEntity.ok(taxResource);
    }
}
