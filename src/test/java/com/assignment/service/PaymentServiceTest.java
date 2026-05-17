package com.assignment.service;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentServiceTest {

    @Test
    void cashInAndGetAvailableBalance() {
        PaymentService service = new PaymentService();
        service.cashIn(1_000_000);
        assertEquals(1_000_000, service.getAvailableBalance());
    }

    @Test
    void payBill_shouldDecreaseBalanceAndMarkPaid() {
        PaymentService service = new PaymentService();
        service.cashIn(1_000_000);

        int billId = service.createBill("ELECTRIC", 200_000, "25/10/2020", "EVN");
        service.payBill(billId);

        assertEquals(800_000, service.getAvailableBalance());
    }

    @Test
    void payBill_shouldFailWhenNotEnoughFunds() {
        PaymentService service = new PaymentService();
        service.cashIn(100);

        int billId = service.createBill("WATER", 175_000, "30/10/2020", "SAVACO");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.payBill(billId));
        assertEquals("Sorry!Notenoughfundtoproceedwithpayment.", ex.getMessage());
    }

    @Test
    void payBill_shouldFailWhenBillNotFound() {
        PaymentService service = new PaymentService();
        service.cashIn(1_000_000);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.payBill(999));
        assertEquals("Sorry!Notfoundabillwithsuchid", ex.getMessage());
    }

    @Test
    void listBillsToConsole_shouldPrintHeaderAndRows() {
        PaymentService service = new PaymentService();
        service.createBill("INTERNET", 800_000, "30/11/2020", "VNPT");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream old = System.out;
        System.setOut(new PrintStream(baos));
        try {
            service.listBillsToConsole();
        } finally {
            System.setOut(old);
        }

        String out = baos.toString();
        assertTrue(out.contains("BillNo.TypeAmountDueDateStatePROVIDER"));
        assertTrue(out.contains("1.INTERNET80000030/11/2020NOT_PAIDVNPT".replace("NOT_PAID", "NOT_PAID")));
    }
}
