package io.github.mortuusars.scholar.book;

public class Spread {
    public enum Side {
        LEFT,
        RIGHT;

        public int getIndex() {
            return ordinal();
        }

        public int getPageIndexFromSpread(int spreadIndex) {
            return spreadIndex * 2 + getIndex();
        }
    }
}
