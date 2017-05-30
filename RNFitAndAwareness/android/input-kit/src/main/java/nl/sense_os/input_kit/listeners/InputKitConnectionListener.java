package nl.sense_os.input_kit.listeners;

/**
 * Created by panjiyudasetya on 5/30/17.
 */

public interface InputKitConnectionListener {
    void onInputKitIsAccessible();
    void onInputKitIsNotAccessible(String reason);
}
