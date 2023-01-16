package com.mkw.hometax.tax.validator;

import com.mkw.hometax.tax.dto.HomeTaxDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import javax.servlet.http.HttpServletRequest;

@Component
public class HomeTaxValidator {
    public void validate(HomeTaxDTO taxDTO, Errors errors, HttpServletRequest httpServletRequest){

        if(!taxDTO.getMonthFee().equals("300000")){
            errors.rejectValue("monthFee", "wrongValue", "monthFee must be over 300,000 won");
            errors.reject("wrongValue", "value of monthFee are wrong");
        }

        if(httpServletRequest.getMethod().equals("PUT")){

        }
    }
}
