package net.george.citadel.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public interface Lazy<T> extends Supplier<T> {
    static <T> Lazy<T> of(@NotNull Supplier<T> supplier) {
        return new Fast<>(supplier);
    }

    static <T> Lazy<T> concurrentOf(@NotNull Supplier<T> supplier) {
        return new Concurrent<>(supplier);
    }

    final class Fast<T> implements Lazy<T> {
        private Supplier<T> supplier;
        private T instance;

        private Fast(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        public @Nullable T get() {
            if (this.supplier != null) {
                this.instance = this.supplier.get();
                this.supplier = null;
            }

            return this.instance;
        }
    }

    final class Concurrent<T> implements Lazy<T> {
        private volatile Object lock = new Object();
        private volatile Supplier<T> supplier;
        private volatile T instance;

        private Concurrent(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        public @Nullable T get() {
            Object localLock = this.lock;
            if (this.supplier != null) {
                synchronized(localLock) {
                    if (this.supplier != null) {
                        this.instance = this.supplier.get();
                        this.supplier = null;
                        this.lock = null;
                    }
                }
            }

            return this.instance;
        }
    }
}
