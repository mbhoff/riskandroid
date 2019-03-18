package edu.sdsmt.team4.MobileProject2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

class Monitor implements Serializable {
    String Activity = ""; // example : "Capture Activity"
    String CapturePiece = ""; // example: "Dot"
    int PlayerTurn = 0;
    int Player1Score = 0;
    int Player2Score = 0;
    private boolean GameStarted = false;
    boolean GameOver = false;
    private int TimeCounter;

    Monitor() { }

    boolean isGameStarted() {
        return GameStarted;
    }

    void startGame() {
        GameStarted = true;
    }

    int getPlayerTurn() {
        return PlayerTurn;
    }

    void setPlayerTurn(int playerTurn) {
        PlayerTurn = playerTurn;
    }

    void toJSON(DatabaseReference dataSnapshot) {
        dataSnapshot.child("Activity").setValue(this.Activity);
        dataSnapshot.child("Capture_Piece").setValue(this.CapturePiece);
        dataSnapshot.child("GameStarted").setValue(this.GameStarted);
        dataSnapshot.child("GameOver").setValue(this.GameOver);
        dataSnapshot.child("Player1_Score").setValue(this.Player1Score);
        dataSnapshot.child("Player2_Score").setValue(this.Player2Score);
        dataSnapshot.child("Player_Turn").setValue(this.PlayerTurn);
        dataSnapshot.child("Time").setValue(this.TimeCounter);
    }

    static Monitor fromJSON(DataSnapshot snapshot) {
        // check snapshot and necessary children exist
        if (!snapshot.exists()) return null;
        if (!snapshot.hasChild("Activity")) return null;
        if (!snapshot.hasChild("Capture_Piece")) return null;
        if (!snapshot.hasChild("GameStarted")) return null;
        if (!snapshot.hasChild("GameOver")) return null;
        if (!snapshot.hasChild("Player1_Score")) return null;
        if (!snapshot.hasChild("Player2_Score")) return null;
        if (!snapshot.hasChild("Player_Turn")) return null;
        if (!snapshot.hasChild("Time")) return null;

        Monitor newMonitor = new Monitor();

        newMonitor.Activity = snapshot.child("Activity").getValue().toString();
        newMonitor.CapturePiece = snapshot.child("Capture_Piece").getValue().toString();
        newMonitor.GameStarted = (boolean) snapshot.child("GameStarted").getValue();
        newMonitor.GameOver = (boolean) snapshot.child("GameOver").getValue();
        newMonitor.Player1Score = Integer.parseInt(snapshot.child("Player1_Score").getValue().toString());
        newMonitor.Player2Score = Integer.parseInt(snapshot.child("Player2_Score").getValue().toString());
        newMonitor.PlayerTurn = Integer.parseInt(snapshot.child("Player_Turn").getValue().toString());
        newMonitor.TimeCounter = Integer.parseInt(snapshot.child("Time").getValue().toString());

        return newMonitor;
    }
}
