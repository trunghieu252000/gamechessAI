package components.core.classic.board;

import static components.core.classic.board.Move.MoveFactory;

public enum MoveUtils {

    INSTANCE;

    public static int exchangeScore(final Move move) {
        if(move == MoveFactory.getNullMove()) {
            return 1;
        }
        return move.isAttack() ?
                5 * exchangeScore(move.getBoard().getTransitionMove()) :
                exchangeScore(move.getBoard().getTransitionMove());

    }

}
