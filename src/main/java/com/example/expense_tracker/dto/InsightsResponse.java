package com.example.expense_tracker.dto;

import java.util.List;

import org.springframework.data.util.Pair;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsightsResponse {
    private int month;
    private int year;
    private String highestSpendingCategory;
    private Pair<String, Double> highestExpense;
    private String mostFrequentCategory;
    private double projectedMonthEndExpense;
    private List<String> budgetRisk;
}
