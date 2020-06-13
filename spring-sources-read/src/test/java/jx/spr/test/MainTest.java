package jx.spr.test;

import jx.spr.Main;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

public class MainTest {


    private ApplicationContext app;

    @Before
    public void beforeTest() {
        app = new AnnotationConfigApplicationContext(Main.class);
        printlnArray(app.getBeanDefinitionNames());
        System.out.println("=================================================");
    }


    @Test
    public void apptest() {




    }


    public void printlnArray(String [] strings) {

        Arrays.asList(strings).forEach(System.out::println);



    }
}
