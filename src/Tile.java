public abstract class Tile {
    // Int for tile location
    int tileCoordinate;

    // Constructor for setting location
    Tile(int tileCoordinate) {
        this.tileCoordinate = tileCoordinate;
    }

    // True or false depending if piece is on tile
    // Get the piece if its occupied
    public abstract boolean isTileOccupied();
    public abstract Piece getPiece();
}