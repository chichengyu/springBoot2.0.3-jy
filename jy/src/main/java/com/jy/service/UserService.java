package com.jy.service;

import com.jy.pojo.User;

public interface UserService {

    User save(User user);

    User findByOpenid(String openid);
}
