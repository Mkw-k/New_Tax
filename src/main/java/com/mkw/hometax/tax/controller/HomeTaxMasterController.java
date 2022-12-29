package com.mkw.hometax.tax.controller;

import com.mkw.hometax.tax.HomeTaxMasterResource;
import com.mkw.hometax.tax.HomeTaxMasterValidator;
import com.mkw.hometax.tax.dto.HomeTaxMasterDTO;
import com.mkw.hometax.tax.entity.HomeTaxMasterEntity;
import com.mkw.hometax.tax.repository.HomeTaxMasterRepository;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/homtaxmaster", produces = MediaTypes.HAL_JSON_VALUE)
@Slf4j
public class HomeTaxMasterController {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    HomeTaxMasterRepository homeTaxMasterRepository;

    @Autowired
    HomeTaxMasterValidator homeTaxMasterValidator;

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

        HomeTaxMasterEntity savedTaxMaster = homeTaxMasterRepository.save(taxMasterEntity);

        WebMvcLinkBuilder memberLinkBuilder = linkTo(HomeTaxMasterController.class);
        URI createUri = memberLinkBuilder.toUri();
        HomeTaxMasterResource taxMasterResource = new HomeTaxMasterResource(savedTaxMaster);
        taxMasterResource.add(linkTo(HomeTaxMasterController.class).withRel("query-tax"));
        taxMasterResource.add(memberLinkBuilder.withRel("update-tax"));

        return ResponseEntity.created(createUri).body(taxMasterResource);
    }

    @GetMapping
    public ResponseEntity queryHomeTaxMaster(Pageable pageable,
                                             PagedResourcesAssembler<HomeTaxMasterEntity> assembler){
        Page<HomeTaxMasterEntity> page = this.homeTaxMasterRepository.findAll(pageable);
        PagedModel<HomeTaxMasterResource> pagedResources = assembler.toModel(page, e -> new HomeTaxMasterResource(e));

        pagedResources.add(new Link("/docs/index.html#resources-hometaxmaster-list").withRel("profile"));

        return ResponseEntity.ok(pagedResources);
    }
}
