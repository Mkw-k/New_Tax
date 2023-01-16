package com.mkw.hometax.tax.resource;

import com.mkw.hometax.tax.controller.HomeTaxController;
import com.mkw.hometax.tax.entity.HomeTaxEntity;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class HomeTaxResource extends EntityModel<HomeTaxEntity> {

    public HomeTaxResource(HomeTaxEntity content, Link... links) {
        super(content, links);
        add(linkTo(HomeTaxController.class).slash(content.getDay()).withSelfRel());
    }

}
