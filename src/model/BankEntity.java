package model;

import java.io.Serializable;
import java.util.UUID;

public class BankEntity implements Serializable {
    protected String id;
    protected String name;

    public BankEntity() {
        this.id = UUID.randomUUID().toString();
    }

    public BankEntity(String name) {
        this();
        this.name =  name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
