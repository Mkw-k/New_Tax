package com.mkw.hometax;

import com.mkw.hometax.member.entity.MemberEntity;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;

@Slf4j
@RunWith(JUnitParamsRunner.class)
public class MemberTest {

    @Test
    public void builder(){
        MemberEntity member =MemberEntity.builder()
                .name("123")
                .build();
        Assertions.assertThat(member).isNotNull();
    }


    @Test
    @Parameters(method = "parametersForTestIsSale")
    public void testIsSale(String isSale){
        //given
        MemberEntity memberDTO = MemberEntity.builder()
                .isSale(isSale)
                .build();

        //when
        memberDTO.update();

        log.debug("확인 >>> " + memberDTO.isSaleBool());

        //then
        Assertions.assertThat(memberDTO.isSaleBool());
    }

    private Object[] parametersForTestIsSale(){
        return new Object[]{
                new Object[] {"1"},
                new Object[] {"0"}
        };
    }
}
