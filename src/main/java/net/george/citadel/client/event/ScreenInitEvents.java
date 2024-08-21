package net.george.citadel.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.george.citadel.api.event.CancelableEvent;
import net.george.citadel.api.event.marker.Cancelable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public abstract class ScreenInitEvents extends CancelableEvent {
    public static final Event<Pre.Context> PRE = EventFactory.createArrayBacked(Pre.Context.class,
            (listeners) -> event -> {
                for (Pre.Context callback : listeners) {
                    callback.interact(event);
                }
            });
    public static final Event<Post.Context> POST = EventFactory.createArrayBacked(Post.Context.class,
            (listeners) -> event -> {
                for (Post.Context callback : listeners) {
                    callback.interact(event);
                }
            });
    private final Screen screen;
    private final Consumer<Element> add;
    private final Consumer<Element> remove;
    private final List<Element> listenerList;

    @ApiStatus.Internal
    protected ScreenInitEvents(Screen screen, List<Element> listenerList, Consumer<Element> add, Consumer<Element> remove) {
        this.screen = screen;
        this.listenerList = Collections.unmodifiableList(listenerList);
        this.add = add;
        this.remove = remove;
    }

    public Screen getScreen() {
        return this.screen;
    }

    public List<Element> getListenersList() {
        return this.listenerList;
    }

    public void addListener(Element listener) {
        this.add.accept(listener);
    }

    public void removeListener(Element listener) {
        this.remove.accept(listener);
    }

    @Override
    public abstract void interact();

    public static class Post extends ScreenInitEvents {
        @ApiStatus.Internal
        public Post(Screen screen, List<Element> listenerList, Consumer<Element> add, Consumer<Element> remove) {
            super(screen, listenerList, add, remove);
        }

        @Override
        public void interact() {
            POST.invoker().interact(this);
        }

        @FunctionalInterface
        public interface Context {
            void interact(Post event);
        }
    }

    @Cancelable
    public static class Pre extends ScreenInitEvents {
        @ApiStatus.Internal
        public Pre(Screen screen, List<Element> listenerList, Consumer<Element> add, Consumer<Element> remove) {
            super(screen, listenerList, add, remove);
        }

        @Override
        public void interact() {
            PRE.invoker().interact(this);
        }

        @FunctionalInterface
        public interface Context {
            void interact(Pre event);
        }
    }
}
