package com.mkw.hometax.tax;

import com.mkw.hometax.tax.dto.HomeTaxMasterDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import javax.servlet.http.HttpServletRequest;

@Component
public class HomeTaxMasterValidator {
    public void validate(HomeTaxMasterDTO taxMasterDTO, Errors errors, HttpServletRequest httpServletRequest){

        if(!taxMasterDTO.getMonthFee().equals("300000")){
            errors.rejectValue("monthFee", "wrongValue", "monthFee must be over 300,000 won");
            errors.reject("wrongValue", "value of monthFee are wrong");
        }

        if(httpServletRequest.getMethod().equals("PUT")){

        }
    }
}
