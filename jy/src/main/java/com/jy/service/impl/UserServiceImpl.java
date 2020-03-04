package com.jy.service.impl;

import com.jy.dao.UserDao;
import com.jy.pojo.User;
import com.jy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public User save(User user) {
        userDao.save(user);
        return user;
    }

    @Override
    public User findByOpenid(String openid) {
        User user = userDao.findByOpenid(openid);
        if (user != null){
            return user;
        }
        return null;
    }
}
