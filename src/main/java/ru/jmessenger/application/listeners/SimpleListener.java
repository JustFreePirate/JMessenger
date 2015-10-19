package ru.jmessenger.application.listeners;

/**
 * Created by Дмитрий on 19.10.2015.
 */
public class SimpleListener implements OnGotAMessageListener {
    private static SimpleListener ourInstance = new SimpleListener();

    public static SimpleListener getInstance() {
        return ourInstance;
    }

    private SimpleListener() {
    }

    @Override
    public void onGotAMessage(String message) {
        System.out.println(message);
    }
}
