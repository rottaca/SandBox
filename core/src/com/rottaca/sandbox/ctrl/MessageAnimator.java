package com.rottaca.sandbox.ctrl;

import java.util.ArrayList;

/**
 * Created by Andreas on 17.09.2016.
 */
public class MessageAnimator {
    private class Message {
        String message;
        long startTimeMs;
        long durationMs;
        boolean started;
    }

    ArrayList<Message> messages;

    public MessageAnimator() {
        messages = new ArrayList<Message>();
    }

    public synchronized String getMessageAndUpdate() {
        if (messages.size() == 0)
            return null;

        Message m = messages.get(0);
        if (!m.started) {
            m.started = true;
            m.startTimeMs = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() - m.startTimeMs > m.durationMs)
            messages.remove(m);

        return m.message;
    }

    public synchronized void addMessage(String message, long durationMs) {
        Message m = new Message();
        m.startTimeMs = 0;
        m.durationMs = durationMs;
        m.message = message;
        m.started = false;
        messages.add(m);
    }

    public synchronized void clearMessageQueue() {
        messages.clear();
    }
}
