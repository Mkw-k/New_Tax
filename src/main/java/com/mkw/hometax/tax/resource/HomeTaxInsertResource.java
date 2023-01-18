package com.mkw.hometax.tax.resource;

import com.mkw.hometax.tax.controller.HomeTaxInsertController;
import com.mkw.hometax.tax.entity.HomeTaxInsertEntity;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;

public class HomeTaxInsertResource extends EntityModel<HomeTaxInsertEntity> {

    public HomeTaxInsertResource(HomeTaxInsertEntity content, Link... links) {
        super(content, links);
        add(linkTo(HomeTaxInsertController.class).slash(content.getDay()).withSelfRel());
    }
}
