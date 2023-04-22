package com.company.semocheck.service.checklist;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.ErrorMessages;
import com.company.semocheck.domain.checklist.Block;
import com.company.semocheck.domain.checklist.Checklist;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.repository.BlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository blockRepository;


    @Transactional
    public void createBlock(Member member, Checklist checklist){
        if(checklist.getOwner().getId().equals(member.getId())) throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.CANNOT_BLOCK_OWN_CHECKLIST);
        member.getBlocks().stream().filter(blockedChecklist -> blockedChecklist.getChecklist().getId().equals(checklist.getId())).findFirst().ifPresent(blockedChecklist -> {
            throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.ALREADY_BLOCKED_CHECKLIST);
        });

        Block block = Block.createEntity(member, checklist);
        member.addBlock(block);

        blockRepository.save(block);
    }

    @Transactional
    public void deleteBlock(Member member, Checklist checklist){
        Optional<Block> findOne = member.getBlocks().stream()
                .filter(blockedChecklist -> blockedChecklist.getChecklist().getId().equals(checklist.getId()))
                .findFirst();

        if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND);

        member.removeBlock(findOne.get());
        blockRepository.delete(findOne.get());
    }

    public List<Block> getBlocks(Member member) {
        return member.getBlocks();
    }
}
