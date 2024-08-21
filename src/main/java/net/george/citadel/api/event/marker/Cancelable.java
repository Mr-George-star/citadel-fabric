package net.george.citadel.api.event.marker;

import net.george.citadel.api.event.CancelableEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An event annotation, events annotated with this annotation and extending {@link CancelableEvent base cancelable event} can be canceled after interaction.
   @author Mr.George
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Cancelable {
}
