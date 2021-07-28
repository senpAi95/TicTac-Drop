package com.tictac.droptoken;

import javax.inject.Inject;

/**
 * Encapsulates Operations which can be performed on a game.
 */
public class GridOperations {

    /**
     * Gets the possible row where a coin can be dropped in the column of a given grid.
     *
     * @param grid
     * @param column
     * @return
     */
    public static int getPossibleRow(String[][] grid, int column) {
        // if top entrance is empty, then its valid..
        //if input column is 1, if we can peek (0,1) - top one from grid then we will know that its valid.
        for(int i = grid.length-1; i>=0; i--) {
            if(grid[i][column-1].equals("0"))
                return i;
        }
        return -1;
    }

    /**
     * Determines if the move posted can lead to a Winning state of the game deeming current player as winner.
     * @param grid
     * @param playerId
     * @param row
     * @param column
     * @return
     */
    public static boolean winningMove(String[][] grid, String playerId, int row, int column) {
        int len = grid.length;
        boolean win;
        // decrement 1 considering 0 index in array
        column--;
        // Check row.
        win = checkRow(grid, playerId, row);

        // check column.
        if(!win){
            win = checkColumn(grid, playerId, column);
        }

        // check forward diagonal if row == column
        if(!win && row == column) {
            win = checkForwardDiagonal(grid, playerId);

        }

        // check reverse diagonal if row + column = len +1.
        if(!win && (row + column) == (len +1)) {
            win = checkReverseDiagonal(grid, playerId);
        }

        return win;
    }

    private static boolean checkRow(String[][] grid, String playerId, int row) {
        int len = grid.length;
        for(int i =0 ; i<len; i++) {
            if(!grid[row][i].equals(playerId))
            {
                return false;
            }
        }
        return true;
    }

    private static boolean checkColumn(String[][] grid, String playerId, int column) {
        int len = grid.length;
        for(int i=0; i<len; i++) {
            if(!grid[i][column].equals(playerId)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkForwardDiagonal(String[][] grid, String playerId) {
        int len = grid.length;
        for(int i=0; i<len ; i++) {
            if(!grid[i][i].equals(playerId)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkReverseDiagonal(String[][] grid, String playerId) {
        int len = grid.length;
        for(int i =0; i<len;i++) {
            if(!grid[i][len-i-1].equals(playerId)) {
                return false;
            }
        }
        return true;
    }
}
