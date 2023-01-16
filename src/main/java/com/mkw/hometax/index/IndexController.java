package com.mkw.hometax.index;

import com.mkw.hometax.member.controller.HomeTaxMasterController;
import com.mkw.hometax.tax.controller.HomeTaxController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
public class IndexController {

    @GetMapping("/api")
    public RepresentationModel index() {
        var index = new RepresentationModel();
        index.add(linkTo(HomeTaxMasterController.class).withRel("members"));
        index.add(linkTo(HomeTaxController.class).withRel("hometax"));
        index.add(linkTo(HomeTaxMasterController.class).withRel("hometaxmaster"));
        index.add(new Link("http://13.209.227.104:8082/docs/index.html", "index"));
        return index;
    }

}
