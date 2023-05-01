package com.mkw.hometax.study.controller;

import com.mkw.hometax.tax.dto.HomeTaxBalanceDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/study")
public class StudyController {

    @GetMapping
    public String study(Model model) {
        HomeTaxBalanceDTO taxBalanceDTO1 = HomeTaxBalanceDTO.builder()
                .myId("2021-12-25")
                .balance("158.1")
                .build();

        HomeTaxBalanceDTO taxBalanceDTO2 = HomeTaxBalanceDTO.builder()
                .myId("2020-12-25")
                .balance("156.1")
                .build();

        HomeTaxBalanceDTO taxBalanceDTO3 = HomeTaxBalanceDTO.builder()
                .myId("2019-12-25")
                .balance("152.1")
                .build();

        HomeTaxBalanceDTO taxBalanceDTO4 = HomeTaxBalanceDTO.builder()
                .myId("2018-12-25")
                .balance("151.1")
                .build();

        List<HomeTaxBalanceDTO> homeTaxBalanceDTOList = new ArrayList<>();
        homeTaxBalanceDTOList.add(taxBalanceDTO1);
        homeTaxBalanceDTOList.add(taxBalanceDTO2);
        homeTaxBalanceDTOList.add(taxBalanceDTO3);
        homeTaxBalanceDTOList.add(taxBalanceDTO4);

        List<String> balanceList = homeTaxBalanceDTOList.stream()
                .map(HomeTaxBalanceDTO::getBalance)
                .collect(Collectors.toList());

        List<String> myDateList= homeTaxBalanceDTOList.stream()
                .map(HomeTaxBalanceDTO::getMyId)
                .collect(Collectors.toList());

        model.addAttribute("item", taxBalanceDTO1);
        model.addAttribute("hometaxBalanceDTOList", homeTaxBalanceDTOList);
        model.addAttribute("balanceList", balanceList);
        model.addAttribute("myDateList", myDateList);

        return "study";
    }
}