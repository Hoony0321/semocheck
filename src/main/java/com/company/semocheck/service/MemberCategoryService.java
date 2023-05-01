package com.company.semocheck.service;

import com.company.semocheck.common.response.Code;
import com.company.semocheck.common.response.ErrorMessages;
import com.company.semocheck.domain.category.SubCategory;
import com.company.semocheck.domain.dto.category.MemberCategoryDto;
import com.company.semocheck.domain.dto.category.SubCategoryDto;
import com.company.semocheck.domain.member.Member;
import com.company.semocheck.domain.member.MemberCategory;
import com.company.semocheck.exception.GeneralException;
import com.company.semocheck.repository.MemberCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCategoryService {

    private final MemberCategoryRepository memberCategoryRepository;
    private final CategoryService categoryService;

    public List<MemberCategoryDto> getCategories(Member member) {
        List<MemberCategoryDto> memberCategoryDtos = new ArrayList<>();
        for (MemberCategory category : member.getCategories()) {
            memberCategoryDtos.add(MemberCategoryDto.createDto(category));
        }

        return memberCategoryDtos;
    }

    @Transactional
    public void initMemberCategory(Member member, List<SubCategoryDto> subCategoryDtos){
        List<MemberCategory> memberCategories = new ArrayList<>();
        for(SubCategoryDto subCategoryDto : subCategoryDtos){
            SubCategory subCategory = categoryService.findSubCategoryByName(subCategoryDto.getMain(), subCategoryDto.getName());
            MemberCategory memberCategory = MemberCategory.createEntity(member, subCategory);
            memberCategories.add(memberCategory);
        }

        member.setCategory(memberCategories);
    }

    @Transactional
    public void addMemberCategory(Member member, List<SubCategory> categories) {
        for (SubCategory category : categories) {
            Optional<MemberCategory> findOne = member.getCategories().stream()
                    .filter(ct -> ct.getSubCategory().getId().equals(category.getId())).findFirst();
            if(findOne.isPresent()) throw new GeneralException(Code.BAD_REQUEST, ErrorMessages.EXISTED_CATEGORY);

            MemberCategory memberCategory = MemberCategory.createEntity(member, category);
            member.addCategory(memberCategory);
        }
    }

    @Transactional
    public void updateMemberCategory(Member member, List<SubCategory> categories) {
        // clear memberCategory
        for (MemberCategory category : member.getCategories()) {
            memberCategoryRepository.delete(category);
        }

        // add new memberCategory
        List<MemberCategory> memberCategories = new ArrayList<>();
        for(SubCategory category : categories){
            MemberCategory memberCategory = MemberCategory.createEntity(member, category);
            memberCategoryRepository.save(memberCategory);
            memberCategories.add(memberCategory);
        }

        member.setCategory(memberCategories);
    }

    @Transactional
    public void deleteMemberCategory(Member member, List<SubCategory> categories) {
        for(SubCategory category : categories){
            Optional<MemberCategory> findOne = member.getCategories().stream()
                    .filter(ct -> ct.getSubCategory().getId().equals(category.getId())).findFirst();
            if(findOne.isEmpty()) throw new GeneralException(Code.NOT_FOUND, ErrorMessages.NOT_FOUND_CHECKLIST);

            member.removeCategory(findOne.get());
            memberCategoryRepository.delete(findOne.get());
        }
    }
}
