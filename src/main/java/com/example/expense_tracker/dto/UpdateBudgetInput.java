package com.example.expense_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateBudgetInput {

    @NotBlank
    private String category;

    @Positive
    private double monthlyBudget;
}