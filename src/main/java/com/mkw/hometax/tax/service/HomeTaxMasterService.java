package com.mkw.hometax.tax.service;

import com.mkw.hometax.Accounts.Account;
import com.mkw.hometax.Accounts.CurrentUser;
import com.mkw.hometax.tax.controller.HomeTaxMasterController;
import com.mkw.hometax.tax.dto.HomeTaxMasterDTO;
import com.mkw.hometax.tax.entity.HomeTaxMasterEntity;
import com.mkw.hometax.tax.repository.HomeTaxMasterRepository;
import com.mkw.hometax.tax.resource.HomeTaxMasterResource;
import com.mkw.hometax.tax.validator.HomeTaxMasterValidator;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@Service
public class HomeTaxMasterService {

    @Autowired
    private HomeTaxMasterValidator homeTaxMasterValidator;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    HomeTaxMasterRepository homeTaxMasterRepository;

    public ResponseEntity createHomeTaxMaster(HomeTaxMasterDTO homeTaxMasterDTO,
                                              Errors errors,
                                              HttpServletRequest httpServletRequest,
                                              @CurrentUser Account currentUser){
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
}
