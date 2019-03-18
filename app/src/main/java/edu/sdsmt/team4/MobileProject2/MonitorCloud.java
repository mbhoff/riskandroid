package edu.sdsmt.team4.MobileProject2;


import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


class MonitorCloud {
    // Firebase instance variables
    private static FirebaseAuth userAuth;
    private static FirebaseUser firebaseUser;
    private static FirebaseDatabase firebase;
    private static DatabaseReference monitor_ref;
    private static DatabaseReference game_ref;

    static private final String game_state_location = "Game_State";
    static private final String monitor_state_location = "Monitor";
    static private GameState game_state = null;
    static private Monitor monitor_state = null;

    private static boolean authenticated = false;
    private static boolean internet = false;


    /**
     *  private to defeat instantiation.
     */
    private MonitorCloud() {
        userAuth = FirebaseAuth.getInstance();
        startAuthListening();
        startConnectionListener();
    }


    static boolean isAuthenticated(){
        return authenticated;
    }
    static boolean isConnected() {
        return internet;
    }
    static String getUserName() {
        return firebaseUser != null ? firebaseUser.getEmail() : "";
    }
    public static String getUserID(){
        return firebaseUser != null ? firebaseUser.getUid() : "";
    }


    /**
     * Tries to create a new user.
     * @param email user's email
     * @param password user's password
     * @param act activity to call CreateUserSuccess or SignInFail from
     */
    static void createUser(String email, String password, final CreateAccountActivity act) {
        userAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                act.CreateUserSuccess();
            } else {
                act.CreateUserFail(task.getException().getMessage());
            }
        });
    }

    /**
     * Sign in function for the Login Activity.
     * @param email user's email
     * @param password user's password
     * @param act activity to call CreateUserSuccess or SignInFail from
     */
    static void signIn(String email, String password, final LoginActivity act) {
        userAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebase = FirebaseDatabase.getInstance();
                game_ref = firebase.getReference(game_state_location);
                monitor_ref = firebase.getReference(monitor_state_location);

                //startGameStateListener();
                //startMonitorListener();

                act.SignInSuccess();
            } else {
                act.SignInFail(task.getException().getMessage());
            }
        });
    }

    /**
     * Starts up a thread to monitor when a user is authenticated.
     */
    private void startAuthListening() {
        userAuth.addAuthStateListener(firebaseAuth -> {
            firebaseUser = firebaseAuth.getCurrentUser();
            authenticated = firebaseUser != null;
        });
    }

    /**
     * Starts up a thread to monitor if we are connected to the internet.
     */
    private void startConnectionListener() {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                internet = snapshot.getValue(Boolean.class);

                if (!internet) {
                    // the time out on the server for wifi lost is pretty long (up to 5 min!)
                    // this forces a recheck much sooner
                    FirebaseDatabase.getInstance().goOnline();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                internet = false;
            }
        });
    }

    /**
     * Starts up a thread to monitor when the game monitor_state is updated.
     */
    static private void startGameStateListener() {
        // Attach a listener to read the data at our posts reference
        game_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                game_state = GameState.fromJSON(snapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("game_state data", "The read failed: " + databaseError.getCode());
            }
        });
    }

    /**
     * Starts up a thread to monitor when the game monitor_state is updated.
     */
    static private void startMonitorListener() {
        // Attach a listener to read the data at our posts reference
        monitor_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                monitor_state = Monitor.fromJSON(snapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("monitor_state data", "The read failed: " + databaseError.getCode());
            }
        });
    }

    static DatabaseReference getGameRef() {
        return game_ref;
    }

    static DatabaseReference getMonitorRef() {
        return monitor_ref;
    }

    /**
     * @return The latest monitor_state.
     */
    static Monitor getMonitor() {
        return monitor_state;
    }

    /**
     * Stores a Monitor in the database.
     */
    static void putMonitor(final Monitor state) {
        state.toJSON(monitor_ref);
    }

    /**
     * @return The latest game_state.
     */
    static GameState updateGamestate(){
        return game_state;
    }

    /**
     * Stores the monitor_state of the game, not including stuff in the Monitor, to the database.
     */
    static void uploadGame(final GameState gs) {
        game_ref.child("Captured").setValue(gs.writeCaptured());
        game_ref.child("Current_Round").setValue(gs.getCurrRound());
        game_ref.child("Total_Rounds").setValue(gs.getRounds());
    }
}
