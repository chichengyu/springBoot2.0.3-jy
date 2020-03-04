package com.jy.provider;

import com.jy.pojo.User;
import org.apache.ibatis.jdbc.SQL;

/** new SQL(){{}}  外部括号是创建一个匿名内部类，内部花括号是一个构造代码块，在实例化一个对象时，会先于构造方式执行，编译时会将构造代码块移入构造方法中
 * 用户动态 sql 语句构建
 */
public class UserProvider {

    /**
     * user 添加用户动态 sql 语句构建
     * @param user
     * @return
     */
    public String insert(User user){
        return new SQL(){
            {
                INSERT_INTO("jy_user");
                if (user.getName() != null){
                    VALUES("name","#{name}");
                }
                if (user.getOpenid() != null){
                    VALUES("openid","#{openid}");
                }
                if (user.getHeadImg() != null){
                    VALUES("head_img","#{headImg}");
                }
                if (user.getPhone() != null){
                    VALUES("phone","#{phone}");
                }
                if (user.getSex() != null){
                    VALUES("sex","#{sex}");
                }
                if (user.getSign() != null){
                    VALUES("sign","#{sign}");
                }
                if (user.getCity() != null){
                    VALUES("city","#{city}");
                }
                if (user.getCreateTime() != null){
                    VALUES("create_time","#{createTime}");
                }
            }
        }.toString();
    }
}
