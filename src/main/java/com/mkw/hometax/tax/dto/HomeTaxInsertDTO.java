package com.mkw.hometax.tax.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class HomeTaxInsertDTO {
    @NotEmpty
    private String myId;
    private String inputFee;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime insertDate;
    @NotEmpty
    private String day;
    private String delYn;
    private String confirmYn;
}
