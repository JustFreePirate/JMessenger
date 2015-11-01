package ru.jmessenger.application.listeners;

/**
 * Created by Дмитрий on 19.10.2015.
 */
public class SimpleListener implements OnGotAMessageListener {
    private static final SimpleListener OUR_INSTANCE = new SimpleListener();

    public static SimpleListener getInstance() {
        return OUR_INSTANCE;
    }

    private SimpleListener() {
    }

    @Override
    public void onGotAMessage(String message) {
        System.out.println(message);
    }
}
