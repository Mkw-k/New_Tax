package com.mkw.hometax.tax.validator;

import com.mkw.hometax.tax.dto.HomeTaxBalanceDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import javax.servlet.http.HttpServletRequest;

@Component
public class HomeTaxBalanceValidator {

    //밸리데이터에서 검증하는 데이터는 필수적으로 들어와야만 됨
    public void validate(HomeTaxBalanceDTO homeTaxBalanceDTO, Errors errors, HttpServletRequest httpServletRequest){

        if(StringUtils.isEmpty(homeTaxBalanceDTO.getBalance())){
            errors.rejectValue("balance", "wrongValue", "balance is wrong, wrong request");
            errors.reject("wrongBalance", "value of balance is wrong");
        }

        //TODO 나머지 검증해야되는 사항들 해야됨
    }
}
