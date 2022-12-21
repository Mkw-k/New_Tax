package com.mkw.hometax.configs;


import com.mkw.hometax.Accounts.Account;
import com.mkw.hometax.Accounts.AccountRepository;
import com.mkw.hometax.Accounts.AccountService;
import com.mkw.hometax.Accounts.AccuontRole;
import com.mkw.hometax.common.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

@Configuration
@Slf4j
public class AppConfig {
    private int runCnt = 0;

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ApplicationRunner applicationRunner(){
        return new ApplicationRunner() {
            @Autowired
            AccountService accountService;

            @Autowired
            AppProperties appProperties;

            @Autowired
            AccountRepository accountRepository;

            //TODO 현재 조회후 넣는 방식으로 검증 작업을 진행중인데 저런 방식 말고도 Run 메소드가 중복되지 않게 하는
            //방법이 없을까????
            @Override
            public void run(ApplicationArguments args) throws Exception {
                log.info("runnable 실행되었음");

                Optional<Account> adminOptional = accountRepository.findByEmail(appProperties.getAdminUserName());
                if(adminOptional.isEmpty()){
                    Account admin = Account.builder()
                            .email(appProperties.getAdminUserName())
                            .password(appProperties.getAdminPassword())
                            .roles(Set.of(AccuontRole.Admin, AccuontRole.User))
                            .build();
                    accountService.saveAccount(admin);
                }

                Optional<Account> userOptional = accountRepository.findByEmail(appProperties.getUserUserName());
                if(userOptional.isEmpty()){
                    Account user = Account.builder()
                            .email(appProperties.getUserUserName())
                            .password(appProperties.getUserPassword())
                            .roles(Set.of(AccuontRole.Admin))
                            .build();
                    accountService.saveAccount(user);
                }
            }
        };
    }
}
