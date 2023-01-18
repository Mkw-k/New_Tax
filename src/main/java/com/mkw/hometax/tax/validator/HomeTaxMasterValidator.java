package com.mkw.hometax.tax.validator;

import com.mkw.hometax.tax.dto.HomeTaxMasterDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import javax.servlet.http.HttpServletRequest;

@Component
public class HomeTaxMasterValidator {

    //밸리데이터에서 검증하는 데이터는 필수적으로 들어와야만 됨
    public void validate(HomeTaxMasterDTO homeTaxMasterDTO, Errors errors, HttpServletRequest httpServletRequest){

        if(!homeTaxMasterDTO.getMonthFee().equals("300000")){
            errors.rejectValue("monthFee", "wrongValue", "monthFee is not 300000, wrog request");
            errors.reject("wrongMonthFee", "value of monthFee are wrong");
        }

        //UpdtDttm이 존재할경우, UpdtDttm은 언제나 InptDttm보다 이후여야만 한다
        if(httpServletRequest.getMethod().equals("PUT")){
            if(StringUtils.hasLength(homeTaxMasterDTO.getUpdtDttm().toString())){
                if(homeTaxMasterDTO.getUpdtDttm().isAfter(homeTaxMasterDTO.getInptDttm())){
                    errors.rejectValue("inptDttm", "wrongValue", "inptDttm is wrong");
                }
            }
        }

        //TODO 나머지 검증해야되는 사항들 해야됨
    }
}
