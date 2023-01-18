package com.mkw.hometax.tax.validator;

import com.mkw.hometax.tax.dto.HomeTaxInsertDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Component
public class HomeTaxInsertValidator {

    //밸리데이터에서 검증하는 데이터는 필수적으로 들어와야만 됨
    public void validate(HomeTaxInsertDTO homeTaxInsertDTO, Errors errors, HttpServletRequest httpServletRequest){

        if(Integer.parseInt(homeTaxInsertDTO.getInputFee()) > 2000000){
            errors.rejectValue("inputFee", "wrongValue", "inputFee is too much, wrong request");
            errors.reject("wrongInputFee", "value of inputFee is too much");
        }

        if(homeTaxInsertDTO.getInsertDate().isAfter(LocalDateTime.now())){
            errors.rejectValue("insertDay", "wrongValue", "The insertDay is later than the present, wrong request");
            errors.reject("wrongInsertDay", "value of insertDay is later than the present");
        }

        //TODO 나머지 검증해야되는 사항들 해야됨
    }
}
