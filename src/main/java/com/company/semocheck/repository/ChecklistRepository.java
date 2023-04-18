package com.company.semocheck.repository;

import com.company.semocheck.domain.checklist.Checklist;
import com.company.semocheck.domain.checklist.ChecklistType;
import com.company.semocheck.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {


    //======일반 체크리스트 TYPE:0 ======//
    @Query("SELECT chk FROM Checklist chk WHERE chk.type = 0")
    List<Checklist> findAllChecklist();
    @Query("SELECT chk FROM Checklist chk WHERE chk.type = 0 AND chk.id = ?1")
    Optional<Checklist> findChecklistById(Long id);
    @Query("SELECT chk FROM Checklist chk WHERE chk.type = 0 AND chk.publish = true")
    List<Checklist> findChecklistIsPublished();
    @Query("SELECT chk FROM Checklist chk WHERE chk.type = 0 AND chk.owner = ?1")
    List<Checklist> findChecklistByOwner(Member owner);


    //======임시 체크리스트 TYPE:1 ======//
    @Query("SELECT chk FROM Checklist chk WHERE chk.type = 1")
    List<Checklist> findAllTempChecklist();

    @Query("SELECT chk FROM Checklist chk WHERE chk.type = 1 AND chk.id = ?1")
    Optional<Checklist> findTempChecklistById(Long id);

    @Query("SELECT chk FROM Checklist chk WHERE chk.type = 1 AND chk.owner = ?1")
    List<Checklist> findTempChecklistByOwner(Member owner);
}
