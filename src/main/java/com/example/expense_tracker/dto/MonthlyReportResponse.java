package com.example.expense_tracker.dto;

import java.util.List;

import org.springframework.data.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyReportResponse {
    private int totalTransactions;
    private double totalExpense;
    private List<CategorySummary> categories;
    private double budgetUtilization;
    private Pair<String, Double> highestSpendingCategory;
    private int month;
    private int year;
    private double percentUtilization;
}
