package mchess;

import java.util.ArrayList;
import java.util.Arrays;
import static mchess.ChessBoard.*;

public class Mchess extends javax.swing.JFrame implements IChessBoard {

    public Mchess() {
        initComponents();

        cb.setBoardListener(this);

        board = new int[8][8];
        ChessBoard.copyBoard(board, INITBOARD);

        pieces = new IAPieceList[2];
        pieces[0] = new IAPieceList(64);
        pieces[1] = new IAPieceList(64);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != NN) {
                    if (getColor(board[i][j]) == WHITE) {
                        pieces[WHITE].add(i * 8 + j);
                    } else {
                        pieces[BLACK].add(i * 8 + j);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        background = new javax.swing.JPanel();
        cb = new mchess.ChessBoard();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("mChess");
        setAlwaysOnTop(true);
        setBackground(java.awt.Color.black);
        setMaximumSize(new java.awt.Dimension(590, 400));
        setMinimumSize(new java.awt.Dimension(590, 400));
        setPreferredSize(new java.awt.Dimension(590, 400));
        setResizable(false);
        setSize(new java.awt.Dimension(630, 400));

        background.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout cbLayout = new javax.swing.GroupLayout(cb);
        cb.setLayout(cbLayout);
        cbLayout.setHorizontalGroup(
            cbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 566, Short.MAX_VALUE)
        );
        cbLayout.setVerticalGroup(
            cbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 376, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout backgroundLayout = new javax.swing.GroupLayout(background);
        background.setLayout(backgroundLayout);
        backgroundLayout.setHorizontalGroup(
            backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        backgroundLayout.setVerticalGroup(
            backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(background, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(background, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Mchess.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            new Mchess().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel background;
    private mchess.ChessBoard cb;
    // End of variables declaration//GEN-END:variables

    Player currentPlayer, players[];
    
    private int MAXEND = 1600;
    final int COMPUTER_COLOR = BLACK;
    final int MCHESS_MAX_DEPTH = 3;
    int[][] board;
    int curCol = WHITE;
    int selY = -1, selX = -1;
    ArrayList<int[]> possibleMoves;
    IAPieceList[] pieces;

    
    /**
     * Solves the current chess board problem.
     * @param board : current state of the board.
     * @param color : current player
     * @param depth : depth of the search
     * @return : best move.
     */
    int solvex(int board[][], int color, int depth) {

        //printBoard(board);
        
        if (depth == 0) {
            return eval(board, color) - depth;
        }

        boolean MAX = (color == COMPUTER_COLOR);
        ArrayList<int[]> moves;
        IAPieceList pList = pieces[color];
        int tmp, solution = 0, sy, sx, dx, dy, ex, state;
        int m = MAX ? Integer.MAX_VALUE : Integer.MIN_VALUE;

        for (LNode node = pList.getFirst(); node != null; node = node.next()) {

            int index = node.getIndex();
            int[] piece = new int[]{index / 8, index % 8};

            if ((moves = getMoves(board, piece[0], piece[1])) == null) {
                continue;
            }

            sy = piece[0];
            sx = piece[1];
            
            for (int[] move : moves) {

                /* move the piece */
                dy = move[0];
                dx = move[1];
                ex = board[dy][dx];
                board[dy][dx] = board[sy][sx];
                board[sy][sx] = NN;

                state = eval(board, color);
                if(state == MAXEND)
                    return state;
                
                tmp = solvex(board, oppColor(color), depth - 1);
                
                if (MAX) {
                    /* minimize opponents score */
                    if (tmp < m) {
                        m = tmp;
                        if(depth == MCHESS_MAX_DEPTH)
                            solution = encodemove(piece, move);
                    }
                } else {
                    /* maximise opponent's score */
                    if(tmp > m) {
                        m = tmp;
                    }
                }

                /* undo the move. */
                board[sy][sx] = board[dy][dx];
                board[dy][dx] = ex;
            }
        }
        return (depth == MCHESS_MAX_DEPTH) ? solution : m;
    }

    private int encodemove(int[] src, int[] dest) {
        return ((src[0] * 8 + src[1]) << 8) | (dest[0] * 8 + dest[1]);
    }
    
    private void movePiece(int[][] board, int[] src, int[] dest) {
        
        int box = board[dest[0]][dest[1]];
        boolean killed = box != NN;
        IAPieceList plist = pieces[getColor(board[src[0]][src[1]])];
        IAPieceList oplist = pieces[getColor(box)];

        board[dest[0]][dest[1]] = board[src[0]][src[1]];
        board[src[0]][src[1]] = NN;

        /* maintain piece list */
        if (killed) {
            oplist.delete(dest[0] * 8 + dest[1]);
            cb.notifyCapture(box);
        }
        plist.move((src[0] << 3 | src[1]), (dest[0] << 3 | dest[1]));
        cb.notifyMove(src[1], src[0], dest[1], dest[0]);
    }

    int eval(int board[][], int color) {
        int[] scores = new int[2];
        int weights[] = {
            0, 
            4, 3, 3, 5, 7, 1,
            0, 0,
            4, 3, 3, 5, 7, 1
        };
        
        for (int i = 0; i < 2; i++) {
            for(LNode n = pieces[i].getFirst(); n != null; n = n.next()) {
                scores[i] += weights[board[n.getIndex() / 8][n.getIndex() % 8]];
            }
        }
        
        return scores[color] - scores[oppColor(color)];
    }

    @Override
    public int cellSelected(int type, int y, int x) {

        boolean valid = false;

        switch (type) {
            case ChessBoard.SELECT:
                if (getColor(board[y][x]) != WHITE) {
                    return NORMAL;
                }
                selY = y;
                selX = x;
                possibleMoves = getMoves(board, y, x);
                cb.showPossibleMoves(possibleMoves);
                return HIGHLIGHT;

            case ChessBoard.MOVE:
                if (!valid(selX, selY)
                        || board[selY][selX] == NN
                        || curCol != getColor(board[selY][selX])) {
                    return NORMAL;
                }
                for (int[] pmove : possibleMoves) {
                    if (pmove[0] == y && pmove[1] == x) {
                        valid = true;
                        break;
                    }
                }
                if (!valid) {
                    return NORMAL;
                }
                movePiece(board, new int[]{selY, selX}, new int[]{y, x});
                printBoard(board);
                cb.draw(board);

                int[][] cmove = decodeMove(solvex(board, BLACK, MCHESS_MAX_DEPTH));
                movePiece(board, cmove[0], cmove[1]);
                printBoard(board);
                cb.draw(board);
        }
        return NORMAL;
    }

    private int[][] decodeMove(int move) {
        int src = move >> 8;
        int dest = move & 0xff;
        return new int[][]{
            {src / 8, src % 8},
            {dest / 8, dest % 8}
        };
    }

    private boolean isKing(int piece) {
        return (piece == WK || piece == BK);
    }

    private boolean valid(int y, int x) {
        return (x > -1 && x < 8 && y > -1 && y < 8);
    }

    private boolean empty(int cell) {
        return (cell == NN || cell == DM);
    }

    private ArrayList<int[]> getMoves(int[][] board, int y, int x) {
        switch (board[y][x]) {
            case WP:
            case BP:
                return pmfPawn(board, y, x);
            case WQ:
            case BQ:
                return pmfQueen(board, y, x);
            case WK:
            case BK:
                return pmfKing(board, y, x);
            case WB:
            case BB:
                return pmfBishop(board, y, x);
            case WH:
            case BH:
                return pmfKnight(board, y, x);
            case WR:
            case BR:
                return pmfRook(board, y, x);
        }
        return null;
    }

    /* pmf : possible moves for. */
    private ArrayList<int[]> pmfRook(int[][] board, int y, int x) {
        return gpmf(board, y, x, new int[][]{
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}
        }, true);
    }

    private ArrayList<int[]> pmfQueen(int[][] board, int y, int x) {
        ArrayList<int[]> moves = pmfBishop(board, y, x);
        moves.addAll(pmfRook(board, y, x));
        return moves;
    }

    private ArrayList<int[]> pmfBishop(int[][] board, int y, int x) {
        return gpmf(board, y, x, new int[][]{
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        }, true);
    }

    private ArrayList<int[]> pmfKnight(int[][] board, int y, int x) {
        return gpmf(board, y, x, new int[][]{
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
        }, false);
    }

    /**
     * Evaluates the moves based on the offset matrix given.
     *
     * @param board : The board on which moves should be evaluated
     * @param y : y position of the piece on the board.
     * @param x : x position of the piece on the board.
     * @param offs : the offset matrix related to the piece/
     * @param deep : boolean telling whether we should evaluate deeply
     * @return : list of the moves corresponding to the piece at (x, y)
     */
    private ArrayList<int[]> gpmf(int[][] board, int y, int x, int[][] offs, boolean deep) {

        int i, j, color = getColor(board[y][x]);
        ArrayList<int[]> moves = new ArrayList<>();

        for (int k = 0; k < offs.length; k++) {

            i = y + offs[k][0];
            j = x + offs[k][1];

            if (deep) {
                while (valid(i, j) && empty(board[i][j])) {
                    moves.add(new int[]{i, j});
                    i += offs[k][0];
                    j += offs[k][1];
                }
                if (valid(i, j) && color != getColor(board[i][j])) {
                    moves.add(new int[]{i, j});
                }
            } else if (valid(i, j) && (empty(board[i][j]) || (color != getColor(board[i][j])))) {
                moves.add(new int[]{i, j});
            }

        }
        return moves;
    }

    /**
     * TODO: en-passant move to implemented.
     *
     * @param board
     * @param y
     * @param x
     * @return
     */
    private ArrayList<int[]> pmfPawn(int[][] board, int y, int x) {

        int color = getColor(board[y][x]), i, j,
                offs[][][] = new int[][][]{
                    {{2, 0, 0}, {1, 0, 0}, {1, -1, 1}, {1, 1, 1}}, //black (i, j, require)
                    {{-2, 0, 0}, {-1, 0, 0}, {-1, -1, 1}, {-1, 1, 1}} //white (i, j, require)
                };
        ArrayList<int[]> moves = new ArrayList<>();
        boolean orgpos = (color == WHITE) ? (y == 6) : (y == 1);

        for (int k = 1; k < offs[color].length; k++) {

            i = y + offs[color][k][0];
            j = x + offs[color][k][1];

            if (valid(i, j)) {
                if (offs[color][k][2] == 1) {
                    if (!empty(board[i][j]) && color != getColor(board[i][j])) {
                        moves.add(new int[]{i, j});
                    }
                } else if (empty(board[i][j])) {
                    moves.add(new int[]{i, j});
                }
            }
        }

        i = y + offs[color][0][0];
        j = x + offs[color][0][1];
        if (orgpos && board[i][j] == NN && board[i][j] != DM
                && empty(board[y + offs[color][1][0]][x + offs[color][1][1]])) {
            moves.add(new int[]{i, j});
        }

        return moves;
    }

    /* The toughest of all the moves as we need to evaluate checks. */
    private ArrayList<int[]> pmfKing(int[][] board, int y, int x) {

        int i, j, k, color;
        int[][] b = new int[8][8];
        int offs[][] = new int[][]{
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1}, {0, 1},
            {1, -1}, {1, 0}, {1, 1}
        };
        ArrayList<int[]> omoves, tmoves, moves = new ArrayList<>();
        IAPieceList oppPieces;

        copyBoard(b, board);

        for (k = 0; k < 8; k++) {
            i = y + offs[k][0];
            j = x + offs[k][1];
            if (valid(i, j)) {
                b[i][j] = NN;
            }
        }

        color = getColor(board[y][x]);
        oppPieces = pieces[oppColor(color)];
        omoves = new ArrayList<>();

        for (LNode n = oppPieces.getFirst(); n != null; n = n.next()) {

            int index = n.getIndex();
            int[] opppos = new int[]{index / 8, index % 8};

            if (!isKing(b[opppos[0]][opppos[1]])) {
                tmoves = getMoves(b, opppos[0], opppos[1]);
                if (tmoves != null) {
                    omoves.addAll(tmoves);
                }
            } else {
                for (int l = 0; l < 8; l++) {
                    int p = opppos[0] + offs[l][0];
                    int q = opppos[1] + offs[l][1];
                    if (valid(p, q) && empty(b[p][q])) {
                        omoves.add(new int[]{p, q});
                    }
                }
            }
        }
        for (int[] move : omoves) {
            b[move[0]][move[1]] = AC;
        }

        for (k = 0; k < 8; k++) {
            i = y + offs[k][0];
            j = x + offs[k][1];
            if (valid(i, j)) {
                if (empty(b[i][j]) && (color != getColor(board[i][j]) || empty(board[i][j]))) {
                    moves.add(new int[]{i, j});
                }
            }
        }
        return moves;
    }

    private void printf(String format, Object... args) {
        System.out.printf(format, args);
    }

    private int oppColor(int color) {
        return color == WHITE ? BLACK : WHITE;
    }
}
