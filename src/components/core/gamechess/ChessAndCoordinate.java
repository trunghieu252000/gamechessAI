
package components.core.gamechess;

import components.core.classic.pieces_v2.Coordinate;

import java.util.List;

public class ChessAndCoordinate {
    
    private List<Coordinate> listCoordinate;
    private Coordinate myChess;

    public List<Coordinate> getListCoordinate() {
        return listCoordinate;
    }

    public Coordinate getMyChess() {
        return myChess;
    }

    public ChessAndCoordinate(List<Coordinate> listCoordinate, Coordinate myChess) {
        this.listCoordinate = listCoordinate;
        this.myChess = myChess;
    }

    public void setListCoordinate(List<Coordinate> listCoordinate) {
        this.listCoordinate = listCoordinate;
    }

    public void setMyChess(Coordinate myChess) {
        this.myChess = myChess;
    }
    
    
    
}
