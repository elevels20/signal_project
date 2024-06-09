package com.alerts;

public class RepeatedAlertDecorator extends AlertDecorator {
    private int repeatCount;
    private long interval;

    public RepeatedAlertDecorator(Alert decoratedAlert, int repeatCount, long interval) {
        super(decoratedAlert);
        this.repeatCount = repeatCount;
        this.interval = interval;
    }

    @Override
    public String getAlertMessage() {
        StringBuilder message = new StringBuilder(super.getAlertMessage());
        message.append("\nRepeated ").append(repeatCount).append(" times at intervals of ").append(interval).append(" ms.");
        return message.toString();
    }

    public void checkAndRepeat(AlertGenerator alertGenerator) {
        for (int i = 0; i < repeatCount; i++) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            alertGenerator.triggerAlert(this);
        }
    }
}
