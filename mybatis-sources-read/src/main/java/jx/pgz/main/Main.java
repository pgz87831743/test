package jx.pgz.main;


import jx.pgz.main.bean.User;
import jx.pgz.main.mapper.UserMapper;
import lombok.Builder;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;

public class Main {

    public static void main(String[] args) throws IOException {


        way2();


    }

    private static void way2() {
        UnpooledDataSource dataSource = new UnpooledDataSource(
                "com.mysql.cj.jdbc.Driver",
                "jdbc:mysql://106.13.186.2:3306/test",
                "root",
                "twkj@2019"

        );
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(UserMapper.class);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);


        UserMapper mapper = sqlSessionFactory.openSession().getMapper(UserMapper.class);


        System.out.println(mapper.selectUser());


        System.out.println(User.class.getAnnotation(Builder.class));
    }

    private static void way1() throws IOException {
        String resource = "mybatis.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);


        UserMapper mapper = sqlSessionFactory.openSession().getMapper(UserMapper.class);


        System.out.println(mapper.selectUser());
    }


}
