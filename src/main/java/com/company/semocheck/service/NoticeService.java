package com.company.semocheck.service;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.ErrorMessages;
import com.company.semocheck.domain.Notice;
import com.company.semocheck.domain.dto.notice.CreateNoticeRequest;
import com.company.semocheck.domain.dto.notice.UpdateNoticeRequest;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.company.semocheck.common.response.ErrorMessages.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public Notice findById(Long id) {
        Optional<Notice> findOne = noticeRepository.findById(id);
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, NOT_FOUND_NOTICE);

        return findOne.get();
    }

    public List<Notice> findAll(){
        return noticeRepository.findAll();
    }

    @Transactional
    public Long createNotice(CreateNoticeRequest request){
        Notice notice = Notice.createEntity(request);
        noticeRepository.save(notice);
        return notice.getId();
    }

    @Transactional
    public void updateNotice(Long id, UpdateNoticeRequest request){
        Notice notice = findById(id);
        notice.update(request);
    }

    @Transactional
    public void deleteNotice(Long id){
        Notice notice = findById(id);
        noticeRepository.delete(notice);
    }


}
