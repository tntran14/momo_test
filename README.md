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
```bash
mvn -q clean compile exec:java
```

When it starts, type commands (1 command per line), then `EXIT` to stop. Example:
```text
CASH_IN 1000000
CREATE_BILL ELECTRIC 200000 25/10/2020 EVN
CREATE_BILL INTERNET 800000 30/11/2020 VNPT
LIST_BILL
PAY 1
LIST_PAYMENT
EXIT
```

## 4) Run unit tests (optional)
```bash
mvn -q test

