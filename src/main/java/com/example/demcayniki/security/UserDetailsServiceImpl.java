package com.example.demcayniki.security;

import com.example.demcayniki.domain.entity.ConsumerUser;
import com.example.demcayniki.domain.entity.Role;
import com.example.demcayniki.model.constants.modifiable.UserStatus;
import com.example.demcayniki.repository.ConsumerUserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Locale;

import static com.example.demcayniki.model.constants.modifiable.UserStatus.REMOVED;

@Service
@Transactional(readOnly = true)
class UserDetailsServiceImpl implements UserDetailsService {


    private final ConsumerUserRepository consumerUserRepository;
    public UserDetailsServiceImpl(ConsumerUserRepository consumerUserRepository) {
        this.consumerUserRepository = consumerUserRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String normalizedEmail = normalize(email);

        ConsumerUser user = consumerUserRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (REMOVED.equals(UserStatus.lookup(user.getStatus()))) {
            throw new DisabledException("User removed");
        }
        return User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(
                        user.getRoles().stream().map(Role::getRole).toArray(String[]::new)
                )
                .build();

    }

    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
