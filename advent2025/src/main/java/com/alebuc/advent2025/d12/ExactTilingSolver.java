package com.alebuc.advent2025.d12;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Exact backtracking solver using a compact bitboard.
 * <p>
 * Builds piece types from the region requirements and tries to place them to fully cover the board.
 * The search anchors at the first free cell at each step and prunes via an exploration node budget.
 */
final class ExactTilingSolver {

    private final Map<Integer, Shape> shapesByIndex;
    private final long maxNodes;

    /**
     * Create a solver with a default node budget.
     *
     * @param shapesByIndex shapes available by index
     */
    ExactTilingSolver(Map<Integer, Shape> shapesByIndex) {
        this(shapesByIndex, 50_000_000L);
    }

    /**
     * Create a solver with a custom node budget.
     *
     * @param shapesByIndex shapes available by index
     * @param maxNodes      maximum explored nodes before stopping
     */
    ExactTilingSolver(Map<Integer, Shape> shapesByIndex, long maxNodes) {
        this.shapesByIndex = shapesByIndex;
        this.maxNodes = maxNodes;
    }

    /**
     * Determine if the region can be tiled exactly with the required shapes.
     *
     * @param region region and multiset description
     * @return true if a full tiling exists
     */
    boolean canTile(Region region) {
        int w = region.getWidth();
        int h = region.getLength();
        int cells = w * h;

        List<PieceType> types = new ArrayList<>();
        int remainingPieces = 0;

        // Build piece types from the region's counts
        for (Map.Entry<Integer, Integer> e : region.getShapeNumberByIndex().entrySet()) {
            int shapeIndex = e.getKey();
            int count = e.getValue();
            if (count <= 0) continue;

            Shape shape = shapesByIndex.get(shapeIndex);
            if (shape == null) {
                return false;
            }

            List<Orientation> orientations = shape.getOrientations();
            if (orientations == null || orientations.isEmpty()) {
                return false;
            }

            int area = orientations.getFirst().points().size();
            types.add(new PieceType(shapeIndex, count, orientations, area));
            remainingPieces += count;
        }

        // Heuristic order: fewer orientations first; tie-break by larger area
        types.sort((a, b) -> {
            int cmp = Integer.compare(a.orientations.size(), b.orientations.size());
            if (cmp != 0) return cmp;
            return Integer.compare(b.area, a.area);
        });

        // Area pruning: reject if required area exceeds board area
        long required = 0;
        for (PieceType t : types) {
            required += (long) t.count * t.area;
            if (required > cells) return false;
        }

        BitBoard board = new BitBoard(cells);
        SearchState st = new SearchState(types, w, h, board, remainingPieces);

        boolean result = dfs(st);

        if (w <= 12 && h <= 5) {
            System.out.println("[EXACT] Region " + w + "x" + h + " -> " + (result ? "OK" : "FAIL")
                    + " (nodes=" + st.nodes + ")");
        }

        return result;
    }

    /** Depth-first search that tries to place remaining pieces. */
    private boolean dfs(SearchState st) {
        if (st.nodes++ > maxNodes) {
            return false;
        }

        if (st.remainingPieces == 0) {
            return true;
        }

        // Choose the first empty cell as anchor
        int target = st.firstEmptyCell();
        if (target < 0) {
            return false;
        }

        int tx = target % st.w;
        int ty = target / st.w;

        // Try each piece type and each orientation covering (tx, ty)
        for (PieceType type : st.types) {
            if (type.count == 0) continue;

            for (Orientation o : type.orientations) {
                List<Point2D> pts = o.points();

                for (Point2D anchor : pts) {
                    int ox = tx - anchor.x();
                    int oy = ty - anchor.y();

                    if (!fitsInBounds(o, ox, oy, st.w, st.h)) continue; // outside board
                    if (!canPlace(pts, ox, oy, st)) continue;           // overlaps

                    place(pts, ox, oy, st, true); // place
                    type.count--;
                    st.remainingPieces--;

                    if (dfs(st)) return true;

                    st.remainingPieces++;          // undo
                    type.count++;
                    place(pts, ox, oy, st, false);
                }
            }
        }

        // Optional pruning: force-fill the target cell and continue
        long remainingArea = 0;
        for (PieceType t : st.types) {
            if (t.count > 0) {
                remainingArea += (long) t.count * t.area;
            }
        }
        int freeCells = st.countFreeCells();
        if (remainingArea <= freeCells - 1) {
            st.board.set(target, true);
            if (dfs(st)) return true;
            st.board.set(target, false);
        }

        return false;
    }

    /** Quick bounding check for an oriented piece at offset (ox, oy). */
    private static boolean fitsInBounds(Orientation o, int ox, int oy, int w, int h) {
        return ox >= 0 && oy >= 0 && (ox + o.width()) <= w && (oy + o.height()) <= h;
    }

    /** Check that all cells for the piece are free on the board. */
    private static boolean canPlace(List<Point2D> pts, int ox, int oy, SearchState st) {
        for (Point2D p : pts) {
            int x = ox + p.x();
            int y = oy + p.y();
            int idx = y * st.w + x;
            if (st.board.get(idx)) return false;
        }
        return true;
    }

    /** Set or clear the cells for the piece on the board. */
    private static void place(List<Point2D> pts, int ox, int oy, SearchState st, boolean value) {
        for (Point2D p : pts) {
            int x = ox + p.x();
            int y = oy + p.y();
            int idx = y * st.w + x;
            st.board.set(idx, value);
        }
    }

    /** Piece type with remaining count and unique orientations. */
    private static final class PieceType {
        final int shapeIndex;
        int count;
        final int area;
        final List<Orientation> orientations;

        PieceType(int shapeIndex, int count, List<Orientation> orientations, int area) {
            this.shapeIndex = shapeIndex;
            this.count = count;
            this.area = area;
            this.orientations = orientations;
        }
    }

    /** Mutable search state: board, piece types, region size, counters. */
    private static final class SearchState {
        final List<PieceType> types;
        final int w;
        final int h;
        final BitBoard board;
        long nodes = 0;
        int remainingPieces;

        SearchState(List<PieceType> types, int w, int h, BitBoard board, int remainingPieces) {
            this.types = types;
            this.w = w;
            this.h = h;
            this.board = board;
            this.remainingPieces = remainingPieces;
        }

        int firstEmptyCell() {
            int cells = w * h;
            for (int i = 0; i < cells; i++) {
                if (!board.get(i)) return i;
            }
            return -1;
        }

        int countFreeCells() {
            int cells = w * h;
            int free = 0;
            for (int i = 0; i < cells; i++) {
                if (!board.get(i)) free++;
            }
            return free;
        }
    }

    /** Compact bitboard over the region cells. */
    private static final class BitBoard {
        private final long[] words;

        BitBoard(int cells) {
            this.words = new long[(cells + 63) >>> 6];
        }

        boolean get(int idx) {
            int w = idx >>> 6;
            int b = idx & 63;
            return ((words[w] >>> b) & 1L) != 0L;
        }

        void set(int idx, boolean value) {
            int w = idx >>> 6;
            int b = idx & 63;
            long mask = 1L << b;
            if (value) {
                words[w] |= mask;
            } else {
                words[w] &= ~mask;
            }
        }
    }
}
