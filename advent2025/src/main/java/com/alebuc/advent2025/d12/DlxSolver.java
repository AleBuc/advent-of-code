package com.alebuc.advent2025.d12;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DlxSolver {

    private record Placement(int offsetX, int offsetY, List<Integer> coveredCellIndexes) {
    }

    private record PlacementCacheKey(int boardWidth, int boardHeight, int shapeIndex, int orientationIndex) {
    }

    private final Map<PlacementCacheKey, List<Placement>> placementCache = new HashMap<>();

    public boolean isFittable(Region region, Map<Integer, Shape> shapesByIndex) {
        int boardWidth = region.getWidth();
        int boardHeight = region.getLength();
        int boardCellCount = boardWidth * boardHeight;

        //quick pruning: total area of pieces <= grid area
        int totalRequiredArea = 0;
        for (Map.Entry<Integer, Integer> entry : region.getShapeNumberByIndex().entrySet()) {
            int shapeIndex = entry.getKey();
            int requiredCount = entry.getValue();
            if (requiredCount <= 0) continue;
            Shape shape = shapesByIndex.get(shapeIndex);
            if (shape == null) return false;
            int pieceArea = shape.getOriginalShape().size();
            totalRequiredArea += requiredCount * pieceArea;
            if (totalRequiredArea > boardCellCount) return false;
        }

        DLX dlx = new DLX();

        //cell columns = secondary (no overlap)
        Map<Integer, DLX.Column> cellColumnByCellIndex = new HashMap<>(boardCellCount);
        for (int cellIndex = 0; cellIndex < boardCellCount; cellIndex++) {
            DLX.Column cellColumn = dlx.addColumn("Cell#" + cellIndex, false);
            cellColumnByCellIndex.put(cellIndex, cellColumn);
        }

        //piece columns = primary (all must be used)
        List<PieceInstance> pieceInstances = expandPieceInstances(region);
        Map<PieceInstance, DLX.Column> pieceColumnByInstance = new HashMap<>(pieceInstances.size());
        for (PieceInstance pieceInstance : pieceInstances) {
            DLX.Column pieceInstanceColumn = dlx.addColumn(
                    "PieceType#" + pieceInstance.shapeIndex() + "/Instance#" + pieceInstance.instanceNumber(),
                    true
            );
            pieceColumnByInstance.put(pieceInstance, pieceInstanceColumn);
        }

        //rows: (instance) + (covered cells)
        for (PieceInstance pieceInstance : pieceInstances) {
            Shape shape = shapesByIndex.get(pieceInstance.shapeIndex());
            if (shape == null) return false;
            List<Orientation> orientations = shape.getOrientations();
            if (orientations.isEmpty()) return false;
            DLX.Column pieceColumn = pieceColumnByInstance.get(pieceInstance);
            boolean hasAtLeastOnePlacement = false;

            for (int orientationIndex = 0; orientationIndex < orientations.size(); orientationIndex++) {
                List<Placement> placements = getPlacementsFromCache(
                        boardWidth,
                        boardHeight,
                        shape.getIndex(),
                        orientationIndex,
                        orientations.get(orientationIndex)
                );
                if (!placements.isEmpty()) hasAtLeastOnePlacement = true;
                for (Placement placement : placements) {
                    List<DLX.Column> columnsWithOne = new ArrayList<>(1 + placement.coveredCellIndexes.size());
                    columnsWithOne.add(pieceColumn);
                    for (Integer coveredCellIndex : placement.coveredCellIndexes) {
                        columnsWithOne.add(cellColumnByCellIndex.get(coveredCellIndex));
                    }
                    PlacementTag rowTag = new PlacementTag(
                            pieceInstance.shapeIndex(),
                            pieceInstance.instanceNumber(),
                            orientationIndex,
                            placement.offsetX,
                            placement.offsetY
                    );
                    dlx.addRow(rowTag, columnsWithOne);
                }
            }
            //if instance with no possible placement: impossible.
            if (!hasAtLeastOnePlacement) return false;
        }
        return !dlx.solveOne().isEmpty();
    }

    private List<PieceInstance> expandPieceInstances(Region region) {
        List<PieceInstance> instances = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : region.getShapeNumberByIndex().entrySet()) {
            int shapeIndex = entry.getKey();
            int requiredCount = entry.getValue();
            if (requiredCount <= 0) continue;
            for (int instanceNumber = 0; instanceNumber < requiredCount; instanceNumber++) {
                instances.add(new PieceInstance(shapeIndex, instanceNumber));
            }
        }
        return instances;
    }

    private List<Placement> getPlacementsFromCache(
            int boardWidth,
            int boardHeight,
            int shapeIndex,
            int orientationIndex,
            Orientation orientation
    ) {
        PlacementCacheKey cacheKey = new PlacementCacheKey(boardWidth, boardHeight, shapeIndex, orientationIndex);
        List<Placement> cached = placementCache.get(cacheKey);
        if (cached != null) return cached;
        List<Placement> computed = computePlacements(boardWidth, boardHeight, orientation);
        placementCache.put(cacheKey, computed);
        return computed;
    }

    private static List<Placement> computePlacements(int boardWidth, int boardHeight, Orientation orientation) {
        int orientedWidth = orientation.width();
        int orientedHeight = orientation.height();
        if (orientedWidth > boardWidth || orientedHeight > boardHeight) {
            return List.of();
        }
        List<Placement> placements = new ArrayList<>();
        for (int offsetY = 0; offsetY <= boardHeight - orientedHeight; offsetY++) {
            for (int offsetX = 0; offsetX <= boardWidth - orientedWidth; offsetX++) {
                List<Integer> coveredCellIndexes = new ArrayList<>(orientation.points().size());
                for (Point2D relativePoint : orientation.points()) {
                    int absoluteX = offsetX + relativePoint.x();
                    int absoluteY = offsetY + relativePoint.y();
                    int cellIndex = absoluteY * boardWidth + absoluteX;
                    coveredCellIndexes.add(cellIndex);
                }
                placements.add(new Placement(offsetX, offsetY, List.copyOf(coveredCellIndexes)));
            }
        }
        return List.copyOf(placements);
    }
}
