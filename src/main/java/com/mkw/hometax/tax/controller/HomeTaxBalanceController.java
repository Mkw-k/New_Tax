package com.mkw.hometax.tax.controller;

import com.mkw.hometax.tax.dto.HomeTaxBalanceDTO;
import com.mkw.hometax.tax.entity.HomeTaxBalanceEntity;
import com.mkw.hometax.tax.repository.HomeTaxBalanceRepository;
import com.mkw.hometax.tax.resource.HomeTaxBalanceResource;
import com.mkw.hometax.tax.validator.HomeTaxBalanceValidator;
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
@RequestMapping(value = "/api/hometaxbalance", produces = MediaTypes.HAL_JSON_VALUE)
@Slf4j
public class HomeTaxBalanceController {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    HomeTaxBalanceRepository homeTaxBalanceRepository;

    @Autowired
    HomeTaxBalanceValidator homeTaxBalanceValidator;

    @PostMapping
    public ResponseEntity createHomeTaxBalance(@RequestBody @Valid HomeTaxBalanceDTO homeTaxBalanceDTO,
                                              Errors errors,
                                              HttpServletRequest httpServletRequest){
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }
        HomeTaxBalanceEntity homeTaxBalanceEntity = modelMapper.map(homeTaxBalanceDTO, HomeTaxBalanceEntity.class);

        homeTaxBalanceValidator.validate(homeTaxBalanceDTO, errors, httpServletRequest);
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }

        HomeTaxBalanceEntity savedHomeTaxBalanceEntity = homeTaxBalanceRepository.save(homeTaxBalanceEntity);

        WebMvcLinkBuilder memberLinkBuilder = linkTo(HomeTaxBalanceController.class);
        URI createUri = memberLinkBuilder.toUri();
        HomeTaxBalanceResource homeTaxBalanceResource = new HomeTaxBalanceResource(savedHomeTaxBalanceEntity);
        homeTaxBalanceResource.add(linkTo(HomeTaxBalanceController.class).withRel("query-hometaxbalances"));
        homeTaxBalanceResource.add(memberLinkBuilder.withRel("update-hometaxbalance"));

        return ResponseEntity.created(createUri).body(homeTaxBalanceResource);
    }

    @GetMapping
    public ResponseEntity queryHomeTaxBalance(Pageable pageable,
                                             PagedResourcesAssembler<HomeTaxBalanceEntity> assembler){
        Page<HomeTaxBalanceEntity> page = this.homeTaxBalanceRepository.findAll(pageable);
        PagedModel<HomeTaxBalanceResource> pagedResources = assembler.toModel(page, e -> new HomeTaxBalanceResource(e));

        pagedResources.add(new Link("/docs/index.html#resources-hometaxbalance-list").withRel("profile"));

        /*if(account != null){
        }*/
        pagedResources.add(linkTo(HomeTaxMasterController.class).withRel("create-hometaxbalance"));

        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping(value = "/{myId}", produces = "application/hal+json; charset=UTF-8")
    public ResponseEntity getAnHomeTaxBalance(@PathVariable String myId){
        Optional<HomeTaxBalanceEntity> homeTaxBalanceEntity = this.homeTaxBalanceRepository.findByMyId(myId);
        if(homeTaxBalanceEntity.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        HomeTaxBalanceEntity taxBalanceEntity = homeTaxBalanceEntity.get();
        HomeTaxBalanceResource homeTaxBalanceResource = new HomeTaxBalanceResource(taxBalanceEntity);
        homeTaxBalanceResource.add(new Link("/docs/index.html#resources-hometaxbalance-get").withRel("profile"));
        homeTaxBalanceResource.add(new Link("/docs/index.html#resources-hometaxbalance-update").withRel("update-hometaxbalance"));

        return ResponseEntity.ok(homeTaxBalanceResource);
    }

    @PutMapping(value = "/{myId}")
    public ResponseEntity updateHomeTaxBalance(@PathVariable String myId,
                                              @RequestBody HomeTaxBalanceDTO homeTaxBalanceDTO,
                                              Errors errors,
                                              HttpServletRequest httpServletRequest){
        Optional<HomeTaxBalanceEntity> homeTaxBalanceEntity = homeTaxBalanceRepository.findByMyId(myId);
        if(homeTaxBalanceEntity.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        homeTaxBalanceValidator.validate(homeTaxBalanceDTO, errors, httpServletRequest);
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().build();
        }
        HomeTaxBalanceEntity taxBalanceEntity = homeTaxBalanceEntity.get();
        taxBalanceEntity.setBalance(homeTaxBalanceDTO.getBalance());

        HomeTaxBalanceEntity savedHomeTaxBalanceEntity = this.homeTaxBalanceRepository.save(taxBalanceEntity);

        HomeTaxBalanceResource homeTaxBalanceResource = new HomeTaxBalanceResource(savedHomeTaxBalanceEntity);
        homeTaxBalanceResource.add(new Link("/docs/index.html#resources-hometaxbalance-update").withRel("profile"));

        return ResponseEntity.ok(homeTaxBalanceResource);
    }

}
