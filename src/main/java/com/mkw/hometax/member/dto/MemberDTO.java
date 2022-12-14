package com.mkw.hometax.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

/**
* MemberDTO에 대한 설명을 여기에 적는다
* <pre>
* </pre>
* @author : K
* @class : MemberDTO
* @date : 2022-05-28
* <pre>
* No Date        Time       Author  Desc
* 1  2022-05-28  오전 10:39  K       최초작성
* </pre>
*/
@Deprecated
@Data @NoArgsConstructor @Builder @AllArgsConstructor
public class MemberDTO {
    @NotEmpty
    private String name;
    @NotEmpty
    private String pwd;
    @NotEmpty
    private String isSale;
    @NotEmpty
    private String myId;
    private String classify;
    private String email;
    private String phone;
    private String fileName;
    private String newFileName;
    @Min(0)
    private String auth;
    @CreationTimestamp
    private LocalDateTime inptDttm;
    @UpdateTimestamp
    private LocalDateTime updtDttm;
}
