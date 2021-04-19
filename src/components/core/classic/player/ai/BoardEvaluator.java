package components.core.classic.player.ai;

import components.core.classic.board.Board;

public interface BoardEvaluator {

    int evaluate(Board board, int depth);

}
