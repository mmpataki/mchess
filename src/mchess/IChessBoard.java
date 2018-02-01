package mchess;

public interface IChessBoard {
    
    /**
     * The y and x variales passed here are according to the standard
     * chess-board convention.
     * 
     * @param type : type of selection
     * @param y : vertical unit of board.
     * @param x : horizontal unit of board.
     * @returns : the code HIGHLIGHT | NORMAL | POSSIBLEMOVE for the cell selected
     */
    public int cellSelected(int type, int y, int x);
    
}
