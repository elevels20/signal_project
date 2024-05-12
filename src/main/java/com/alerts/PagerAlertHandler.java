package com.alerts;

public class PagerAlertHandler implements AlertHandler{
    private Pager pager;

    public PagerAlertHandler(Pager pager){
        this.pager = pager;
    }

    @Override
    public void handleAlert(Alert alert) {
        String message = String.format("Alert: Patient ID %s - %s at %d", alert.getPatientId(), alert.getCondition(), alert.getTimestamp());

        pager.sendNotification(message);
    }
}
