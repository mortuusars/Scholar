package io.github.mortuusars.scholar.client.screen;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

public class History {
    protected final Deque<Change> undoStack = new ArrayDeque<>(50);
    protected final Deque<Change> redoStack = new ArrayDeque<>(50);
    protected final int maxChanges;

    public History(int maxChanges) {
        this.maxChanges = maxChanges;
    }

    public History() {
        this(50);
    }

    public void addChange(Change change) {
        if (undoStack.size() > maxChanges) {
            undoStack.removeFirst();
        }

        undoStack.addLast(change);
        redoStack.clear();
    }

    public void addChange(Supplier<Boolean> undo, Supplier<Boolean> redo) {
        addChange(new Change() {
            @Override
            public boolean undo() {
                return undo.get();
            }

            @Override
            public boolean redo() {
                return redo.get();
            }
        });
    }

    public void addChange(Runnable undo, Runnable redo) {
        addChange(new Change() {
            @Override
            public boolean undo() {
                undo.run();
                return true;
            }

            @Override
            public boolean redo() {
                redo.run();
                return true;
            }
        });
    }

    public @Nullable Change undo() {
        if (!undoStack.isEmpty()) {
            Change lastEdit = undoStack.removeLast();
            lastEdit.undo();
            redoStack.addLast(lastEdit);
            return lastEdit;
        }
        return null;
    }

    public @Nullable Change redo() {
        if (!redoStack.isEmpty()) {
            Change lastUndo = redoStack.removeLast();
            lastUndo.redo();
            undoStack.addLast(lastUndo);
            return lastUndo;
        }
        return null;
    }

    public interface Change {
        boolean undo();
        boolean redo();
    }
}
