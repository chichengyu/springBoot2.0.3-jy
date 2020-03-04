package com.jy.dao;

import com.jy.pojo.User;
import com.jy.provider.UserProvider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

public interface UserDao {


    //@Insert("insert into jy_user values(null,#{openid},#{name},#{headImg},#{phone},#{sign},#{sex},#{city},#{createTime})")
    @InsertProvider(type = UserProvider.class,method = "insert")
    @Options(useGeneratedKeys = true,keyColumn = "id",keyProperty = "id")
    int save(User user);


    @Select("select * from user where id=#{id}")
    User findById(Integer id);

    @Select("select * from user where openid=#{openid}")
    User findByOpenid(String openid);
}
