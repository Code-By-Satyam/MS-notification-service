package com.microservice.notification.notification_service.services;

import com.microservice.notification.notification_service.configs.UserInfoUserDetails;
import com.microservice.notification.notification_service.dao.UserInfoRepository;
import com.microservice.notification.notification_service.entities.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

@Component
public class UserInfoUserDetailsService implements UserDetailsService {

    private static final Logger logger = LogManager.getLogger(UserInfoUserDetailsService.class);

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserInfo> userInfo = userInfoRepository.findByName(username);
        if (userInfo.isPresent()) {
            logger.info("User found: {}", username);
            return new UserInfoUserDetails(userInfo.get());
        } else {
            logger.error("User not found: {}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }
}
