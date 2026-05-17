package com.assignment.shell;

import com.assignment.service.PaymentService;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public final class PaymentShell {

    private final PaymentService paymentService;

    public PaymentShell(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public static void main(String[] args) throws Exception {
        PaymentService service = new PaymentService();
        PaymentShell shell = new PaymentShell(service);
        shell.run();
    }

    public void run() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;

        while (true) {
            line = reader.readLine();
            if (line == null) {
                return;
            }
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\s+");
            String command = parts[0].toUpperCase();

            try {
                if ("CASH_IN".equals(command)) {
                    handleCashIn(parts);
                } else if ("CREATE_BILL".equals(command)) {
                    handleCreateBill(parts);
                } else if ("DELETE_BILL".equals(command)) {
                    handleDeleteBill(parts);
                } else if ("VIEW_BILL".equals(command)) {
                    handleViewBill(parts);
                } else if ("UPDATE_BILL".equals(command)) {
                    handleUpdateBill(parts);
                } else if ("LIST_BILL".equals(command)) {
                    paymentService.listBillsToConsole();
                } else if ("DUE_DATE".equals(command)) {
                    paymentService.listBillsDueDateToConsole(parts[1]);
                } else if ("PAY".equals(command)) {
                    handlePay(parts);
                } else if ("SCHEDULE".equals(command)) {
                    handleSchedule(parts);
                } else if ("LIST_PAYMENT".equals(command)) {
                    paymentService.listPaymentsToConsole();
                } else if ("SEARCH_BILL_BY_PROVIDER".equals(command)) {
                    paymentService.searchBillsByProviderToConsole(parts[1]);
                } else if ("SEARCH_BILL_BY_PROVIDERVNPT".equals(command)) {
                    paymentService.searchBillsByProviderToConsole("VNPT");
                } else if ("EXIT".equals(command)) {
                    System.out.println("Goodbye!");
                    return;
                } else {
                    System.out.println("Unknown command");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void handleCashIn(String[] parts) {
        long amount = Long.parseLong(parts[1]);
        paymentService.cashIn(amount);
        System.out.println("Your available balance:" + paymentService.getAvailableBalance());
    }

    private void handleCreateBill(String[] parts) {
        String type = parts[1];
        long amount = Long.parseLong(parts[2]);
        String dueDate = parts[3];
        String provider = parts[4];
        int billId = paymentService.createBill(type, amount, dueDate, provider);
        System.out.println("Billcreatedwithid" + billId);
    }

    private void handleDeleteBill(String[] parts) {
        int billId = Integer.parseInt(parts[1]);
        paymentService.deleteBill(billId);
    }

    private void handleViewBill(String[] parts) {
        int billId = Integer.parseInt(parts[1]);
        paymentService.viewBillToConsole(billId);
    }

    private void handleUpdateBill(String[] parts) {
        int billId = Integer.parseInt(parts[1]);
        String type = parts[2];
        long amount = Long.parseLong(parts[3]);
        String dueDate = parts[4];
        String provider = parts[5];
        paymentService.updateBill(billId, type, amount, dueDate, provider);
    }

    private void handlePay(String[] parts) {
        int billId = Integer.parseInt(parts[1]);
        paymentService.payBill(billId);
    }

    private void handleSchedule(String[] parts) {
        int billId = Integer.parseInt(parts[1]);
        String date = parts[2];
        paymentService.schedulePayment(billId, date);
    }
}
