package com.company.semocheck.domain.request.inquiry;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.exception.GeneralException;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateInquiryCommentRequest {
    private String content;

    public void validate(){
        if(content == null || content.isEmpty()){
            throw new GeneralException(Code.ILLEGAL_REQUEST_FORM);
        }
    }
}
