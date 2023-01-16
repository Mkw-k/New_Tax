package com.mkw.hometax.tax.validator;

import com.mkw.hometax.tax.dto.HomeTaxPaymentChkDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Component
public class HomeTaxPaymentChkValidator {
    public void validate(HomeTaxPaymentChkDTO taxPaymentChkDTO, Errors errors, HttpServletRequest httpServletRequest){

        if(!Arrays.asList("Y", "N").contains(taxPaymentChkDTO.getMonthFee().toUpperCase())){
            errors.rejectValue("monthFee", "wrongValue", "monthFee must be Y or N");
            errors.reject("wrongValue", "value of monthFee are wrong");
        }

        if(httpServletRequest.getMethod().equals("PUT")){

        }
    }
}
