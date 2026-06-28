package com.example.expense_tracker.Entity;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Entity(name = "Expenses")
public class UserExpenseEntity {
    @NotBlank
    private String userId;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String expenseId;
    private String expenseName;
    @Positive
    private double amount;
    @NotBlank
    private String category;
    private Date date;
    private String description;

    public UserExpenseEntity(String userId, String expenseId, String expenseName,
            double amount, String category) {
        this.amount = amount;
        this.category = category;
        this.userId = userId;
        this.expenseId = expenseId;
        this.expenseName = expenseName;
    }

    public UserExpenseEntity() {

    }
}
