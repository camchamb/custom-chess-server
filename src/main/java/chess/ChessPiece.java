package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    public ChessPiece(String piece) {
        switch (piece) {
            case "K" -> {
                this.pieceColor = ChessGame.TeamColor.WHITE;
                this.type = PieceType.KING;
            }
            case "Q" -> {
                this.pieceColor = ChessGame.TeamColor.WHITE;
                this.type = PieceType.QUEEN;
            }
            case "B" -> {
                this.pieceColor = ChessGame.TeamColor.WHITE;
                this.type = PieceType.BISHOP;
            }
            case "N" -> {
                this.pieceColor = ChessGame.TeamColor.WHITE;
                this.type = PieceType.KNIGHT;
            }
            case "R" -> {
                this.pieceColor = ChessGame.TeamColor.WHITE;
                this.type = PieceType.ROOK;
            }
            case "P" -> {
                this.pieceColor = ChessGame.TeamColor.WHITE;
                this.type = PieceType.PAWN;
            }
            case "k" -> {
                this.pieceColor = ChessGame.TeamColor.BLACK;
                this.type = PieceType.KING;
            }
            case "q" -> {
                this.pieceColor = ChessGame.TeamColor.BLACK;
                this.type = PieceType.QUEEN;
            }
            case "b" -> {
                this.pieceColor = ChessGame.TeamColor.BLACK;
                this.type = PieceType.BISHOP;
            }
            case "n" -> {
                this.pieceColor = ChessGame.TeamColor.BLACK;
                this.type = PieceType.KNIGHT;
            }
            case "r" -> {
                this.pieceColor = ChessGame.TeamColor.BLACK;
                this.type = PieceType.ROOK;
            }
            case "p" -> {
                this.pieceColor = ChessGame.TeamColor.BLACK;
                this.type = PieceType.PAWN;
            }
            default -> throw new RuntimeException("Invalid piece symbol: " + piece);
        }
    }

    @Override
    public String toString() {
        return "ChessPiece{" + pieceColor + type + '}';
    }

    public String toFen() {
        return switch (pieceColor) {
            case WHITE -> switch (type) {
                case KING -> "K";
                case QUEEN -> "Q";
                case BISHOP -> "B";
                case KNIGHT -> "N";
                case ROOK -> "R";
                case PAWN -> "P";
            };
            case BLACK -> switch (type) {
                case KING -> "k";
                case QUEEN -> "q";
                case BISHOP -> "b";
                case KNIGHT -> "n";
                case ROOK -> "r";
                case PAWN -> "p";
            };
        };
    }



    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var calculator = new PieceMovesCalculator(this.type, this.pieceColor, board, myPosition);
        return calculator.pieceMoves();
    }
}
