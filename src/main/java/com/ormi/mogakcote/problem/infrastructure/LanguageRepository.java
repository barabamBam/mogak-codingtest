package com.ormi.mogakcote.problem.infrastructure;

import com.ormi.mogakcote.problem.domain.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language, Long>{

    Optional<Language> findByLanguageId(Long languageId);

    void deleteByLanguageId(Long languageId);
}
