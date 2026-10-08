package aztech.milite.test.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MIGameTest {
    int timeoutTicks() default 100;

    String batch() default "defaultBatch";

    boolean skyAccess() default false;

    int rotationSteps() default 0;

    boolean required() default true;

    boolean manualOnly() default false;

    long setupTicks() default 0L;

    int attempts() default 1;

    int requiredSuccesses() default 1;
}
