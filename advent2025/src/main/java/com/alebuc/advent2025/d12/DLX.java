package com.alebuc.advent2025.d12;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://fr.wikipedia.org/wiki/Algorithme_X_de_Knuth">Algorithme X de Knuth</a>
 */
@Getter
public final class DLX {

    static class Node {
        Node left = this, right = this, up = this, down = this;
        Column column;

        //optional: to reconstruct an understandable solution
        Object rowTag;
    }

    static class Column extends Node {
        int activeNodeCount = 0;   //number of nodes in the column
        String name;               //debug
        boolean primary;           //if true: mandatory constraint

        Column(String name, boolean primary) {
            this.name = name;
            this.primary = primary;
            this.column = this;
        }
    }

    private final Column header = new Column("HEADER", true);
    private final List<Node> partialSolution = new ArrayList<>();

    public Column addColumn(String name, boolean primary) {
        Column newColumn = new Column(name, primary);

        //only primary columns are linked in header horizontal list.
        if (primary) {
            newColumn.right = header;
            newColumn.left = header.left;
            header.left.right = newColumn;
            header.left = newColumn;
        } else {
            //secondary columns are not part of header list.
            newColumn.left = newColumn.right = newColumn;
        }

        return newColumn;
    }

    public void addRow(Object rowTag, List<Column> columnsWithOne) {
        Node firstNodeInRow = null;

        for (Column constraintColumn : columnsWithOne) {
            Node newDataNode = new Node();
            newDataNode.column = constraintColumn;
            newDataNode.rowTag = rowTag;

            //vertical insertion at bottom of column
            newDataNode.down = constraintColumn;
            newDataNode.up = constraintColumn.up;
            constraintColumn.up.down = newDataNode;
            constraintColumn.up = newDataNode;
            constraintColumn.activeNodeCount++;

            //horizontal insertion in row
            if (firstNodeInRow == null) {
                firstNodeInRow = newDataNode;
                newDataNode.left = newDataNode.right = newDataNode;
            } else {
                newDataNode.right = firstNodeInRow;
                newDataNode.left = firstNodeInRow.left;
                firstNodeInRow.left.right = newDataNode;
                firstNodeInRow.left = newDataNode;
            }
        }
    }

    private void cover(Column columnToCover) {
        if (columnToCover.primary) {
            columnToCover.right.left = columnToCover.left;
            columnToCover.left.right = columnToCover.right;
        }

        for (Node rowNode = columnToCover.down; rowNode != columnToCover; rowNode = rowNode.down) {
            for (Node nodeInRow = rowNode.right; nodeInRow != rowNode; nodeInRow = nodeInRow.right) {
                nodeInRow.down.up = nodeInRow.up;
                nodeInRow.up.down = nodeInRow.down;
                nodeInRow.column.activeNodeCount--;
            }
        }
    }

    private void uncover(Column columnToUncover) {
        for (Node rowNode = columnToUncover.up; rowNode != columnToUncover; rowNode = rowNode.up) {
            for (Node nodeInRow = rowNode.left; nodeInRow != rowNode; nodeInRow = nodeInRow.left) {
                nodeInRow.column.activeNodeCount++;
                nodeInRow.down.up = nodeInRow;
                nodeInRow.up.down = nodeInRow;
            }
        }

        if (columnToUncover.primary) {
            columnToUncover.right.left = columnToUncover;
            columnToUncover.left.right = columnToUncover;
        }
    }

    private Column choosePrimaryColumnWithMinimumOptions() {
        Column bestColumn = null;
        int smallestOptionCount = Integer.MAX_VALUE;

        for (Node node = header.right; node != header; node = node.right) {
            Column candidateColumn = (Column) node;
            if (candidateColumn.activeNodeCount < smallestOptionCount) {
                smallestOptionCount = candidateColumn.activeNodeCount;
                bestColumn = candidateColumn;
                if (smallestOptionCount == 0) break;
            }
        }

        return bestColumn;
    }

    public List<Object> solveOne() {
        partialSolution.clear();
        boolean solved = search();
        if (!solved) return List.of();

        List<Object> rowTags = new ArrayList<>(partialSolution.size());
        for (Node chosenRowNode : partialSolution) {
            rowTags.add(chosenRowNode.rowTag);
        }
        return List.copyOf(rowTags);
    }

    private boolean search() {
        //success: no primary constraints left
        if (header.right == header) return true;

        Column chosenPrimaryColumn = choosePrimaryColumnWithMinimumOptions();
        if (chosenPrimaryColumn == null || chosenPrimaryColumn.activeNodeCount == 0) return false;

        cover(chosenPrimaryColumn);

        for (Node rowNode = chosenPrimaryColumn.down; rowNode != chosenPrimaryColumn; rowNode = rowNode.down) {
            partialSolution.add(rowNode);

            for (Node nodeInRow = rowNode.right; nodeInRow != rowNode; nodeInRow = nodeInRow.right) {
                cover(nodeInRow.column);
            }

            if (search()) return true;

            for (Node nodeInRow = rowNode.left; nodeInRow != rowNode; nodeInRow = nodeInRow.left) {
                uncover(nodeInRow.column);
            }

            partialSolution.removeLast();
        }

        uncover(chosenPrimaryColumn);
        return false;
    }
}
