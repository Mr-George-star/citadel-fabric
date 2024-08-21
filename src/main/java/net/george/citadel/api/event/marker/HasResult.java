package net.george.citadel.api.event.marker;

import net.george.citadel.api.event.ResultReturningEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An event annotation, events annotated with this annotation and extending {@link ResultReturningEvent base result returning event} can have a result after interaction.
 * @author Mr.George
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HasResult {
}
