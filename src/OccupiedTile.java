public class OccupiedTile extends Tile {
    // Type of Piece for recognizing the piece
    Piece tilePiece;

    OccupiedTile(int coordinate, Piece tilePiece) {
        super(coordinate);
        this.tilePiece = tilePiece;
    }

    // Return true - there will be always a piece on the tile
    @Override
    public boolean isTileOccupied() {
        return true;
    }

    // Return tilePiece - there will be always a piece on the tile
    @Override
    public Piece getPiece() {
        return this.tilePiece;
    }
}
