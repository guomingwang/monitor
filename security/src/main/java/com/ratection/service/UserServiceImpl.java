package com.ratection.service;

import cn.hutool.core.collection.CollUtil;
import com.ratection.api.ResultCode;
import com.ratection.api.ServiceException;
import com.ratection.domain.SecurityUser;
import com.ratection.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理业务类
 */
@Service
public class UserServiceImpl implements UserDetailsService {

    private List<User> userList;

    @Value("${user.username}")
    private String username;
    @Value("${user.password}")
    private String password;
    @Value("${user.role}")
    private String role;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        password = passwordEncoder.encode(password);
        userList = new ArrayList<>();
        userList.add(new User(username, password, CollUtil.toList(role)));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<User> findUserList = userList.stream()
                .filter(item -> item.getUsername().equals(username))
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(findUserList)) {
            throw new ServiceException(ResultCode.USERNAME_PASSWORD_ERROR);
        }
        SecurityUser securityUser = new SecurityUser(findUserList.get(0));
        return securityUser;
    }
}
