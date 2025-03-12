package io.github.mortuusars.scholar.util;

import java.util.function.Supplier;

public interface Change {
    void apply();
    boolean undo();

    static Change create(Runnable apply, Supplier<Boolean> undo) {
        return new Change() {
            @Override
            public void apply() {
                apply.run();
            }

            @Override
            public boolean undo() {
                return undo.get();
            }
        };
    }

    static Change create(Runnable apply, Runnable undo) {
        return create(apply, () -> {
            undo.run();
            return true;
        });
    }
}