package com.company.semocheck.domain.dto.member;
import com.company.semocheck.domain.Checklist;
import com.company.semocheck.domain.Member;
import com.company.semocheck.domain.MemberCategory;
import com.company.semocheck.domain.dto.category.MemberCategoryDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class MemberDto {
    private Long id;
    private String name;
    private String picture;

    private int inProgressCount;
    private int completeCount;
    private int scrapCount;
    private int ownerCount;
    private List<MemberCategoryDto> categories = new ArrayList<>();

    static public MemberDto createDto(Member member){
        MemberDto dto = new MemberDto();
        dto.id = member.getId();
        dto.name = member.getName();
        dto.picture = member.getPicture();
        dto.scrapCount = member.getScraps().size();

        List<Checklist> checklists = member.getChecklists();
        dto.inProgressCount = checklists.stream().filter(chk -> !chk.getComplete()).toList().size();
        dto.completeCount = checklists.stream().filter(chk -> chk.getComplete()).toList().size();
        dto.ownerCount = checklists.stream().filter(chk -> chk.getOrigin() == null).toList().size();

        for (MemberCategory category : member.getCategories()) {
            dto.categories.add(MemberCategoryDto.createDto(category));
        }

        return dto;
    }
}
