package mchess;

import java.util.ArrayList;
import static mchess.ChessBoard.*;

public class Player {
    
    int color;
    int ky, kx;
    IAPieceList pieces;
    ArrayList<int[]> possibleMoves;

    public Player(int color) {
        kx = 4;
        this.color = color;
        ky = (this.color == WHITE) ? 7: 4;
    }
    
}
