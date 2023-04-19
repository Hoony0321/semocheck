package com.company.semocheck.service.checklist;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.ErrorMessages;
import com.company.semocheck.domain.checklist.BlockedChecklist;
import com.company.semocheck.domain.checklist.Checklist;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.repository.BlockedChecklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BlockedChecklistService {

    private final BlockedChecklistRepository blockedChecklistRepository;


    @Transactional
    public void createBlockedChecklist(Member member, Checklist checklist){
        member.getBlocklist().stream().filter(blockedChecklist -> blockedChecklist.getChecklist().getId().equals(checklist.getId())).findFirst().ifPresent(blockedChecklist -> {
            throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.ALEADY_BLOCKED_CHECKLIST);
        });

        BlockedChecklist blockedChecklist = BlockedChecklist.createEntity(member, checklist);
        member.addBlockedChecklist(blockedChecklist);

        blockedChecklistRepository.save(blockedChecklist);
    }

    @Transactional
    public void deleteBlockedChecklist(Member member, Checklist checklist){
        Optional<BlockedChecklist> findOne = member.getBlocklist().stream()
                                                                .filter(blockedChecklist -> blockedChecklist.getChecklist().getId().equals(checklist.getId()))
                                                                .findFirst();

        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND);

        member.removeBlockedChecklist(findOne.get());
        blockedChecklistRepository.delete(findOne.get());
    }

    public List<BlockedChecklist> getBlocklists(Member member) {
        return member.getBlocklist();
    }
}
