package com.assignment.service;

import com.assignment.model.Bill;
import com.assignment.model.BillState;
import com.assignment.model.BillType;
import com.assignment.repository.BillRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class PaymentService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("d/M/uuuu");

    private final BillRepository billRepository = new BillRepository();

    private long availableBalance;

    private final PaymentRepository paymentRepository = new PaymentRepository();

    public long getAvailableBalance() {
        return availableBalance;
    }

    public void cashIn(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        availableBalance += amount;
    }

    // create/update/view/delete/search
    public int createBill(String serviceTypeToken, long amount, String dueDateToken, String provider) {
        BillType type = BillType.fromToken(serviceTypeToken);
        LocalDate dueDate = parseDate(dueDateToken);
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        return billRepository.create(type, amount, dueDate, provider);
    }

    public void deleteBill(int billId) {
        billRepository.deleteById(billId);
    }

    public void viewBillToConsole(int billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Sorry!Notfoundabillwithsuchid"));
        printBillsHeader();
        printBillRow(bill);
    }

    public void updateBill(int billId, String serviceTypeToken, long amount, String dueDateToken, String provider) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        BillType type = BillType.fromToken(serviceTypeToken);
        LocalDate dueDate = parseDate(dueDateToken);

        // Simplified update for interview: delete then recreate.
        billRepository.deleteById(billId);
        billRepository.create(type, amount, dueDate, provider);
    }

    public void listBillsToConsole() {
        List<Bill> bills = billRepository.listAll();
        printBillsHeader();
        for (Bill bill : bills) {
            printBillRow(bill);
        }
    }

    public void listBillsDueDateToConsole(String dateToken) {
        LocalDate dueDate = parseDate(dateToken);
        List<Bill> matched = new ArrayList<>();
        for (Bill b : billRepository.listAll()) {
            if (b.getDueDate().equals(dueDate)) matched.add(b);
        }
        matched.sort(Comparator.comparingInt(Bill::getId));
        printBillsHeader();
        for (Bill bill : matched) {
            printBillRow(bill);
        }
    }

    public void searchBillsByProviderToConsole(String providerToken) {
        List<Bill> matched = billRepository.searchByProvider(providerToken);
        printBillsHeader();
        for (Bill bill : matched) {
            printBillRow(bill);
        }
    }

    public void payBill(int billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Sorry!Notfoundabillwithsuchid"));

        if (bill.getState() == BillState.PAID) {
            throw new IllegalArgumentException("Bill is already paid");
        }

        if (availableBalance < bill.getAmount()) {
            throw new IllegalArgumentException("Sorry!Notenoughfundtoproceedwithpayment.");
        }

        availableBalance -= bill.getAmount();
        billRepository.markPaid(billId);

        paymentRepository.addPayment(billId, bill.getAmount(), LocalDate.now(), "PROCESSED");
        System.out.println("PaymenthasbeencompletedforBillwithid" + billId + ".");
        System.out.println("Yourcurrentbalanceis:" + availableBalance);

        processScheduledDueOn(LocalDate.now());
    }

    public void schedulePayment(int billId, String dateToken) {
        LocalDate scheduleDate = parseDate(dateToken);

        billRepository.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Sorry!Notfoundabillwithsuchid"));

        billRepository.schedule(billId);
        paymentRepository.addScheduled(billId, parseBillAmount(billId), scheduleDate);

        System.out.println("Paymentforbillid" + billId + "isscheduledon" + dateToken);
    }

    public void listPaymentsToConsole() {
        List<PaymentView> views = paymentRepository.listAllViews();
        if (views.isEmpty()) {
            System.out.println("No.AmountPaymentDateStateBillId");
            return;
        }
        System.out.println("No.AmountPaymentDateStateBillId");
        for (int i = 0; i < views.size(); i++) {
            PaymentView v = views.get(i);
            System.out.println((i + 1) + "." + v.amount + v.date + v.state + v.billId);
        }
    }

    // ---- helpers ----

    private LocalDate parseDate(String token) {
        return LocalDate.parse(token.trim(), DATE_FMT);
    }

    private void printBillsHeader() {
        System.out.println("BillNo.TypeAmountDueDateStatePROVIDER");
    }

    private void printBillRow(Bill bill) {
        String stateToken = bill.getState().name();
        System.out.println(
                bill.getId() + "."
                        + bill.getType().name() + bill.getAmount()
                        + bill.getDueDate().format(DATE_FMT) + stateToken
                        + bill.getProvider().toUpperCase()
        );
    }

    private long parseBillAmount(int billId) {
        return billRepository.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Sorry!Notfoundabillwithsuchid"))
                .getAmount();
    }

    private void processScheduledDueOn(LocalDate date) {
        List<ScheduledPayment> due = paymentRepository.findScheduledDueOn(date);
        if (due.isEmpty()) return;

        due.sort(Comparator.comparing(ScheduledPayment::getDate).thenComparing(ScheduledPayment::getBillId));

        for (ScheduledPayment sp : due) {
            if (availableBalance >= sp.amount) {
                availableBalance -= sp.amount;
                billRepository.markPaid(sp.billId);
                paymentRepository.markProcessed(sp.paymentId, LocalDate.now());
            }
        }
    }

    // ---- internal payment store ----

    private static final class PaymentRepository {
        private int nextPaymentId = 1;

        private final List<PaymentRecord> processedPayments = new ArrayList<>();
        private final List<ScheduledPayment> scheduledPayments = new ArrayList<>();

        void addPayment(int billId, long amount, LocalDate date, String paidState) {
            processedPayments.add(new PaymentRecord(nextPaymentId++, amount, date.format(DATE_FMT), paidState, billId));
        }

        void addScheduled(int billId, long amount, LocalDate scheduleDate) {
            scheduledPayments.add(new ScheduledPayment(nextPaymentId++, billId, amount, scheduleDate));
        }

        List<PaymentView> listAllViews() {
            List<PaymentView> out = new ArrayList<>();
            for (PaymentRecord p : processedPayments) {
                out.add(new PaymentView(p.amount, p.date, p.state, p.billId));
            }
            for (ScheduledPayment sp : scheduledPayments) {
                out.add(new PaymentView(sp.amount, sp.date.format(DATE_FMT), "PENDING", sp.billId));
            }
            out.sort(Comparator.comparingInt(PaymentView::getBillId));
            return out;
        }

        List<ScheduledPayment> findScheduledDueOn(LocalDate date) {
            List<ScheduledPayment> out = new ArrayList<>();
            for (ScheduledPayment sp : scheduledPayments) {
                if (sp.date.equals(date)) out.add(sp);
            }
            return out;
        }

        void markProcessed(int scheduledPaymentId, LocalDate processedDate) {
            ScheduledPayment match = null;
            for (ScheduledPayment sp : scheduledPayments) {
                if (sp.paymentId == scheduledPaymentId) {
                    match = sp;
                    break;
                }
            }
            if (match == null) return;

            scheduledPayments.remove(match);
            processedPayments.add(new PaymentRecord(
                    match.paymentId,
                    match.amount,
                    processedDate.format(DATE_FMT),
                    "PROCESSED",
                    match.billId
            ));
        }
    }

    private static final class PaymentRecord {
        final int paymentId;
        final long amount;
        final String date;
        final String state;
        final int billId;

        PaymentRecord(int paymentId, long amount, String date, String state, int billId) {
            this.paymentId = paymentId;
            this.amount = amount;
            this.date = date;
            this.state = state;
            this.billId = billId;
        }
    }

    private static final class ScheduledPayment {
        final int paymentId;
        final int billId;
        final long amount;
        final LocalDate date;

        ScheduledPayment(int paymentId, int billId, long amount, LocalDate date) {
            this.paymentId = paymentId;
            this.billId = billId;
            this.amount = amount;
            this.date = date;
        }

        int getBillId() { return billId; }

        LocalDate getDate() { return date; }

        int getPaymentId() { return paymentId; }
    }

    private static final class PaymentView {
        final long amount;
        final String date;
        final String state;
        final int billId;

        PaymentView(long amount, String date, String state, int billId) {
            this.amount = amount;
            this.date = date;
            this.state = state;
            this.billId = billId;
        }

        int getBillId() { return billId; }
    }
}
