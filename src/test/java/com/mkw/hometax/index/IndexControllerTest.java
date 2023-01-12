package com.mkw.hometax.index;

import com.mkw.hometax.configs.BaseControllerTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class IndexControllerTest extends BaseControllerTest {

    @Test
    @DisplayName("인덱스 조회하기")
    public void getIndex() throws Exception{
        //Given

        //When & Then
        this.mockMvc.perform(get("/api"))
                .andExpect(status().isOk())
                .andDo(document("index",
                        links(
                                linkWithRel("members").description("link to self"),
                                linkWithRel("hometax").description("link to hometax"),
                                linkWithRel("hometaxmaster").description("link to hometaxmaster"),
                                linkWithRel("index").description("link to index of api")
                        )
                ))
        ;
    }
}