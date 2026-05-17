package com.assignment.repository;

import com.assignment.model.Bill;
import com.assignment.model.BillState;
import com.assignment.model.BillType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class BillRepository {
    private int nextId = 1;
    private final List<Bill> bills = new ArrayList<>();

    public int create(BillType type, long amount, LocalDate dueDate, String provider) {
        int id = nextId++;
        Bill bill = new Bill(id, type, amount, dueDate, provider, BillState.NOT_PAID);
        bills.add(bill);
        return id;
    }

    public Optional<Bill> findById(int id) {
        return bills.stream().filter(b -> b.getId() == id).findFirst();
    }

    public void deleteById(int id) {
        bills.removeIf(b -> b.getId() == id);
    }

    public List<Bill> listAll() {
        bills.sort(Comparator.comparingInt(Bill::getId));
        return new ArrayList<>(bills);
    }

    public List<Bill> searchByProvider(String provider) {
        String p = provider == null ? "" : provider.trim().toUpperCase();
        return bills.stream()
                .filter(b -> b.getProvider().trim().toUpperCase().equals(p))
                .sorted(Comparator.comparingInt(Bill::getId))
                .collect(Collectors.toList());
    }

    public void markPaid(int billId) {
        Bill bill = findByIdOrThrow(billId);
        bill.markPaid();
    }

    public void schedule(int billId) {
        Bill bill = findByIdOrThrow(billId);
        bill.markScheduled();
    }

    private Bill findByIdOrThrow(int billId) {
        return findById(billId).orElseThrow(() -> new IllegalArgumentException("Sorry!Notfoundabillwithsuchid"));
    }
}
