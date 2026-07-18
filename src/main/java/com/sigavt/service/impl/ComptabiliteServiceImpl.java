package com.sigavt.service.impl;

import com.sigavt.dto.request.EcritureComptableRequest;
import com.sigavt.entity.EcritureComptable;
import com.sigavt.enums.TypeEcriture;
import com.sigavt.exception.RessourceIntrouvableException;
import com.sigavt.repository.EcritureComptableRepository;
import com.sigavt.service.ComptabiliteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ComptabiliteServiceImpl implements ComptabiliteService {

    private final EcritureComptableRepository ecritureComptableRepository;

    @Override
    public EcritureComptable creer(EcritureComptableRequest r) {
        if (r.getLibelle() == null || r.getLibelle().isBlank()) {
            throw new com.sigavt.exception.RegleMetierException("Le libelle est obligatoire");
        }
        String numeroEcriture = genererNumeroEcriture();
        EcritureComptable e = EcritureComptable.builder()
                .numeroEcriture(numeroEcriture)
                .dateEcriture(r.getDateEcriture() != null ? r.getDateEcriture() : LocalDate.now())
                .libelle(r.getLibelle())
                .description(r.getDescription())
                .categorie(r.getCategorie())
                .typeEcriture(r.getTypeEcriture() != null ? TypeEcriture.valueOf(r.getTypeEcriture().toUpperCase()) : null)
                .compteDebit(r.getCompteDebit())
                .compteCredit(r.getCompteCredit())
                .montantDebit(r.getMontantDebit() != null ? r.getMontantDebit() : BigDecimal.ZERO)
                .montantCredit(r.getMontantCredit() != null ? r.getMontantCredit() : BigDecimal.ZERO)
                .debit(r.getDebit() != null ? r.getDebit() : BigDecimal.ZERO)
                .credit(r.getCredit() != null ? r.getCredit() : BigDecimal.ZERO)
                .reference(r.getReference())
                .build();
        return ecritureComptableRepository.save(e);
    }

    private String genererNumeroEcriture() {
        String prefix = "EC-" + LocalDate.now().getYear();
        long count = ecritureComptableRepository.count() + 1;
        return String.format("%s-%04d", prefix, count);
    }

    @Override
    public EcritureComptable obtenirParId(Long id) {
        return ecritureComptableRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Ecriture introuvable : " + id));
    }

    @Override
    public List<EcritureComptable> listerParPeriode(LocalDate debut, LocalDate fin) {
        LocalDate d = debut != null ? debut : LocalDate.now().withDayOfMonth(1);
        LocalDate f = fin != null ? fin : LocalDate.now();
        return ecritureComptableRepository.findByDateEcritureBetweenOrderByDateEcritureDesc(d, f);
    }

    @Override
    public Map<String, Object> genererBilan(int mois, int annee) {
        if (mois == 0 || annee == 0) {
            YearMonth ym = YearMonth.now();
            mois = ym.getMonthValue();
            annee = ym.getYear();
        }
        YearMonth ym = YearMonth.of(annee, mois);
        List<EcritureComptable> ecritures = ecritureComptableRepository
                .findByDateEcritureBetweenOrderByDateEcritureDesc(ym.atDay(1), ym.atEndOfMonth());

        BigDecimal recettes = ecritures.stream().map(EcritureComptable::getCredit).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal depenses = ecritures.stream().map(EcritureComptable::getDebit).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal benefice = recettes.subtract(depenses);
        double marge = recettes.compareTo(BigDecimal.ZERO) > 0
                ? benefice.doubleValue() / recettes.doubleValue() * 100
                : 0;

        Map<String, Object> resume = new HashMap<>();
        resume.put("periode", ym.toString());
        resume.put("recettes", recettes);
        resume.put("depenses", depenses);
        resume.put("benefice", benefice);
        resume.put("margePourcent", Math.round(marge * 10) / 10.0);
        return resume;
    }

    @Override
    public void supprimer(Long id) {
        ecritureComptableRepository.delete(obtenirParId(id));
    }
}
