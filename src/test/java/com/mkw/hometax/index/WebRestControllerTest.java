package com.mkw.hometax.index;

import com.mkw.hometax.common.AppProperties;
import com.mkw.hometax.configs.BaseControllerTest;
import org.springframework.beans.factory.annotation.Autowired;


class WebRestControllerTest extends BaseControllerTest {
    @Autowired
    AppProperties appProperties;

    /*@Test
    @DisplayName("레스트 컨트롤러 테스트")
    @Disabled("TODO 개발예정")
    public void webRestControllerTest() throws Exception {
        //when & then
        mockMvc.perform(get("/api/profile")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().string("local"))
                .andExpect(status().isOk());

    }*/
}