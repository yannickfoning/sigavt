package com.sigavt.repository;

import com.sigavt.entity.ConfigurationMetier;
import com.sigavt.enums.CategorieConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigurationMetierRepository extends JpaRepository<ConfigurationMetier, Long> {

    Optional<ConfigurationMetier> findByCle(String cle);

    List<ConfigurationMetier> findByCategorie(CategorieConfiguration categorie);
}
