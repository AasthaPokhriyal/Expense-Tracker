package com.example.expense_tracker.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearlyResponse {

    private int year;

    private double totalExpense;

    private List<MonthExpense> monthWiseExpense;

    private String highestSpendingMonth;

    private String highestCategory;

    private List<CategorySummary> categorySummary;

}