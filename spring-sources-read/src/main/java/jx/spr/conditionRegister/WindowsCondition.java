package jx.spr.conditionRegister;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class WindowsCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        metadata.getAnnotations().forEach(System.out::println);

        System.out.println(context.getEnvironment().getProperty("os.name"));
        return true;
    }
}
