package com.mkw.hometax.member;


import com.mkw.hometax.member.entity.MemberEntity;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

public class MemberResource extends EntityModel<MemberEntity> {

    public MemberResource(MemberEntity event, Link... links) {
        super(event, Arrays.asList(links));
    }

}
