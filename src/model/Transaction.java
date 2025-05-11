package model;

import java.util.Date;

public class Transaction extends BankEntity {
    private String type;
    private double amount;
    private String sendingAccountId;
    private String receivingAccountId;
    private Date timeStamp;

    public Transaction(String type, double amount, String sendingAccountId, String receivingAccountId, Date timeStamp) {
        this.type = type;
        this.amount = amount;
        this.sendingAccountId = sendingAccountId;
        this.receivingAccountId = receivingAccountId;
        this.timeStamp = timeStamp;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getSendingAccountId() {
        return sendingAccountId;
    }

    public String getReceivingAccountId() {
        return receivingAccountId;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", sourceAccountId='" + sendingAccountId + '\'' +
                ", targetAccountId='" + receivingAccountId + '\'' +
                ", timestamp=" + timeStamp +
                '}';
    }

}
