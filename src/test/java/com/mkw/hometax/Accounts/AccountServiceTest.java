package com.mkw.hometax.Accounts;

import lombok.extern.slf4j.Slf4j;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
//@RunWith(SpringRunner.class)
@ExtendWith({SpringExtension.class})
@ActiveProfiles("test")
@SpringBootTest
public class AccountServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Test
    @DisplayName("스프링 시큐리티 테스트 기본 틀 1")
    public void findByUserName(){
        //Given
        String email = "rhauddn111@hanmail.net";
        String password = "mkw";

        Account account = Account.builder()
                .email(email)
                .password(password)
                .roles(Set.of(AccuontRole.Admin, AccuontRole.User))
                .build();
        accountRepository.save(account);

        //When
        UserDetailsService userDetailsService = (UserDetailsService) accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        //Then
        assertThat(userDetails.getPassword()).isEqualTo(password);
    }

    @Test
    public void findByUsernameFail(){
        String userName = "randome@email.com";
        /*expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage(Matchers.containsString(userName));
        accountService.loadUserByUsername(userName);*/

        Assertions.assertThrows(UsernameNotFoundException.class, () ->{
            accountService.loadUserByUsername(userName);
        });
    }

}