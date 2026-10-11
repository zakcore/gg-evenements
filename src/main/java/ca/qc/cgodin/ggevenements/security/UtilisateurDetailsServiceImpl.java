package ca.qc.cgodin.ggevenements.security;

import ca.qc.cgodin.ggevenements.repository.UtilisateurRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UtilisateurDetailsServiceImpl implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurDetailsServiceImpl(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String courriel) throws UsernameNotFoundException {
        return utilisateurRepository.findByCourrielIgnoreCase(courriel.trim())
                .map(UtilisateurDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Identifiants invalides"));
    }
}
