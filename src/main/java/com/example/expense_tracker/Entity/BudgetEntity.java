package com.example.expense_tracker.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Entity(name = "Budget")
@Data
public class BudgetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String budgetId;
    @NotBlank
    private String userId;
    @NotBlank
    private String category;
    @Positive
    private double monthlyBudget;

    public BudgetEntity() {
    }
}
