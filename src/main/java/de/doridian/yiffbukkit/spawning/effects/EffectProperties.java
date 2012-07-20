package de.doridian.yiffbukkit.spawning.effects;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EffectProperties {
	String name();
	int potionColor() default 0;
	double radius() default 3;
	boolean persistent() default true;
	boolean potionTrail() default false;
}
