package com.ormi.mogakcote.language.infrastructure;

import com.ormi.mogakcote.language.domain.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language, Integer>{

    Optional<Language> findByLanguageId(Integer languageId);

    void deleteByLanguageId(int languageId);
}
