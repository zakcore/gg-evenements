package ca.qc.cgodin.ggevenements.dto;

import ca.qc.cgodin.ggevenements.model.Inscription;

import java.util.List;
import java.util.Set;

/** Données de la page « Mes événements » : deux sous-listes et les événements déjà évalués. */
public record MesEvenements(List<Inscription> aVenir, List<Inscription> passes, Set<Integer> evenementsAvecAvis) {

    public boolean avisSoumis(Integer evenementId) {
        return evenementsAvecAvis.contains(evenementId);
    }

    /** « Laisser un avis » : événement terminé (non annulé), inscription confirmée, aucun avis existant. */
    public boolean peutLaisserAvis(Inscription inscription) {
        return inscription.isConfirmee()
                && inscription.getEvenement().isTermine()
                && !avisSoumis(inscription.getEvenement().getId());
    }
}
