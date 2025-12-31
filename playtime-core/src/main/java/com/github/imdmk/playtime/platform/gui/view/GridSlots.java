package com.github.imdmk.playtime.platform.gui.view;

final class GridSlots {

    private static final int ROW_3_NEXT = 25;
    private static final int ROW_3_PREVIOUS = 19;
    private static final int ROW_3_EXIT = 22;

    private static final int ROW_4_NEXT = 34;
    private static final int ROW_4_PREVIOUS = 28;
    private static final int ROW_4_EXIT = 31;

    private static final int ROW_5_NEXT = 43;
    private static final int ROW_5_PREVIOUS = 37;
    private static final int ROW_5_EXIT = 40;

    private static final int ROW_6_NEXT = 52;
    private static final int ROW_6_PREVIOUS = 46;
    private static final int ROW_6_EXIT = 49;

    private GridSlots() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    static int next(int rows) {
        return switch (rows) {
            case 3 -> ROW_3_NEXT;
            case 4 -> ROW_4_NEXT;
            case 5 -> ROW_5_NEXT;
            case 6 -> ROW_6_NEXT;
            default -> throw new IllegalArgumentException("Unsupported rows for NEXT: " + rows);
        };
    }

    static int previous(int rows) {
        return switch (rows) {
            case 3 -> ROW_3_PREVIOUS;
            case 4 -> ROW_4_PREVIOUS;
            case 5 -> ROW_5_PREVIOUS;
            case 6 -> ROW_6_PREVIOUS;
            default -> throw new IllegalArgumentException("Unsupported rows for PREVIOUS: " + rows);
        };
    }

    static int exit(int rows) {
        return switch (rows) {
            case 3 -> ROW_3_EXIT;
            case 4 -> ROW_4_EXIT;
            case 5 -> ROW_5_EXIT;
            case 6 -> ROW_6_EXIT;
            default -> throw new IllegalArgumentException("Unsupported rows for EXIT: " + rows);
        };
    }
}
