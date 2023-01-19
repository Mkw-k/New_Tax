package com.mkw.hometax.tax.resource;

import com.mkw.hometax.tax.controller.HomeTaxBalanceController;
import com.mkw.hometax.tax.entity.HomeTaxBalanceEntity;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.List;

import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;

public class HomeTaxBalanceResource extends EntityModel<HomeTaxBalanceEntity> {

    public HomeTaxBalanceResource(HomeTaxBalanceEntity content, Link... links) {
        super(content, links);
        add(linkTo(HomeTaxBalanceController.class).slash(content.getMyId()).withSelfRel());
    }
}
