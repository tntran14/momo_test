# Bill Payment Service - Startup Guideline

## 1) Requirements
- Java: **OpenJDK 8** (`javac 1.8.x`)
- Maven installed and available in PATH (`mvn` works)

## 2) Project entry point
The CLI shell entry point is:
- `com.assignment.shell.PaymentShell`

## 3) Start the program (recommended)
From project root (`d:/momo_test`):
```bat
mvn -q clean compile exec:java
```
You will then be able to type commands line-by-line in the running console.

## 4) Start the program without Maven (direct run)
First make sure you already built/compiled classes into `target/classes` (or compile again):
```bat
javac -source 8 -target 8 -d target/classes ^
src/main/java/com/assignment/model/BillType.java ^
src/main/java/com/assignment/model/BillState.java ^
src/main/java/com/assignment/model/Bill.java ^
src/main/java/com/assignment/repository/BillRepository.java ^
src/main/java/com/assignment/service/PaymentService.java ^
src/main/java/com/assignment/shell/PaymentShell.java
```

Then run:
```bat
java -cp target/classes com.assignment.shell.PaymentShell
```

## 5) Supported CLI commands (each command is one line)
### Account
- `CASH_IN <amount>`
  - Example: `CASH_IN 1000000`

### Bill management (simplified for this assignment)
- `CREATE_BILL <TYPE> <AMOUNT> <DUE_DATE> <PROVIDER>`
  - Example: `CREATE_BILL ELECTRIC 200000 25/10/2020 EVN`
  - TYPE supports: `ELECTRIC`, `WATER`, `INTERNET` (others map to `OTHER`)

- `DELETE_BILL <billId>`
  - Example: `DELETE_BILL 1`

- `VIEW_BILL <billId>`

- `UPDATE_BILL <billId> <TYPE> <AMOUNT> <DUE_DATE> <PROVIDER>`
  - Simplified update: delete then recreate.

### Listing / Searching
- `LIST_BILL`
- `DUE_DATE <d/M/yyyy>` (example: `DUE_DATE 25/10/2020`)
- `SEARCH_BILL_BY_PROVIDER <PROVIDER>`
- `SEARCH_BILL_BY_PROVIDERVNPT` (shortcut)

### Payments
- `PAY <billId>`
- `SCHEDULE <billId> <d/M/yyyy>`
  - Example: `SCHEDULE 2 28/10/2020`
- `LIST_PAYMENT`

### Exit
- `EXIT`
  - Prints: `Goodbye!`

## 6) Quick demo (auto-run, no need to type manually)
This will run a small script: cash in -> create 2 bills -> list -> pay bill 1 -> list payments -> exit.

```bat
cmd /c "(echo CASH_IN 1000000& echo CREATE_BILL ELECTRIC 200000 25/10/2020 EVN& echo CREATE_BILL INTERNET 800000 30/11/2020 VNPT& echo LIST_BILL& echo PAY 1& echo LIST_PAYMENT& echo EXIT) | java -cp target/classes com.assignment.shell.PaymentShell"
```

## 7) Notes about output formatting
- The program prints headers like:
  - `BillNo.TypeAmountDueDateStatePROVIDER`
  - `No.AmountPaymentDateStateBillId`
- Dates are printed with format: `d/M/yyyy` (example: `25/10/2020`).
