# momo_test
# WSL / Linux test guide (momo_test)

## 1) Install Java 8 + Maven
```bash
sudo apt update
sudo apt install -y openjdk-8-jdk maven
java -version
mvn -version
```

## 2) Clone repository
```bash
cd ~
git clone https://github.com/tntran14/momo_test.git
cd momo_test
```

## 3) Build + run (interactive shell)
### Option A (run first, then type commands)
Chương trình là **interactive** và sẽ **chờ bạn nhập lệnh**. Nên nếu bạn chạy kiểu thường và “không thấy hiện gì”, đó là bình thường.

Chạy:
```bash
mvn -q clean compile exec:java
```

Sau khi nó chạy lên và chờ input, bạn gõ lệnh từng dòng, ví dụ:
```text
CASH_IN 1000000
LIST_BILL
PAY 1
LIST_PAYMENT
EXIT
```

### Option B (show output immediately using stdin pipe)
```bash
printf "CASH_IN 1000000\nLIST_BILL\nEXIT\n" | mvn -q clean compile exec:java
```

Demo đầy đủ (tạo bill + pay + list payment):
```bash
printf "CASH_IN 1000000\nCREATE_BILL ELECTRIC 200000 25/10/2020 EVN\nCREATE_BILL INTERNET 800000 30/11/2020 VNPT\nLIST_BILL\nPAY 1\nLIST_PAYMENT\nEXIT\n" | mvn -q clean compile exec:java
```

## 4) Run unit tests (and know PASS/FAIL)
### Cách 1 (xem log chi tiết)
```bash
mvn test
```

### Cách 2 (biết PASS/FAIL qua exit code)
```bash
mvn -q test; echo $?
```
- `0` = PASS
- khác `0` = FAIL

### Cách 3 (xem report)
```bash
ls target/surefire-reports
cat target/surefire-reports/*.txt
