package com.mkw.hometax.index;

import com.mkw.hometax.common.AppProperties;
import com.mkw.hometax.configs.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WebRestControllerTest extends BaseControllerTest {
    @Autowired
    AppProperties appProperties;

    @Test
    @DisplayName("레스트 컨트롤러 테스트")
    public void webRestControllerTest() throws Exception {
        //when & then
        mockMvc.perform(get("/api/profile")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().string("local"))
                .andExpect(status().isOk());

    }
}