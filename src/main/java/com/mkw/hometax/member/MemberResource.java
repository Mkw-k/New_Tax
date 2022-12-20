package com.mkw.hometax.member;


import com.mkw.hometax.member.controller.MemberController;
import com.mkw.hometax.member.entity.MemberEntity;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class MemberResource extends EntityModel<MemberEntity> {

    public MemberResource(MemberEntity member, Link... links) {
        super(member, Arrays.asList(links));
        add(linkTo(MemberController.class).slash(member.getMyId()).withSelfRel());
    }

}
