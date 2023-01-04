package com.mkw.hometax.member;

import com.mkw.hometax.member.dto.MemberDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import javax.servlet.http.HttpServletRequest;

@Deprecated
@Component
public class MemberValidator {

    //밸리데이터에서 검증하는 데이터는 필수적으로 들어와야만 됨
    public void validate(MemberDTO memberDTO, Errors errors){

        if(memberDTO.getAuth().equals("3")){
            errors.rejectValue("auth", "wrongValue", "auth 3 is Admin value, wrong request");
            errors.reject("wrongAuth", "value of auth are wrong");
        }

        //UpdtDttm이 존재할경우, UpdtDttm은 언제나 InptDttm보다 이후여야만 한다
        if(StringUtils.hasLength(memberDTO.getUpdtDttm().toString())){
            if(memberDTO.getUpdtDttm().isAfter(memberDTO.getInptDttm())){
                errors.rejectValue("inptDttm", "wrongValue", "inptDttm is wrong");
            }
        }

        //TODO 나머지 검증해야되는 사항들 해야됨
    }

    public void validate(MemberDTO memberDTO, Errors errors, HttpServletRequest httpServletRequest){

        if(memberDTO.getAuth().equals("3")){
            errors.rejectValue("auth", "wrongValue", "auth 3 is Admin value, wrong request");
            errors.reject("wrongAuth", "value of auth are wrong");
        }

        //UpdtDttm이 존재할경우, UpdtDttm은 언제나 InptDttm보다 이후여야만 한다
        if(httpServletRequest.getMethod().equals("PUT")){
            if(StringUtils.hasLength(memberDTO.getUpdtDttm().toString())){
                if(memberDTO.getUpdtDttm().isAfter(memberDTO.getInptDttm())){
                    errors.rejectValue("inptDttm", "wrongValue", "inptDttm is wrong");
                }
            }
        }

        //TODO 나머지 검증해야되는 사항들 해야됨
    }
}
