package com.mkw.hometax.member.service;

import com.mkw.hometax.member.MemberRepository;
import com.mkw.hometax.member.entity.MemberEntity;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.NullableUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.transaction.Transactional;

/**
* MemberService에 대한 설명을 여기에 적는다
* <pre>
* </pre>
* @author : K
* @class : MemberService
* @date : 2022-05-28
* <pre>
* No Date        Time       Author  Desc
* 1  2022-05-28  오전 10:22  K       최초작성
* </pre>
*/
@Slf4j
@NoArgsConstructor
@Transactional
public class MemberService {
    //repository
    MemberRepository memberRepository;

    /**
    * @name
    * <PRE>
    * </PRE>
    * @MethodName : regiMember
    * @Author : K
    * @ModifiedDate : 2022-05-28 오전 10:35
    * @returnType : 
    */
    public MemberEntity regiMember(@RequestBody MemberEntity member){
        log.debug("member정보 확인 >>> " + member.toString());
        MemberEntity save = memberRepository.save(member);
        if(save == null){
            log.debug(">>> 저장실패");
        }else{
            log.debug(">>> 저장성공");
        }

        return save;
    }

}
