package com.example.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategorySummary {
    private double totalSpent;
    private double totalBudget;
    private double remaining;
    private double percentUtilization;
    private String category;
    private String status;
}
