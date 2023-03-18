package com.company.semocheck.service;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.ErrorMessages;
import com.company.semocheck.domain.Checklist;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.Scrap;
import com.company.semocheck.domain.dto.ScrapDto;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.repository.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScrapService {

    private final ScrapRepository scrapRepository;

    public List<Checklist> getScrap(Member member) {
        List<Checklist> checklists = new ArrayList<>();
        for (Scrap scrap : member.getScraps()) {
            checklists.add(scrap.getChecklist());
        }

        return checklists;
    }

    @Transactional
    public void createScrap(Member member, Checklist checklist) {
        Optional<Scrap> findOne = member.getScraps().stream().filter(scrap -> scrap.getChecklist().getId().equals(checklist.getId())).findFirst();
        if(findOne.isPresent()) throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.EXISTED_SCRAP);

        Scrap scrap = Scrap.createEntity(member, checklist);
        member.addScrap(scrap);

        //scrap count 증가
        checklist.increaseScrapCount();
    }

    @Transactional
    public void deleteScrap(Member member, Checklist checklist) {
        Optional<Scrap> findOne = scrapRepository.findByChecklist(checklist);
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, ErrorMessages.NOT_FOUND_SCRAP);


        member.removeScrap(findOne.get());
        scrapRepository.delete(findOne.get());

        //scrap count 감소
        checklist.decreaseScrapCount();
    }
}
