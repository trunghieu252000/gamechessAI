
package components.core.classic.pieces_v2;

import java.util.ArrayList;
import java.util.List;

public class EmptyCell extends Piece{

    public EmptyCell(int _color, Coordinate _coordinate) {
        super(_color, _coordinate);
        this.name = "";
        this.Point = Piece.EMPTY_POINT;
    }
    @Override
    public List<Coordinate> getPossibleMove(Piece[][] board) {
         List<Coordinate> list = new ArrayList<Coordinate>() ;
         return list;
    }
    
}
