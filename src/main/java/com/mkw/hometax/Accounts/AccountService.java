package com.mkw.hometax.Accounts;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AccountService implements UserDetailsService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Account saveAccount(Account account){
        account.setPassword((this.passwordEncoder.encode(account.getPassword())));
        log.info("AccountService.saveAccount ▶▶▶ start");
        log.info("account ▶▶▶ " + account.toString());
        return this.accountRepository.save(account);
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(userName)
                .orElseThrow(() -> new UsernameNotFoundException(userName));

        return new AccountAdapter(account);
    }

    private Collection<? extends GrantedAuthority> authorities(Set<AccuontRole> roles) {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .collect(Collectors.toSet())
                ;
    }
}
