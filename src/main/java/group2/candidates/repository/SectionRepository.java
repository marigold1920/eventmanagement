package group2.candidates.repository;

import group2.candidates.model.data.Section;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SectionRepository extends JpaRepository<Section, Integer> {

    @Query("select s from Section s where s.event.eventId = ?1")
    Page<Section> loadSectionOfAnEvent(Integer eventId, Pageable pageable);
}
