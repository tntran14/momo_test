package com.assignment.model;

import java.time.LocalDate;
import java.util.Objects;

public final class Bill {
    private final int id;
    private final BillType type;
    private final long amount;
    private final LocalDate dueDate;
    private final String provider;
    private BillState state;

    public Bill(int id, BillType type, long amount, LocalDate dueDate, String provider, BillState state) {
        this.id = id;
        this.type = Objects.requireNonNull(type, "type");
        this.amount = amount;
        this.dueDate = Objects.requireNonNull(dueDate, "dueDate");
        this.provider = Objects.requireNonNull(provider, "provider");
        this.state = Objects.requireNonNull(state, "state");
    }

    public int getId() {
        return id;
    }

    public BillType getType() {
        return type;
    }

    public long getAmount() {
        return amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getProvider() {
        return provider;
    }

    public BillState getState() {
        return state;
    }

    public void markPaid() {
        this.state = BillState.PAID;
    }

    public void markScheduled() {
        this.state = BillState.SCHEDULED;
    }
}
