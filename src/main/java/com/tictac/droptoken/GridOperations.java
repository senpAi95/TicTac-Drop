package com.tictac.droptoken;

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
    // row where the coin got placed.
    public static boolean winningMove(String[][] grid, String playerId, int row, int column) {
        int len = grid.length;
        boolean win = true;
        // decrement 1 considering 0 index in array
        column--;
        // Check row.
        for(int i =0 ; i<len; i++) {
            if(!grid[row][i].equals(playerId))
            {
                win = false;
                break;
            }
        }

        // check column.
        if(!win){
            win = true;
            for(int i=0; i<len; i++) {
                if(!grid[i][column].equals(playerId)) {
                    win = false;
                    break;
                }
            }
        }

        // check forward diagonal if row == column
        if(!win && row == column) {
            win = true;
            for(int i=0; i<len ; i++) {
                if(!grid[i][i].equals(playerId)) {
                    win = false;
                    break;
                }
            }
        }

        // check reverse diagonal if row + column = len +1.
        if(!win && (row + column) == (len +1)) {
            for(int i =0; i<len;i++) {
                if(!grid[i][len-i-1].equals(playerId)) {
                    win = false;
                    break;
                }
            }
        }

        return win;
    }
}
