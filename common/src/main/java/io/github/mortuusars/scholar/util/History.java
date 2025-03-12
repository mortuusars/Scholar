package io.github.mortuusars.scholar.util;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;

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

    public void add(Change change) {
        if (undoStack.size() > maxChanges) {
            undoStack.removeFirst();
        }

        undoStack.addLast(change);
        redoStack.clear();
    }

    public void add(Runnable apply, Runnable undo) {
        add(Change.create(apply, undo));
    }

    public @Nullable Change undo() {
        if (!undoStack.isEmpty()) {
            Change lastEdit = undoStack.removeLast();

            if (!lastEdit.undo()) {
                // Removing all changes if undo fails.
                // It's probably not a good idea to try more undos or redos if changes were not undone correctly.
                undoStack.clear();
                redoStack.clear();
            } else {
                redoStack.addLast(lastEdit);
            }

            return lastEdit;
        }
        return null;
    }

    public @Nullable Change redo() {
        if (!redoStack.isEmpty()) {
            Change change = redoStack.removeLast();
            change.apply();
            undoStack.addLast(change);
            return change;
        }
        return null;
    }
}