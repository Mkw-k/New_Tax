package com.mkw.hometax.member;

import com.mkw.hometax.member.dto.MemberDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class MemberValidator {

    //밸리데이터에서 검증하는 데이터는 필수적으로 들어와야만 됨
    public void validate(MemberDTO memberDTO, Errors errors){
        if(memberDTO.getAuth().equals("3")){
            errors.rejectValue("auth", "wrongValue", "auth is wrong");
            errors.reject("wrongAuth", "value of auth are wrong");
        }

        LocalDateTime inptDttm = memberDTO.getInptDttm();
        if(inptDttm.isAfter(memberDTO.getUpdtDttm())){
            errors.rejectValue("inptDttm", "wrongValue", "inptDttm is wrong");
        }

        //TODO 나머지 검증해야되는 사항들 해야됨
    }
}
