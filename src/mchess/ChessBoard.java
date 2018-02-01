package mchess;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *
 * @author mmp
 */
public class ChessBoard extends JPanel {

    public final static int NN = 0,
            BR = 1, BH = 2, BB = 3, BQ = 4, BK = 5, BP = 6,
            WR = 9, WH = 10, WB = 11, WQ = 12, WK = 13, WP = 14,
            AC = 15, DM = 16;   //DM = Dummy
    
    public static final int SELECT = 1;
    public static final int MOVE = 2;
    public static final int WHITE = 1;
    public static final int BLACK = 0;
    public static final int POSSIBLEMOVE = 1;
    public static final int HIGHLIGHT = 2;
    public static final int NORMAL = 3;

    /**/
    public static final int[][] INITBOARD = {
        {BR, BH, BB, BQ, BK, BB, BH, BR},
        {BP, BP, BP, BP, BP, BP, BP, BP},
        {NN, NN, NN, NN, NN, NN, NN, NN},
        {NN, NN, NN, NN, NN, NN, NN, NN},
        {NN, NN, NN, NN, NN, NN, NN, NN},
        {NN, NN, NN, NN, NN, NN, NN, NN},
        {WP, WP, WP, WP, WP, WP, WP, WP},
        {WR, WH, WB, WQ, WK, WB, WH, WR}
    };
    
    private final static String[] TAB = {
        " ",
        "" + ((char) 9820), "" + ((char) 9822), "" + ((char) 9821), "" + ((char) 9819), "" + ((char) 9818), "" + ((char) 9823),
        "", "",
        "" + ((char) 9814), "" + ((char) 9816), "" + ((char) 9815), "" + ((char) 9813), "" + ((char) 9812), "" + ((char) 9817),
        "" + ((char) 9750), "" /* + ((char) 9750) */
    };

    int csize = 20;
    int blackColor = 0x4b74fe, whiteColor = 0xffffff, possibleMoveColor = 0x634876, killedBackground = 0x575482;
    int[][] board;
    private int currentCelly = -1;
    private int currentCellx = -1;

    Font font, halffont;
    ArrayList<int[]> prevPossMoves;
    ArrayList<Integer> blackKilled, whiteKilled;
    IChessBoard listener;
    JTextArea movesList;

    public ChessBoard() {

        movesList = new JTextArea();
        movesList.setEditable(false);
        add(movesList);
        
        board = new int[8][8];
        copyBoard(board, INITBOARD);
        
        blackKilled = new ArrayList<>();
        whiteKilled = new ArrayList<>();
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int y = e.getY() / csize;
                int x = e.getX() / csize;

                int evt = e.getButton();
                switch (evt) {
                    case MouseEvent.BUTTON1:    //Select the piece.
                        if (valid(x, y) && board[y][x] != NN) {
                            if(listener.cellSelected(SELECT, y, x) == HIGHLIGHT)
                                highLightCell(y, x);
                        }
                        break;
                    case MouseEvent.BUTTON3:    //Lift the piece
                        if (valid(x, y)) {
                            listener.cellSelected(MOVE, y, x);
                        }
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // may be we animate the things here.
                //System.out.println(e.getX() + ", " + e.getY());
            }
        });
    }
    
    public static void printBoard(int board[][]) {
        System.out.println("\n\n------CHESSBOARD-----");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(TAB[board[i][j]]);
            }
            System.out.println();
        }
    }

    private boolean valid(int x, int y) {
        return (x > -1 && x < 8 && y > -1 && y < 8);
    }

    private void unhighLightCell(int y, int x) {
        if (!valid(x, y)) {
            return;
        }
        Graphics g = this.getGraphics();
        g.setFont(font);
        drawCell(g, y, x, NORMAL);
    }

    private void highLightCell(int y, int x) {
        if (!valid(x, y)) {
            return;
        }
        unhighLightCell(currentCelly, currentCellx);
        currentCellx = x;
        currentCelly = y;
        Graphics g = this.getGraphics();
        g.setFont(font);
        drawCell(g, y, x, HIGHLIGHT);
    }

    public void draw(int[][] board) {
        this.board = board;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        csize = Integer.min(getHeight() / 8, getWidth() / 8);
        font = new Font("CourierNew", Font.BOLD, csize);
        halffont = new Font("CourierNew", Font.BOLD, csize / 2);
        g.setFont(font);
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                drawCell(g, i, j, NORMAL);
            }
        }
        drawKilled(g);
        movesList.setBounds(csize * 8 + 10, csize + 10, csize * 4 - 10, csize * 6 - 20);
    }

    private void drawCell(Graphics g, int i, int j, int mode) {
        switch (mode) {
            case HIGHLIGHT:
                g.setColor(Color.red);
                g.drawRect(j * csize, i * csize, csize - 1, csize - 1);
                break;
            case POSSIBLEMOVE:
                g.setColor(new Color(possibleMoveColor));
                g.fillRect(j * csize, i * csize, csize, csize);
                break;
            case NORMAL:
                g.setColor(new Color(((i + j) & 1) != 0 ? blackColor : whiteColor));
                g.fillRect(j * csize, i * csize, csize, csize);
                break;
        }
        g.setColor(new Color(0));
        g.drawString(TAB[board[i][j]], j * csize, (i + 1) * csize - (csize / 10));
    }

    public static void copyBoard(int[][] dest, int[][] src) {
        for (int i = 0; i < 8; i++) {
            System.arraycopy(src[i], 0, dest[i], 0, 8);
        }
    }

    public static int getColor(int piece) {
        return ((piece & 0x8) >> 3);
    }

    private void drawKilled(Graphics g) {
        
        g.setColor(new Color(killedBackground));
        g.fillRect(csize * 8 + 10, 0, csize * 4, csize);
        g.fillRect(csize * 8 + 10, csize * 7, csize * 4, csize);
        g.setColor(Color.black);
        g.setFont(halffont);
        
        for (int i = 0; i < whiteKilled.size(); i++) {
            g.drawString(TAB[whiteKilled.get(i)], csize * 8 + 10 + i * (csize / 2), (csize / 2) + (i / 8) * (csize / 2));
        }
        
        for (int i = 0; i < blackKilled.size(); i++) {
            g.drawString(TAB[blackKilled.get(i)], csize * 8 + 10 + i * (csize / 2), (int)(csize * 7.5) + (i / 8) * (csize / 2));
        }
    }
    
    void setBoardListener(IChessBoard cbl) {
        this.listener = cbl;
    }

    void showPossibleMoves(ArrayList<int[]> moves) {
        Graphics g = this.getGraphics();
        g.setFont(font);
        if(prevPossMoves != null) {
            for (int[] prevPossMove : prevPossMoves) {
                drawCell(g, prevPossMove[0], prevPossMove[1], NORMAL);
            }
        }
        prevPossMoves = moves;
        if (moves != null) {
            for (int[] move : moves) {
                drawCell(g, move[0], move[1], POSSIBLEMOVE);
            }
        }
    }

    void notifyCapture(int piece) {
        (getColor(piece) == WHITE ? whiteKilled : blackKilled).add(piece);
    }

    void notifyMove(int sx, int sy, int dx, int dy) {
        movesList.append("" + ((char)(sx + 'a')) + (8 - sy) + " " + ((char)(dx + 'a')) + (8 - dy) + "\n");
    }
}
