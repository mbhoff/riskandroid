package edu.sdsmt.team4.MobileProject2;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * @author Krey Warshaw
 * Class to keep track of the game state and some functions for interacting with it.
 * Keeps track of all the players in the game, all the zones that are captured, how many rounds are
 * left and whose turn it is.
 */
class GameState implements Parcelable {
    /**
     * Maximum number of rounds in a game.
     */
    private static final int MAX_ROUNDS = 3;

    static int whoAmI;

    /**
     * ArrayList keeping track of who owns what square.
     * will be total length of (size * size)
     * Uncaptured Zones will be marked as the size of players
     */
    private ArrayList<Integer> captured = new ArrayList<>();
    
    /**
     * keeps track of how many rounds are left, one round is one iteration through all players
     */
    private int rounds;

    /**
     * current round
     */
    private int currRound = 1;

    /**
     * dimension of the game board, hardcoded to 10
     */
    private int size = 10;

    /**
     * Monitor object to be updated
     */
    private Monitor monitor = new Monitor();

    /**
     * Mandatory overload for Parcelable to describe contents
     */
    @Override
    public int describeContents(){
        return 0;
    }

    /**
     * Handles creating a parcel of the class
     * Builds a parcel out of the data members of the GameState class.
     * flattens the ArrayLists into a serialized data and writes and int
     * for the number of rounds left and the player turn.
     * @param out Parcel to flatten class into
     * @param flags flags for the parcelable
     */
    @Override
    public void writeToParcel(final Parcel out, final int flags) {
        out.writeSerializable(this.captured);
        out.writeSerializable(this.monitor);
        out.writeInt(rounds);
        out.writeInt(currRound);
    }

    /**
     * Making Creator to build a GameState from a parcel
     * Overrides the createFromParcel and newArray methods for the Creator Object
     */
    public static final Parcelable.Creator<GameState> CREATOR = new Parcelable.Creator<GameState>() {
        @Override
        public GameState createFromParcel(Parcel in) {
            return new GameState(in);
        }

        @Override
        public GameState[] newArray(int size) {
            return new GameState[size];
        }
    };

    /**
     * Constructor for GameState
     */
    GameState() {
        this.monitor.setPlayerTurn(0);
        this.rounds = MAX_ROUNDS;

        final int tempSize = size * size;
        for (int i = 0; i < tempSize; ++i)
            captured.add(2);
    }

    /**
     * Constructor to build GameState from a parcel
     * Unpacks data from the parcel in the order it was placed in the parcel
     * @param  parcel The parcel to build a GameState from
     */
    //suppresses the warnings for an unchecked cast
    @SuppressWarnings("unchecked")
    private GameState(final Parcel parcel) {
        this.captured = (ArrayList<Integer>) parcel.readSerializable();
        this.monitor = (Monitor) parcel.readSerializable();
        this.rounds = parcel.readInt();
        this.currRound = parcel.readInt();
    }

    /**
     * Returns the total number of rounds to play
     * @return int  The total number of rounds to be played
     */
    int getRounds() {
        return rounds;
    }

    /**
     * Returns the current game round
     * @return int  The current round the game is on
     */
    int getCurrRound() {
        return currRound;
    }

    /**
     * Increments currRounds
     */
    private void incRounds(){
        this.currRound += 1;
    }

    /**
     * Returns the player number for whose turn it is
     * @return int  number of current player
     */
    int getPlayerTurn() {
        return monitor.getPlayerTurn();
    }

    /**
     * Moves GameState to next player
     * Increments PlayerTurn in monitor, if on the last player we restart at player 0
     * and move to the next round.
     */
    void nextPlayer() {
        if (monitor.getPlayerTurn() + 1 >= 2){
            incRounds();
            monitor.setPlayerTurn(0);
        } else monitor.setPlayerTurn(monitor.getPlayerTurn() + 1);
    }

    /**
     * Returns who owns a given square
     * Checks the capture array for who owns the square, if unowned the number returned
     * will be equal to the number of players in the game.
     * @exception ArrayIndexOutOfBoundsException if i is out of capture array bounds
     * @param  i index of the capture array to check
     * @return int number of player who owns the space
     */
    int whoOwns(final int i) {
        if (i > captured.size())
            throw new ArrayIndexOutOfBoundsException();
        else
            return captured.get(i);
    }

    /**
     * Captures a square
     * Sets a zone equal to the current player
     * @param  index  index of capture array to capture
     */
    void capZone(final int index){
        captured.set(index, monitor.getPlayerTurn());
    }

    void setMonitor(final Monitor newMonitor) {
        monitor = newMonitor;
    }

    Monitor getMonitor() {
        return monitor;
    }

    /**
     * Returns the size of the game board
     * @return int The dimension of the game board
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the dimension of the game board
     * @param size The dimension to set the game board to
     */
    public void setSize(final int size) {
        this.size = size;
    }

    void startGame() {
        monitor.startGame();
    }

    /**
     * Returns the winner's name
     * Sets score to the first players score then lops through other
     * players testing for a higher score setting winner to a player with higher score
     * and
     * @return string  the name of the winner
     */
    boolean didIWin() {
        return getMyScore() >= getOpponentScore();
    }

    /**
     * Returns a new GameState object from a DataSnapshot, or null.
     */
    static GameState fromJSON(final DataSnapshot snapshot) {
        // check snapshot and necessary children exist
        if (!snapshot.exists()) return null;
        if (!snapshot.hasChild("Captured")) return null;
        if (!snapshot.hasChild("Current_Round")) return null;
        if (!snapshot.hasChild("Total_Rounds")) return null;

        final GameState newState = new GameState();
        final String captured = snapshot.child("Captured").getValue().toString();
        final char[] temp = captured.toCharArray();
        int count = 0;

        for (int i = 0; i < captured.length(); i++) {
            if (Character.isDigit(temp[i])) {
                newState.captured.set(count,Character.getNumericValue(temp[i]));
                count++;
            }
        }

        newState.currRound = Integer.parseInt(snapshot.child("Current_Round").getValue().toString());
        newState.rounds = Integer.parseInt(snapshot.child("Total_Rounds").getValue().toString());

        return newState;
    }

    void writeJSON(final DatabaseReference snapshot) {
        snapshot.child("Captured").setValue(writeCaptured());
        snapshot.child("Current_Round").setValue(this.currRound);
        snapshot.child("Total_Rounds").setValue(this.rounds);
    }

    String writeCaptured() {
        StringBuilder output = new StringBuilder();
        final int tempSize = this.size * this.size;
        for (int i = 0; i < tempSize; ++i) {
            output.append(this.captured.get(i).toString());
            if (i != tempSize - 1)
                output.append(',');
        }

        return output.toString();
    }

    void uploadMonitor() {
        monitor.toJSON(MonitorCloud.getMonitorRef());
    }

    int getMyScore() {
        if (GameState.whoAmI == 0) {
            return monitor.Player1Score;
        } else {
            return monitor.Player2Score;
        }
    }

    void setMyScore(int value) {
        if (GameState.whoAmI == 0) {
            monitor.Player1Score = value;
        } else {
            monitor.Player2Score = value;
        }
    }

    int getOpponentScore() {
        if (GameState.whoAmI == 1) {
            return monitor.Player1Score;
        } else {
            return monitor.Player2Score;
        }
    }

    void setOpponentScore(final int value) {
        if (GameState.whoAmI == 1) {
            monitor.Player1Score = value;
        } else {
            monitor.Player2Score = value;
        }
    }

    void endGame() {
        monitor.GameOver = true;
    }
}
