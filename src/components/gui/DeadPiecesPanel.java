package components.gui;

import com.google.common.primitives.Ints;
import components.core.classic.board.Move;
import components.core.classic.pieces_v1.Piece;
import components.gui.Table.MoveLog;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class DeadPiecesPanel extends JPanel {

	private final JPanel northPanel;
	private final JPanel southPanel;

	private static final long serialVersionUID = 1L;
	private static final Color PANEL_COLOR = Color.decode("0xFDF5E6");
	private static final Dimension TAKEN_PIECES_PANEL_DIMENSION = new Dimension(40, 80);
	private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);

	public DeadPiecesPanel() {
		super(new BorderLayout());
		setBackground(Color.decode("0xFDF5E6"));
		setBorder(PANEL_BORDER);
		this.northPanel = new JPanel(new GridLayout(8, 2));
		this.southPanel = new JPanel(new GridLayout(8, 2));
		this.northPanel.setBackground(PANEL_COLOR);
		this.southPanel.setBackground(PANEL_COLOR);
		add(this.northPanel, BorderLayout.NORTH);
		add(this.southPanel, BorderLayout.SOUTH);
		setPreferredSize(TAKEN_PIECES_PANEL_DIMENSION);
	}

	public void redo(final MoveLog moveLog) {
		southPanel.removeAll();
		northPanel.removeAll();

		final List<Piece> whiteTakenPieces = new ArrayList<>();
		final List<Piece> blackTakenPieces = new ArrayList<>();

		for (final Move move : moveLog.getMoves()) {
			if (move.isAttack()) {
				final Piece takenPiece = move.getAttackedPiece();
				if (takenPiece.getPieceAllegiance().isWhite()) {
					whiteTakenPieces.add(takenPiece);
				} else if (takenPiece.getPieceAllegiance().isBlack()) {
					blackTakenPieces.add(takenPiece);
				} else {
					throw new RuntimeException("Should not reach here!");
				}
			}
		}

		Collections.sort(whiteTakenPieces, new Comparator<Piece>() {
			@Override
			public int compare(final Piece p1, final Piece p2) {
				return Ints.compare(p1.getPieceValue(), p2.getPieceValue());
			}
		});

		Collections.sort(blackTakenPieces, new Comparator<Piece>() {
			@Override
			public int compare(final Piece p1, final Piece p2) {
				return Ints.compare(p1.getPieceValue(), p2.getPieceValue());
			}
		});

		for (final Piece takenPiece : whiteTakenPieces) {
			try {
				final BufferedImage image = ImageIO
						.read(new File("art/pieceArt/" + takenPiece.getPieceAllegiance().toString().substring(0, 1)
								+ "" + takenPiece.toString() + ".png"));
				final ImageIcon ic = new ImageIcon(image);
				final JLabel imageLabel = new JLabel(new ImageIcon(ic.getImage()
						.getScaledInstance(ic.getIconWidth()/2, ic.getIconWidth()/2, Image.SCALE_SMOOTH)));
				this.southPanel.add(imageLabel);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

		for (final Piece takenPiece : blackTakenPieces) {
			try {
				final BufferedImage image = ImageIO
						.read(new File("art/pieceArt/" + takenPiece.getPieceAllegiance().toString().substring(0, 1)
								+ "" + takenPiece.toString() + ".png"));
				final ImageIcon ic = new ImageIcon(image);
				final JLabel imageLabel = new JLabel(new ImageIcon(ic.getImage()
						.getScaledInstance(ic.getIconWidth()/2, ic.getIconWidth()/2, Image.SCALE_SMOOTH)));
				this.northPanel.add(imageLabel);

			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

		validate();
	}
}