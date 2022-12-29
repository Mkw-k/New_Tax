package com.mkw.hometax.tax;

import com.mkw.hometax.tax.controller.HomeTaxMasterController;
import com.mkw.hometax.tax.entity.HomeTaxMasterEntity;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class HomeTaxMasterResource extends EntityModel<HomeTaxMasterEntity> {

    public HomeTaxMasterResource(HomeTaxMasterEntity content, Link... links) {
        super(content, links);
        add(linkTo(HomeTaxMasterController.class).slash(content.getDay()).withSelfRel());
    }
}
