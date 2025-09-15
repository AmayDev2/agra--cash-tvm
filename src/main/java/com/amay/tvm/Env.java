package com.amay.tvm;

public interface Env {
    // ---------- Ticket limits (from your snippet) ----------
    // SJT limits
    public static final int MAX_TICKET_SJT = 10;
    public static final int MIN_TICKET_SJT = 1;

    // RJT limits
    public static final int MAX_TICKET_RJT = 10;
    public static final int MIN_TICKET_RJT = 1;

    // GT limits
    public static final int MAX_TICKET_GT = 40;
    public static final int MIN_TICKET_GT = 10;

    public static final int FARE_PER_SJT = 20;
    public static final int FARE_PER_RJT = 35;
    public static final int FARE_PER_GT  = 50;

    int hop1=5;
    int hop2=10;
    int hop3=10;
}
