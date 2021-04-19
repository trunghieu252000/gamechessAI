package components.core.classic.player.ai;

import components.core.classic.board.Board;
import components.core.classic.board.Move;

public interface MoveStrategy {

    long getNumBoardsEvaluated();

    Move execute(Board board);

}
