package com.example.demcayniki.config;

import com.example.demcayniki.entity.ConsumerUser;
import com.example.demcayniki.model.constants.UserStatus;
import com.example.demcayniki.repository.ConsumerUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import static com.example.demcayniki.model.constants.UserStatus.*;


@Service
@Transactional
class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    ConsumerUserRepository consumerUserRepository;

    @Override
    public UserDetails loadUserByUsername (String login){
        String normalized = normalizeLogin(login);

        ConsumerUser consumerUser = consumerUserRepository.findByEmailIgnoreCase(normalized)
                .orElseThrow(() -> new UsernameNotFoundException(normalized));

        switch (consumerUser.getStatus()) {
            case ACTIVE:
                throw new RuntimeException();
                break;
            case INACTIVE:
                throw new RuntimeException();
                break;
            case PENDING:
                throw new RuntimeException();
                break;
            case PENDING_REMOVED:
                throw new RuntimeException();
                break;
            default:
                return User
                        .withUsername(consumerUser.getEmail())
                        .password(consumerUser).roles("USER").build();
        }
    }

    private static String normalizeLogin(String login) {
        if (login == null) {
            return "";
        }
        return login.trim().toLowerCase(Locale.ROOT);
    }
}
