package com.example.expense_tracker.dto;

import java.sql.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ExpenseInput {

    @NotBlank
    private String expenseName;

    @Positive
    private double amount;

    @NotBlank
    private String category;

    private Date date;

    private String description;
}