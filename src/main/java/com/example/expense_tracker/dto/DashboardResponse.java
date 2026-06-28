package com.example.expense_tracker.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private double totalSpent;
    private double totalBudget;
    private double remaining;
    private double percentUtilization;
    private int totalTransactions;
    private String highestSpendingCategory;
    private List<CategorySummary> categories;
}
