package jx.pgz.main.mapper;

import jx.pgz.main.bean.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper {


    @Select(" select *\n" +
            "        from test.s_user;")
    List<User> selectUser();
}
