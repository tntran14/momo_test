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

            // Support both formats:
            //  - "CASH_IN 1000000" (space separated)
            //  - "CASH_IN1000000"  (command + param glued)
            String[] parts = line.split("\\s+");
            String raw = parts[0].toUpperCase();
            String command;
            String[] args;

            String trimmedUpper = line.toUpperCase().replaceAll("\\s+", "");
            // Try to parse command-without-spaces first (longest match by known command prefixes)
            if (trimmedUpper.startsWith("CASH_IN")) {
                command = "CASH_IN";
                args = new String[] { line.substring(command.length()) };
            } else if (trimmedUpper.startsWith("PAY")) {
                command = "PAY";
                args = new String[] { line.substring(command.length()) };
            } else if (trimmedUpper.startsWith("SCHEDULE")) {
                command = "SCHEDULE";
                // format: SCHEDULE<billId><space?>/<date> -> we will parse later
                args = new String[] { line.substring(command.length()) };
            } else {
                command = raw;
                args = new String[parts.length - 1];
                for (int i = 1; i < parts.length; i++) {
                    args[i - 1] = parts[i];
                }
            }

            try {
                if ("CASH_IN".equals(command)) {
                    handleCashIn(args);
                } else if ("CREATE_BILL".equals(command)) {
                    // CREATE_BILL expects space-separated tokens
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
                    handlePay(args);
                } else if ("SCHEDULE".equals(command)) {
                    handleSchedule(args);
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
        // parts = [amount] (for glued format) or [amount] (space format also works if you pass it that way)
        if (parts.length < 1) throw new IllegalArgumentException("CASH_IN requires amount");
        long amount = Long.parseLong(parts[0].trim());
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
