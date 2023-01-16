package com.mkw.hometax.tax.resource;

import com.mkw.hometax.tax.controller.HomeTaxPaymentChkController;
import com.mkw.hometax.tax.entity.HomeTaxMasterEntity;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class HomeTaxMasterResource extends EntityModel<HomeTaxMasterEntity> {

    public HomeTaxMasterResource(HomeTaxMasterEntity content, Link... links) {
        super(content, links);
        add(linkTo(HomeTaxPaymentChkController.class).slash(content.getDay()).withSelfRel());
    }
}
