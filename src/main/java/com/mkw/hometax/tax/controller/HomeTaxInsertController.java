package com.mkw.hometax.tax.controller;

import com.mkw.hometax.tax.dto.HomeTaxInsertDTO;
import com.mkw.hometax.tax.entity.HomeTaxInsertEntity;
import com.mkw.hometax.tax.repository.HomeTaxInsertReepository;
import com.mkw.hometax.tax.resource.HomeTaxInsertResource;
import com.mkw.hometax.tax.validator.HomeTaxInsertValidator;
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
@RequestMapping(value = "/api/hometaxinsert", produces = MediaTypes.HAL_JSON_VALUE)
@Slf4j
public class HomeTaxInsertController {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    HomeTaxInsertReepository homeTaxInsertReepository;

    @Autowired
    HomeTaxInsertValidator homeTaxInsertValidator;

    @PostMapping
    public ResponseEntity createHomeTaxInsert(@RequestBody @Valid HomeTaxInsertDTO homeTaxInsertDTO,
                                              Errors errors,
                                              HttpServletRequest httpServletRequest){
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }
        HomeTaxInsertEntity homeTaxInsertEntity = modelMapper.map(homeTaxInsertDTO, HomeTaxInsertEntity.class);

        homeTaxInsertValidator.validate(homeTaxInsertDTO, errors, httpServletRequest);
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }

        HomeTaxInsertEntity savedHomeTaxInsertEntity = homeTaxInsertReepository.save(homeTaxInsertEntity);

        WebMvcLinkBuilder memberLinkBuilder = linkTo(HomeTaxInsertController.class);
        URI createUri = memberLinkBuilder.toUri();
        HomeTaxInsertResource homeTaxInsertResource = new HomeTaxInsertResource(savedHomeTaxInsertEntity);
        homeTaxInsertResource.add(linkTo(HomeTaxInsertController.class).withRel("query-hometaxinserts"));
        homeTaxInsertResource.add(memberLinkBuilder.withRel("update-hometaxinsert"));

        return ResponseEntity.created(createUri).body(homeTaxInsertResource);
    }

    @GetMapping
    public ResponseEntity queryHomeTaxInsert(Pageable pageable,
                                             PagedResourcesAssembler<HomeTaxInsertEntity> assembler){
        Page<HomeTaxInsertEntity> page = this.homeTaxInsertReepository.findAll(pageable);
        PagedModel<HomeTaxInsertResource> pagedResources = assembler.toModel(page, e -> new HomeTaxInsertResource(e));

        pagedResources.add(new Link("/docs/index.html#resources-hometaxInsert-list").withRel("profile"));

        /*if(account != null){
        }*/
        pagedResources.add(linkTo(HomeTaxMasterController.class).withRel("create-homeTaxInsert"));

        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping(value = "/{myId}", produces = "application/hal+json; charset=UTF-8")
    public ResponseEntity getAnHomeTaxPaymentChk(@PathVariable String myId){
        java.util.Optional<HomeTaxInsertEntity> homeTaxInsertEntity = this.homeTaxInsertReepository.findByMyId(myId);
        if(homeTaxInsertEntity.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        HomeTaxInsertEntity taxInsertEntity = homeTaxInsertEntity.get();
        HomeTaxInsertResource taxInsertResource = new HomeTaxInsertResource(taxInsertEntity);
        taxInsertResource.add(new Link("/docs/index.html#resources-hometaxinsert-get").withRel("profile"));
        taxInsertResource.add(new Link("/docs/index.html#resources-hometaxinsert-update").withRel("update-hometaxinsert"));

        return ResponseEntity.ok(taxInsertResource);
    }

    @PutMapping(value = "/{myId}")
    public ResponseEntity updateHomeTaxPaymentChk(@PathVariable String myId,
                                              @RequestBody HomeTaxInsertDTO taxInsertDTO,
                                              Errors errors,
                                              HttpServletRequest httpServletRequest){
        Optional<HomeTaxInsertEntity> taxInsertEntity = homeTaxInsertReepository.findByMyId(myId);
        if(taxInsertEntity.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        homeTaxInsertValidator.validate(taxInsertDTO, errors, httpServletRequest);
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().build();
        }
        HomeTaxInsertEntity homeTaxInsertEntity = taxInsertEntity.get();

        HomeTaxInsertEntity savedHomeTaxInsertEntity = this.homeTaxInsertReepository.save(homeTaxInsertEntity);

        HomeTaxInsertResource taxInsertResource = new HomeTaxInsertResource(savedHomeTaxInsertEntity);
        taxInsertResource.add(new Link("/docs/index.html#resources-hometaxinsert-update").withRel("profile"));

        return ResponseEntity.ok(taxInsertResource);
    }

}
