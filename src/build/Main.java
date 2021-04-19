
package build;

import components.core.classic.pieces_v2.Bishop;
import components.core.classic.pieces_v2.Coordinate;
import components.core.classic.pieces_v2.EmptyCell;
import components.core.classic.pieces_v2.Rook;
import components.core.classic.pieces_v2.Knight;
import components.core.classic.pieces_v2.Queen;
import components.core.classic.pieces_v2.Piece;
import network.Request;
import network.Client;
import network.ThreadAI;
import components.core.gamechess.*;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;


public class Main extends JFrame {

    public static final int PLAYER_PLAYER = 100;
    public static final int PLAYER_COMPUTER = 101;
    public static final int PLAYER_ONLINE = 102;
    public static final int CHALLENGE = 103;
    public static final int TO_BE_CHALLENGED = 104;
    public static int Mode;
    public int rowBefore = -1;
    public int columBefore = -1;
    public static ArrayList<Coordinate> listCoordinate;
    public static boolean checkSelect = false;
    public static Coordinate pieceSelect = new Coordinate(-1, -1);
    public static Thread listener;
    public static int capacity = -1;
    public static Main Mymain;
    public static boolean waitForEnemy;
    public static boolean waitAI = false;
    public static int k = 2;

    //--------------------------------------------------------------------------
    public static boolean checkKingWhiteMove = false;
    public static boolean checkKingBlackMove = false;
    public static boolean checkRookLeftWhiteMove = false;
    public static boolean checkRookLeftBlackMove = false;
    public static boolean checkRookRightWhiteMove = false;
    public static boolean checkRookRightBlackMove = false;
    public ThreadAI aI;
    public PlayOnline playonline;
    //--------------------------------------------------------------------------
    private JButton btn_home;
    private JButton btn_newGame;
    private JButton btn_surr;
    public JPanel chessBoard;
    private JButton jButton4;
    private JMenu jMenu1;
    private JMenuBar jMenuBar1;
    private JPanel jPanel1;
    private JToolBar jToolBar1;
    public static JLabel lb_turn;
    //-----------------------------------------------------------------------------

    public Main() {

        initComponents();
        chessBoard.removeAll();
        this.getContentPane().setBackground(Color.WHITE);
        ImageIcon icon = new ImageIcon("art/pieceArt/MAIN.jpg");
        JLabel label = new JLabel();
        label.setIcon(icon);
        chessBoard.add(label);
        chessBoard.setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                try {
                    Client.requestSurrener();
                    System.out.println(InetAddress.getLocalHost().getHostAddress().toString());
                    Client.RequestDisConnect("/" + InetAddress.getLocalHost().getHostAddress().toString());
                    Client.server.close();
                } catch (Exception ex) {
                }
            }
        });

        listener = new Thread(new Runnable() {
            private volatile boolean running = true;

            public void terminate() {
                System.out.println("stop listener");
                running = false;
            }

            @Override
            public void run() {
                while (running) {
                    Request req = Client.response();// doc xu ly request tu client                    
                    switch (req.getType()) {

                        case Request.CLIENT1:
                            int dialogButton = JOptionPane.showConfirmDialog(null, "Waiting For Your Opponent", "Challenge", JOptionPane.YES_NO_OPTION);
                            if (dialogButton == JOptionPane.YES_OPTION) {
                                Client.requestAcceptChallenge(req.getIPChallenge());
                                Main.capacity = Main.TO_BE_CHALLENGED;
                                waitForEnemy = false;
                                newGamePlayOnline();//...............
                            }
                            break;
                        case Request.CLIENT2:
                            Main.capacity = Main.CHALLENGE;
                            JOptionPane.showMessageDialog(null, "Start The Match");
                            capacity = Main.CHALLENGE;
                            waitForEnemy = true;
                            BoardChess.turn = Piece.White;
                            newGamePlayOnline();//................
                            break;
                        case Request.ONLINE_PLAYER:
                            PlayOnline.listPlayer = req.getPlayerList();
                            playonline.room();
                            playonline.LoadListPlayerOnline();

                            break;
                        case Request.MOVE_CHESS:
                            System.err.println("CLIENT MOVE CHESS");
                            Main.Mymain.chessBoard.removeAll();
                            System.out.println(req.getStart().getRow() + "---" + req.getStart().getCol());
                            System.out.println(req.getTarget().getRow() + "---" + req.getTarget().getCol());

                            BoardChess.chessBoard[req.getTarget().getRow()][req.getTarget().getCol()] = BoardChess.chessBoard[req.getStart().getRow()][req.getStart().getCol()];

                            BoardChess.chessBoard[req.getTarget().getRow()][req.getTarget().getCol()].getCoordinate().setRow(req.getTarget().getRow());

                            BoardChess.chessBoard[req.getTarget().getRow()][req.getTarget().getCol()].getCoordinate().setCol(req.getTarget().getCol());

                            BoardChess.chessBoard[req.getStart().getRow()][req.getStart().getCol()] = new EmptyCell(-1, new Coordinate(req.getStart().getRow(), req.getStart().getCol()));

                            if (Main.Mymain.capacity == TO_BE_CHALLENGED) {
                                BoardChess.ReverseChessBoard(BoardChess.chessBoard, BoardChess.chessBoardReverse);
                                BoardChess.turn = Piece.Black;
                            }
                            if (Main.Mymain.capacity == CHALLENGE) {
                                BoardChess.turn = Piece.White;
                            }
                            waitForEnemy = true;
                            chessBoard.add(BoardChess.paintChessBox(new EmptyCell(-1, new Coordinate(req.getStart().getRow(), -1)), null));
                            chessBoard.setVisible(true);
                            Main.Mymain.setVisible(true);
                            break;

                        case Request.SURRENDER:
                            JOptionPane.showMessageDialog(null, "OPPONENT SURRENDER\n" + "YOU WIN");
                            Main.Mymain.loadHome();
                            break;

                        case Request.CROWN:
                            switch (req.getCrown()) {
                                case Request.CROWNROOK:
                                    if (capacity == CHALLENGE) {
                                        BoardChess.chessBoard[req.getCoordinateCrown().getRow()][req.getCoordinateCrown().getCol()] = new Rook(Piece.Black, req.getCoordinateCrown());
                                    } else {
                                        BoardChess.chessBoard[req.getCoordinateCrown().getRow()][req.getCoordinateCrown().getCol()] = new Rook(Piece.White, req.getCoordinateCrown());
                                        BoardChess.ReverseChessBoard(BoardChess.chessBoard, BoardChess.chessBoardReverse);
                                    }
                                    break;
                                case Request.CROWNQUEEN:
                                    if (capacity == CHALLENGE) {
                                        BoardChess.chessBoard[req.getCoordinateCrown().getRow()][req.getCoordinateCrown().getCol()] = new Queen(Piece.Black, req.getCoordinateCrown());
                                    } else {
                                        BoardChess.chessBoard[req.getCoordinateCrown().getRow()][req.getCoordinateCrown().getCol()] = new Queen(Piece.White, req.getCoordinateCrown());
                                        BoardChess.ReverseChessBoard(BoardChess.chessBoard, BoardChess.chessBoardReverse);
                                    }
                                    break;
                                case Request.CROWNKNIGHT:
                                    if (capacity == CHALLENGE) {
                                        BoardChess.chessBoard[req.getCoordinateCrown().getRow()][req.getCoordinateCrown().getCol()] = new Knight(Piece.Black, req.getCoordinateCrown());
                                    } else {
                                        BoardChess.chessBoard[req.getCoordinateCrown().getRow()][req.getCoordinateCrown().getCol()] = new Knight(Piece.White, req.getCoordinateCrown());
                                        BoardChess.ReverseChessBoard(BoardChess.chessBoard, BoardChess.chessBoardReverse);
                                    }
                                    break;
                                case Request.CROWNBISHOP:
                                    if (capacity == CHALLENGE) {
                                        BoardChess.chessBoard[req.getCoordinateCrown().getRow()][req.getCoordinateCrown().getCol()] = new Bishop(Piece.Black, req.getCoordinateCrown());
                                    } else {
                                        BoardChess.chessBoard[req.getCoordinateCrown().getRow()][req.getCoordinateCrown().getCol()] = new Bishop(Piece.White, req.getCoordinateCrown());
                                        BoardChess.ReverseChessBoard(BoardChess.chessBoard, BoardChess.chessBoardReverse);
                                    }
                                    break;

                            }
                            chessBoard.removeAll();
                            chessBoard.add(BoardChess.paintChessBox(new EmptyCell(-1, new Coordinate(-2, -1)), null));
                            chessBoard.setVisible(true);
                            Main.Mymain.setVisible(true);
                            break;
                    }
                }
            }
        });
    }

    private void initComponents() {
        this.setTitle("Chess");
        jMenu1 = new JMenu();
        chessBoard = new JPanel();
        jToolBar1 = new JToolBar();
        btn_home = new JButton();
        btn_newGame = new JButton();
        jButton4 = new JButton();
        jPanel1 = new JPanel();
        btn_surr = new JButton();
        lb_turn = new JLabel();
        jMenuBar1 = new JMenuBar();


        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBackground(new Color(153, 204, 0));
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        chessBoard.setPreferredSize(new Dimension(600, 600));
        chessBoard.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                chessBoardMouseClicked(evt);
            }
        });
        chessBoard.setLayout(new java.awt.GridLayout(1, 0));

        jToolBar1.setRollover(true);

        btn_home.setIcon(new ImageIcon("art/pieceArt/Home.png")); 
        btn_home.setFocusable(false);
        btn_home.setHorizontalTextPosition(SwingConstants.CENTER);
        btn_home.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn_home.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btn_homeActionPerformed(evt);
            }
        });
        jToolBar1.add(btn_home);

        btn_newGame.setIcon(new ImageIcon("art/pieceArt/new.png")); 
        btn_newGame.setFocusable(false);
        btn_newGame.setHorizontalTextPosition(SwingConstants.CENTER);
        btn_newGame.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn_newGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btn_newGameActionPerformed(evt);
            }
        });
        jToolBar1.add(btn_newGame);

        jButton4.setText("jButton4");

        jPanel1.setBackground(new Color(255, 255, 255));

        btn_surr.setText("SURRENDER");
        btn_surr.setVisible(false);
        btn_surr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btn_surrActionPerformed(evt);
            }
        });

        lb_turn.setHorizontalAlignment(SwingConstants.CENTER);
        lb_turn.setText("PLAY ONLINE");
        lb_turn.setVisible(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btn_surr, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lb_turn, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(btn_surr, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(lb_turn, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(51, Short.MAX_VALUE))
        );

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(223, 223, 223))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(49, 49, 49)
                                .addComponent(chessBoard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(33, 33, 33)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(54, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(16, 16, 16)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(72, 72, 72)
                                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(chessBoard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }

    private void chessBoardMouseClicked(MouseEvent evt) {

        columBefore = evt.getX() / 75;
        rowBefore = evt.getY() / 75;
        chessBoard.removeAll();

        if (checkSelect == false) {
            pieceSelect.setRow(rowBefore);
            pieceSelect.setCol(columBefore);

            // CHECK ONLINE
            if (Mode == PLAYER_ONLINE) {
                ;
                if (capacity == TO_BE_CHALLENGED) {
                    rowBefore = 7 - rowBefore;
                    columBefore = 7 - columBefore;
                }
            }

            // CHECK AI
            if (Mode == PLAYER_COMPUTER) {
                if (!waitAI) {
                    return;
                }
            }

            if (BoardChess.turn == BoardChess.chessBoard[rowBefore][columBefore].getColor()) {
                System.out.println(BoardChess.chessBoard[rowBefore][columBefore].getName());
                listCoordinate = new ArrayList<Coordinate>(BoardChess.chessBoard[rowBefore][columBefore].getPossibleMove(BoardChess.chessBoard));
                if (waitForEnemy == false && Mode == PLAYER_ONLINE)
                    return;
                checkSelect = true;
                // CHECK RESULT: WIN OR CLOSE OR HOA
                checkResult();

                // PROCESS ONLINE
                if (Mode == PLAYER_ONLINE) {
                    if (capacity == TO_BE_CHALLENGED) {
                        for (int i = 0; i < listCoordinate.size(); i++) {
                            listCoordinate.get(i).setRow(7 - listCoordinate.get(i).getRow());
                            listCoordinate.get(i).setCol(7 - listCoordinate.get(i).getCol());
                        }
                    }
                }

                chessBoard.add(BoardChess.paintChessBox(BoardChess.chessBoard[pieceSelect.getRow()][pieceSelect.getCol()], listCoordinate));
                chessBoard.setVisible(true);
                this.setVisible(true);
            }

        } else {
            boolean isCrown = false;
            if (listCoordinate != null || listCoordinate.size() > 0) {

                // Process Online
                if (Mode == PLAYER_ONLINE) {
                    if (capacity == TO_BE_CHALLENGED) {
                        pieceSelect.setRow(7 - pieceSelect.getRow());
                        pieceSelect.setCol(7 - pieceSelect.getCol());

                        for (int j = 0; j < listCoordinate.size(); j++) {
                            listCoordinate.get(j).setRow(7 - listCoordinate.get(j).getRow());
                            listCoordinate.get(j).setCol(7 - listCoordinate.get(j).getCol());
                        }

                        rowBefore = 7 - rowBefore;
                        columBefore = 7 - columBefore;
                    }
                }
                for (int i = 0; i < listCoordinate.size(); i++) {
                    if (rowBefore == listCoordinate.get(i).getRow() && columBefore == listCoordinate.get(i).getCol()) {
                        // SWAP AND MOVE CHESS
                        Piece temp = BoardChess.chessBoard[rowBefore][columBefore];
                        temp.getCoordinate().setRow(rowBefore);
                        temp.getCoordinate().setCol(columBefore);

                        BoardChess.chessBoard[rowBefore][columBefore] = BoardChess.chessBoard[pieceSelect.getRow()][pieceSelect.getCol()];
                        BoardChess.chessBoard[rowBefore][columBefore].getCoordinate().setRow(rowBefore);
                        BoardChess.chessBoard[rowBefore][columBefore].getCoordinate().setCol(columBefore);
                        BoardChess.chessBoard[pieceSelect.getRow()][pieceSelect.getCol()] = new EmptyCell(-1, new Coordinate(pieceSelect.getRow(), pieceSelect.getCol()));
                        // CHECK INVALID OF PIECE GO

                        // CHECK RESULT
                        checkResult();

                        if (BoardChess.checkMate() == true) {
                            JOptionPane.showMessageDialog(null, "" + "King is in danger", "Error", JOptionPane.ERROR_MESSAGE);

                            // BACK PIECE GO BEFORE
                            BoardChess.chessBoard[pieceSelect.getRow()][pieceSelect.getCol()] = BoardChess.chessBoard[rowBefore][columBefore];
                            BoardChess.chessBoard[pieceSelect.getRow()][pieceSelect.getCol()].getCoordinate().setRow(pieceSelect.getRow());
                            BoardChess.chessBoard[pieceSelect.getRow()][pieceSelect.getCol()].getCoordinate().setCol(pieceSelect.getCol());

                            BoardChess.chessBoard[rowBefore][columBefore] = temp;
                            BoardChess.chessBoard[rowBefore][columBefore].getCoordinate().setRow(rowBefore);
                            BoardChess.chessBoard[rowBefore][columBefore].getCoordinate().setCol(columBefore);
                            return;
                        } else {
                            // SEND REQUEST FOR SERVER
                            if (Mode == Main.PLAYER_ONLINE && Main.capacity == TO_BE_CHALLENGED) {
                                Client.requestMoveChess(pieceSelect, new Coordinate(rowBefore, columBefore));// clienttt, gui toa do client2
                                BoardChess.ReverseChessBoard(BoardChess.chessBoard, BoardChess.chessBoardReverse);
                                waitForEnemy = false;
                            } else if (Mode == Main.PLAYER_ONLINE) {
                                Client.requestMoveChess(pieceSelect, new Coordinate(rowBefore, columBefore));// clientttt, gui toa do client1
                                waitForEnemy = false;
                            }
                            if (Mode == PLAYER_COMPUTER) {
                                this.waitAI = false;
                            }

                            // CHECK MOVE OF KING AND ROOK
                            switch (BoardChess.chessBoard[pieceSelect.getRow()][pieceSelect.getCol()].getName()) {
                                case "White_King":
                                    Main.checkKingWhiteMove = true;
                                    break;
                                case "Black_King":
                                    Main.checkKingBlackMove = true;
                                    break;
                                case "White_Rook":
                                    if (pieceSelect.getCol() == 0) {
                                        Main.checkRookLeftWhiteMove = true;
                                    }
                                    if (pieceSelect.getCol() == 7) {
                                        System.out.println("checkRookRightWhiteMove");
                                        Main.checkRookRightWhiteMove = true;
                                    }
                                    break;
                                case "Black_Rook":
                                    if (pieceSelect.getCol() == 7) {
                                        Main.checkRookLeftBlackMove = true;
                                    }
                                    if (pieceSelect.getCol() == 0) {
                                        Main.checkRookRightBlackMove = true;
                                    }
                                    break;
                            }
                            // Enter into

                            // ento into King Black
                            if (BoardChess.chessBoard[rowBefore][columBefore].getName().equals("BK")) {
                                // KING ENTO INTO LEFT
                                if (rowBefore == 0 && columBefore == 2 && !Main.checkRookRightBlackMove) {
                                    // PROCESS ONLINE
                                    if (Mode == Main.PLAYER_ONLINE) {
                                        Client.requestMoveChess(new Coordinate(0, 0), new Coordinate(0, 3));//clienttt
                                    }

                                    BoardChess.chessBoard[0][3] = BoardChess.chessBoard[0][0];
                                    BoardChess.chessBoard[0][3].getCoordinate().setCol(3);
                                    BoardChess.chessBoard[0][0] = new EmptyCell(-1, new Coordinate(0, 0));

                                }
                                // KING ENTER INTO RIGHT
                                if (rowBefore == 0 && columBefore == 6 && !Main.checkRookLeftBlackMove) {
                                    if (Mode == Main.PLAYER_ONLINE) {
                                        Client.requestMoveChess(new Coordinate(0, 7), new Coordinate(0, 5));// clientttttt
                                    }

                                    BoardChess.chessBoard[0][5] = BoardChess.chessBoard[0][7];
                                    BoardChess.chessBoard[0][5].getCoordinate().setCol(5);
                                    BoardChess.chessBoard[0][7] = new EmptyCell(-1, new Coordinate(0, 7));

                                }

                                // process online
                                if (capacity == TO_BE_CHALLENGED) {
                                    BoardChess.ReverseChessBoard(BoardChess.chessBoard, BoardChess.chessBoardReverse);
                                }

                            }

                            // KING WHITE ENTER INTO
                            if (BoardChess.chessBoard[rowBefore][columBefore].getName().equals("WK")) {
                                // ENTER INTO LEFT
                                if (rowBefore == 7 && columBefore == 2 && !Main.checkRookLeftWhiteMove) {
                                    // PROCESS ONLINE ENTER INTO
                                    if (Mode == PLAYER_ONLINE) {
                                        Client.requestMoveChess(new Coordinate(7, 0), new Coordinate(7, 3));// clienttt
                                    }
                                    BoardChess.chessBoard[7][3] = BoardChess.chessBoard[7][0];
                                    BoardChess.chessBoard[7][3].getCoordinate().setCol(3);
                                    BoardChess.chessBoard[7][0] = new EmptyCell(-1, new Coordinate(7, 0));

                                }
                                if (rowBefore == 7 && columBefore == 6 && !Main.checkRookRightWhiteMove) {
                                    // PEOCESS ONLINE ENTER
                                    if (Mode == PLAYER_ONLINE) {
                                        Client.requestMoveChess(new Coordinate(7, 7), new Coordinate(7, 5));// clienttt
                                    }
                                    BoardChess.chessBoard[7][5] = BoardChess.chessBoard[7][7];
                                    BoardChess.chessBoard[7][5].getCoordinate().setCol(5);
                                    BoardChess.chessBoard[7][7] = new EmptyCell(-1, new Coordinate(7, 7));

                                }

                                // process online
                                if (capacity == TO_BE_CHALLENGED) {
                                    BoardChess.ReverseChessBoard(BoardChess.chessBoard, BoardChess.chessBoardReverse);
                                }
                            }
                            // turn
                            if (BoardChess.turn == Piece.White) {
                                isCrown = Crown();
                                BoardChess.turn = Piece.Black;
                            } else {
                                isCrown = Crown();
                                BoardChess.turn = Piece.White;
                            }
                        }
                    }
                }
                checkSelect = false;
                if (!isCrown) {
                    chessBoard.add(BoardChess.paintChessBox(new EmptyCell(-1, new Coordinate(-1, -1)), null));
                    chessBoard.setVisible(true);
                    this.setVisible(true);
                }
            }
        }

    }


    private void btn_newGameActionPerformed(ActionEvent evt) {
        components.core.gamechess.Mode modeWindow = new Mode(this);
        modeWindow.setVisible(true);
    }

    private void btn_homeActionPerformed(ActionEvent evt) {
        if (BoardChess.checkInit) {
            int n = JOptionPane.showConfirmDialog(null, "Do you want to quit game?", "Notification", JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                loadHome();
            }
        }
    }

    private void btn_surrActionPerformed(ActionEvent evt) {// dau hang
        // SUR WHEN ONLINE
        if (Mode == PLAYER_ONLINE) {
            Client.requestSurrener();// client
            loadHome();
        } else {
            JOptionPane.showMessageDialog(null, "Operation fail");
        }
    }

    public void checkResult() {
        if (BoardChess.checkResult() == 1) {
            String temp;
            if (BoardChess.turn == Piece.White) {
                temp = "Chess Black Win";
            } else {
                temp = "Chess White Win";
            }
            JOptionPane.showMessageDialog(new JFrame(), temp, "Notification", JOptionPane.PLAIN_MESSAGE);
        } else if (BoardChess.checkResult() == 2) {// co hoa
            JOptionPane.showMessageDialog(new JFrame(), "ICE", "Notification", JOptionPane.PLAIN_MESSAGE);
        }
    }


    // hoÃ¡n Ä‘á»•i quÃ¢n tá»‘t    
    public boolean Crown() {
        if (BoardChess.turn == Piece.White) {
            for (int iCol = 0; iCol < 8; iCol++) {
                if (BoardChess.chessBoard[0][iCol].getName().equals("WP")) {
                    new Crown(new Coordinate(0, iCol), this).setVisible(true);
                    return true;
                }
            }
        }

        if (BoardChess.turn == Piece.Black) {
            for (int iCol = 0; iCol < 8; iCol++) {
                if (BoardChess.chessBoard[7][iCol].getName().equals("BP")) {
                    new Crown(new Coordinate(7, iCol), this).setVisible(true);
                    return true;
                }
            }
        }
        return false;

    }

    // tá»± chÆ¡i 1 mÃ¬nh
    public void newGamePlayerAndPlayer() {
        chessBoard.removeAll();
        BoardChess.initValue();
        capacity = CHALLENGE;
        Mode = PLAYER_PLAYER;
        if (BoardChess.turn == Piece.Black) {
            lb_turn.setText("Turn of Black");
        }
        if (BoardChess.turn == Piece.White) {
            lb_turn.setText("Turn of White");
        }
        chessBoard.add(BoardChess.paintChessBox(new EmptyCell(-1, new Coordinate(-1, -1)), null));
        chessBoard.setVisible(true);
        this.setVisible(true);
        lb_turn.setVisible(true);
    }

    //Ä‘Ã¡nh vá»›i mÃ¡y
    public void newGamePlayrtAndAI() {
        chessBoard.removeAll();
        BoardChess.initValue();
        BoardChess.initBoard(BoardChess.chessBoard);
        Mode = PLAYER_COMPUTER;
        waitAI = true;
        this.aI = new ThreadAI(this, Piece.Black);
    }

    
    public void newGamePlayOnline() {
        chessBoard.removeAll();
        BoardChess.initValue();
        Mode = PLAYER_ONLINE;
        System.out.println("Playyyyyyyyyyyyyyyyyyyyyy");
        chessBoard.add(BoardChess.paintChessBox(new EmptyCell(-1, new Coordinate(-1, -1)), null));
        chessBoard.setVisible(true);
        this.setVisible(true);
        lb_turn.setVisible(true);
        btn_surr.setVisible(true);
        btn_newGame.setVisible(false);
    }

  
    public void loadHome() {
    	if(capacity==CHALLENGE|| capacity==TO_BE_CHALLENGED) {
    		try {
    			Client.RequestDisConnect("/" + InetAddress.getLocalHost().getHostAddress().toString());
                Client.server.close();
			} catch (Exception e) {
			}
    	}
        BoardChess.initValue();
        chessBoard.removeAll();
        this.getContentPane().setBackground(Color.WHITE);
        ImageIcon icon = new ImageIcon("art/pieceArt/MAIN.jpg");
        JLabel label = new JLabel();
        label.setIcon(icon);
        chessBoard.add(label);
        chessBoard.setVisible(true);
        this.setVisible(true);
        btn_newGame.setVisible(true);
    }

    public static void main(String args[]) {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        new Main().setVisible(true);

    }

}
