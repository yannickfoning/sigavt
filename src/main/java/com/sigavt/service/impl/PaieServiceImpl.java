package com.sigavt.service.impl;

import com.sigavt.dto.request.BulletinPaieRequest;
import com.sigavt.entity.BulletinPaie;
import com.sigavt.entity.Personnel;
import com.sigavt.enums.StatutPaiement;
import com.sigavt.exception.RegleMetierException;
import com.sigavt.exception.RessourceIntrouvableException;
import com.sigavt.repository.BulletinPaieRepository;
import com.sigavt.repository.PersonnelRepository;
import com.sigavt.service.PaieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * Calcul de paie adapte au contexte camerounais.
 * NB : les regles CNPS (4,2% salarie / 11,2% employeur) et le bareme IRPP
 * simplifie ci-dessous sont indicatifs. Ils doivent etre ajustes/valides
 * avec un expert comptable / la legislation en vigueur (CGI, CNPS) avant
 * une mise en production reelle.
 */
@Service
@RequiredArgsConstructor
public class PaieServiceImpl implements PaieService {

    private static final BigDecimal TAUX_CNPS_SALARIE = BigDecimal.valueOf(0.042);
    private static final BigDecimal TAUX_CNPS_EMPLOYEUR = BigDecimal.valueOf(0.112);

    private final BulletinPaieRepository bulletinPaieRepository;
    private final PersonnelRepository personnelRepository;

    @Override
    public BulletinPaie generer(BulletinPaieRequest r) {
        Personnel personnel = personnelRepository.findById(r.getPersonnelId())
                .orElseThrow(() -> new RessourceIntrouvableException("Employe introuvable : " + r.getPersonnelId()));

        if (bulletinPaieRepository.findByPersonnel_IdAndPeriode(personnel.getId(), r.getPeriode()).isPresent()) {
            throw new RegleMetierException("Un bulletin existe deja pour cet employe sur la periode " + r.getPeriode());
        }

        BigDecimal salaireBase = personnel.getSalaireBase() != null ? personnel.getSalaireBase() : BigDecimal.ZERO;
        BigDecimal indemniteTransport = valeurOuZero(r.getIndemniteTransport());
        BigDecimal primeAnciennete = valeurOuZero(r.getPrimeAnciennete());
        BigDecimal primePerformance = valeurOuZero(r.getPrimePerformance());
        BigDecimal heuresSup = valeurOuZero(r.getHeuresSup());
        BigDecimal tauxHoraireSup = r.getTauxHoraireSup() != null ? r.getTauxHoraireSup() : BigDecimal.valueOf(1500);

        BigDecimal montantHeuresSup = heuresSup.multiply(tauxHoraireSup).setScale(0, RoundingMode.HALF_UP);
        BigDecimal salaireBrut = salaireBase.add(indemniteTransport).add(primeAnciennete).add(primePerformance).add(montantHeuresSup);

        BigDecimal cnpsSalarie = salaireBrut.multiply(TAUX_CNPS_SALARIE).setScale(0, RoundingMode.HALF_UP);
        BigDecimal cnpsPatronal = salaireBrut.multiply(TAUX_CNPS_EMPLOYEUR).setScale(0, RoundingMode.HALF_UP);
        BigDecimal irpp = calculerIrpp(salaireBrut, cnpsSalarie);
        BigDecimal autresRetenues = valeurOuZero(r.getAutresRetenues());

        BigDecimal netAPayer = salaireBrut.subtract(cnpsSalarie).subtract(irpp).subtract(autresRetenues);

        BulletinPaie bulletin = BulletinPaie.builder()
                .personnel(personnel)
                .periode(r.getPeriode())
                .salaireBase(salaireBase)
                .indemniteTransport(indemniteTransport)
                .heuresSup(heuresSup)
                .tauxHoraireSup(tauxHoraireSup)
                .primeAnciennete(primeAnciennete)
                .primePerformance(primePerformance)
                .cnpsSalarie(cnpsSalarie)
                .cnpsPatronal(cnpsPatronal)
                .irpp(irpp)
                .autresRetenues(autresRetenues)
                .netAPayer(netAPayer)
                .statut(StatutPaiement.EN_ATTENTE)
                .dateGeneration(LocalDate.now())
                .build();

        return bulletinPaieRepository.save(bulletin);
    }

    /**
     * Calcule l'IRPP mensuel camerounais selon le barème 2025.
     * Revenu annuel imposable = (salaire_brut - CNPS_salarié) × 12
     * Tranches annuelles:
     *   0 - 2 000 000 F/an → 11%
     *   2 000 001 - 3 000 000 → 16.5%
     *   3 000 001 - 5 000 000 → 27.5%
     *   > 5 000 000 → 38.5%
     */
    private BigDecimal calculerIrpp(BigDecimal salaireBrut, BigDecimal cotisationCnps) {
        // Revenu imposable mensuel après CNPS
        BigDecimal revenuMensuel = salaireBrut.subtract(cotisationCnps);
        if (revenuMensuel.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;

        // Revenu annuel imposable
        BigDecimal revenuAnnuel = revenuMensuel.multiply(BigDecimal.valueOf(12));

        // Tranches annuelles
        BigDecimal t2M = BigDecimal.valueOf(2_000_000);
        BigDecimal t3M = BigDecimal.valueOf(3_000_000);
        BigDecimal t5M = BigDecimal.valueOf(5_000_000);

        BigDecimal irppAnnuel;
        if (revenuAnnuel.compareTo(t2M) <= 0) {
            irppAnnuel = revenuAnnuel.multiply(BigDecimal.valueOf(0.11));
        } else if (revenuAnnuel.compareTo(t3M) <= 0) {
            irppAnnuel = BigDecimal.valueOf(220_000)
                    .add(revenuAnnuel.subtract(t2M).multiply(BigDecimal.valueOf(0.165)));
        } else if (revenuAnnuel.compareTo(t5M) <= 0) {
            irppAnnuel = BigDecimal.valueOf(385_000)
                    .add(revenuAnnuel.subtract(t3M).multiply(BigDecimal.valueOf(0.275)));
        } else {
            irppAnnuel = BigDecimal.valueOf(935_000)
                    .add(revenuAnnuel.subtract(t5M).multiply(BigDecimal.valueOf(0.385)));
        }

        // IRPP mensuel
        return irppAnnuel.divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP);
    }

    private BigDecimal valeurOuZero(BigDecimal valeur) {
        return valeur != null ? valeur : BigDecimal.ZERO;
    }

    @Override
    public List<BulletinPaie> listerParPeriode(String periode) {
        return periode != null ? bulletinPaieRepository.findByPeriode(periode) : bulletinPaieRepository.findAll();
    }

    @Override
    public BulletinPaie obtenirParId(Long id) {
        return bulletinPaieRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Bulletin introuvable : " + id));
    }

    @Override
    public BulletinPaie marquerPaye(Long id) {
        BulletinPaie b = obtenirParId(id);
        b.setStatut(StatutPaiement.PAYE);
        b.setDatePaiement(LocalDate.now());
        return bulletinPaieRepository.save(b);
    }

    @Override
    public void supprimer(Long id) {
        bulletinPaieRepository.delete(obtenirParId(id));
    }
}
