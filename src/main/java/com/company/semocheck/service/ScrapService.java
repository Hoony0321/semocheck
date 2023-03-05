package com.company.semocheck.service;

import com.company.semocheck.common.response.Code;
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

    public List<ScrapDto> getScrap(Member member) {
        List<ScrapDto> scrapDtos = new ArrayList<>();
        for (Scrap scrap : member.getScraps()) {
            ScrapDto dto = ScrapDto.createDto(scrap);
            scrapDtos.add(dto);
        }

        return scrapDtos;
    }

    @Transactional
    public void createScrap(Member member, Checklist checklist) {
        Optional<Scrap> findOne = member.getScraps().stream().filter(scrap -> scrap.getChecklist().getId().equals(checklist.getId())).findFirst();
        if(findOne.isPresent()) throw new GeneralException(Code.BAD_REQUEST, "이미 스크랩한 체크리스트입니다.");

        Scrap scrap = Scrap.createEntity(member, checklist);
        member.addScrap(scrap);

        //scrap count 증가
        checklist.increaseScrapCount();
    }

    @Transactional
    public void deleteScrap(Member member, Checklist checklist) {
        Optional<Scrap> findOne = scrapRepository.findByChecklist(checklist);
        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, "해당 id의 체크리스트는 스크랩 목록에 존재하지 않습니다.");

        member.removeScrap(findOne.get());
        scrapRepository.delete(findOne.get());

        //scrap count 감소
        checklist.decreaseScrapCount();
    }
}
