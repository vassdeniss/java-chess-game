public class EmptyTile extends Tile {
    EmptyTile(int coordinate) {
        super(coordinate);
    }

    // Return false - no piece on empty tile
    @Override
    public boolean isTileOccupied() {
        return false;
    }

    // Return null - no piece on empty tile
    @Override
    public Piece getPiece() {
        return null;
    }
}
