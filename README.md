# Bill Payment Service - Startup Guideline

## 0) Clone from GitHub (so người khác dùng được)
```bat
git clone https://github.com/tntran14/momo_test.git
cd momo_test
```

## 1) Requirements
- Java: **OpenJDK 8** (`javac 1.8.x`)
- Maven installed and available in PATH (`mvn` works)

## 2) Project entry point
The CLI shell entry point is:
- `com.assignment.shell.PaymentShell`

## 3) Start the program (recommended - Maven)
From project root (`momo_test`):
```bat
mvn -q clean compile exec:java
```
Then bạn gõ lệnh vào console (mỗi lệnh 1 dòng).

## 4) Start without Maven (direct run)
First compile sources into `target/classes` using Java 8:
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

### Bill management
- `CREATE_BILL <TYPE> <AMOUNT> <DUE_DATE> <PROVIDER>`
  - Example: `CREATE_BILL ELECTRIC 200000 25/10/2020 EVN`
  - TYPE supports: `ELECTRIC`, `WATER`, `INTERNET` (others map to `OTHER`)

- `DELETE_BILL <billId>`
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
- `SCHEDULE <billId> <d/M/yyyy>` (example: `SCHEDULE 2 28/10/2020`)
- `LIST_PAYMENT`

### Exit
- `EXIT`
  - Prints: `Goodbye!`

## 6) Quick demo (auto-run, no need to type manually)
This runs: cash in -> create 2 bills -> list -> pay bill 1 -> list payments -> exit.

```bat
cmd /c "(echo CASH_IN 1000000& echo CREATE_BILL ELECTRIC 200000 25/10/2020 EVN& echo CREATE_BILL INTERNET 800000 30/11/2020 VNPT& echo LIST_BILL& echo PAY 1& echo LIST_PAYMENT& echo EXIT) | mvn -q exec:java"
```

## 7) Output headers (expected)
- Bills header: `BillNo.TypeAmountDueDateStatePROVIDER`
- Payments header: `No.AmountPaymentDateStateBillId`
