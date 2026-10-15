package ca.qc.cgodin.ggevenements.service;

import ca.qc.cgodin.ggevenements.exception.RegleAffaireException;
import ca.qc.cgodin.ggevenements.model.*;
import ca.qc.cgodin.ggevenements.repository.AvisRepository;
import ca.qc.cgodin.ggevenements.repository.EvenementRepository;
import ca.qc.cgodin.ggevenements.repository.InscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Règles d'affaires d'inscription : capacité, doublons et promotion de la liste d'attente. */
@ExtendWith(MockitoExtension.class)
class InscriptionServiceTest {

    private static final Integer EVENEMENT_ID = 1;
    private static final Integer UTILISATEUR_ID = 7;

    @Mock private InscriptionRepository inscriptionRepository;
    @Mock private EvenementRepository evenementRepository;
    @Mock private AvisRepository avisRepository;
    @Mock private EvenementService evenementService;
    @Mock private UtilisateurService utilisateurService;

    @InjectMocks private InscriptionService service;

    private Evenement evenement;
    private Utilisateur utilisateur;

    @BeforeEach
    void preparer() {
        evenement = new Evenement();
        evenement.setCapaciteMax(2);
        evenement.setStatut(StatutEvenement.PLANIFIE);
        evenement.setDateDebut(LocalDateTime.now().plusDays(3));
        evenement.setDateFin(LocalDateTime.now().plusDays(3).plusHours(2));
        utilisateur = new Utilisateur();
        utilisateur.setPrenom("Camille");
        utilisateur.setNom("Pelletier");
        when(evenementRepository.findByIdPourMiseAJour(EVENEMENT_ID)).thenReturn(Optional.of(evenement));
    }

    @Test
    void inscriptionConfirmeeTantQueLaCapaciteNEstPasAtteinte() {
        when(inscriptionRepository.countByEvenementIdAndStatut(EVENEMENT_ID, StatutInscription.CONFIRMEE)).thenReturn(1L);
        when(utilisateurService.trouver(UTILISATEUR_ID)).thenReturn(utilisateur);

        assertThat(service.inscrire(EVENEMENT_ID, UTILISATEUR_ID)).isEqualTo(StatutInscription.CONFIRMEE);
        verify(inscriptionRepository).save(any(Inscription.class));
    }

    @Test
    void inscriptionEnAttenteLorsqueLEvenementEstComplet() {
        when(inscriptionRepository.countByEvenementIdAndStatut(EVENEMENT_ID, StatutInscription.CONFIRMEE)).thenReturn(2L);
        when(utilisateurService.trouver(UTILISATEUR_ID)).thenReturn(utilisateur);

        assertThat(service.inscrire(EVENEMENT_ID, UTILISATEUR_ID)).isEqualTo(StatutInscription.EN_ATTENTE);
    }

    @Test
    void doubleInscriptionRefuseeParLaCoucheService() {
        when(inscriptionRepository.existsByEvenementIdAndUtilisateurId(EVENEMENT_ID, UTILISATEUR_ID)).thenReturn(true);

        assertThatThrownBy(() -> service.inscrire(EVENEMENT_ID, UTILISATEUR_ID))
                .isInstanceOf(RegleAffaireException.class);
        verify(inscriptionRepository, never()).save(any());
    }

    @Test
    void desinscriptionDUnConfirmePromeutLePremierEnAttente() {
        Inscription moi = new Inscription(evenement, utilisateur, StatutInscription.CONFIRMEE);
        Inscription premierEnAttente = new Inscription(evenement, new Utilisateur(), StatutInscription.EN_ATTENTE);
        when(inscriptionRepository.findByEvenementIdAndUtilisateurId(EVENEMENT_ID, UTILISATEUR_ID)).thenReturn(Optional.of(moi));
        when(inscriptionRepository.findFirstByEvenementIdAndStatutOrderByDateInscriptionAscIdAsc(EVENEMENT_ID, StatutInscription.EN_ATTENTE))
                .thenReturn(Optional.of(premierEnAttente));

        service.desinscrire(EVENEMENT_ID, UTILISATEUR_ID);

        verify(inscriptionRepository).delete(moi);
        assertThat(premierEnAttente.getStatut()).isEqualTo(StatutInscription.CONFIRMEE);
    }

    @Test
    void desinscriptionDUnePersonneEnAttenteNePromeutPersonne() {
        Inscription moi = new Inscription(evenement, utilisateur, StatutInscription.EN_ATTENTE);
        when(inscriptionRepository.findByEvenementIdAndUtilisateurId(EVENEMENT_ID, UTILISATEUR_ID)).thenReturn(Optional.of(moi));

        service.desinscrire(EVENEMENT_ID, UTILISATEUR_ID);

        verify(inscriptionRepository).delete(moi);
        verify(inscriptionRepository, never()).findFirstByEvenementIdAndStatutOrderByDateInscriptionAscIdAsc(any(), any());
    }
}
