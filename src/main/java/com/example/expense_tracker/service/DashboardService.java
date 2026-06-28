package com.example.expense_tracker.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.expense_tracker.Entity.BudgetEntity;
import com.example.expense_tracker.Entity.UserEntity;
import com.example.expense_tracker.Entity.UserExpenseEntity;
import com.example.expense_tracker.dto.CategorySummary;
import com.example.expense_tracker.dto.DashboardResponse;
import com.example.expense_tracker.dto.InsightsResponse;
import com.example.expense_tracker.dto.MonthExpense;
import com.example.expense_tracker.dto.MonthlyReportResponse;
import com.example.expense_tracker.dto.YearlyResponse;
import com.example.expense_tracker.exceptions.ResourceNotFoundException;

@Service
public class DashboardService {
    @Autowired
    UserService userService;

    @Autowired
    UserExpenseService userExpenseService;

    @Autowired
    BudgetService budgetService;

    public DashboardResponse getDashboard(String username) {
        DashboardResponse response = new DashboardResponse();

        Optional<UserEntity> userOpt = userService.findByUsername(username);

        if (!userOpt.isPresent()) {
            throw new ResourceNotFoundException("No such user found");
        }

        // ----------------------------------------------------------------------

        UserEntity user = userOpt.get();
        String userId = user.getUserId();

        List<UserExpenseEntity> list = userExpenseService.findByUserId(userId);
        response.setTotalTransactions(list.size());

        Double totalSpent = userExpenseService.calculate(userId);

        Double totalBudget = budgetService.calculate(userId);

        double remaining = totalBudget - totalSpent;

        response.setTotalSpent(totalSpent);
        response.setRemaining(remaining);
        response.setTotalBudget(totalBudget);

        double percent = 0;

        if (totalBudget > 0) {
            percent = Math.round((totalSpent * 100) / totalBudget);
        }

        response.setPercentUtilization(percent);

        // ------------------------------------------------------------------------

        // totalBudget, totalSpent, category, percent

        double maxi = 0;
        String maxCategory = "";

        Map<String, Double> categorySumSpent = userExpenseService.categoryWiseSum(userId);

        List<BudgetEntity> budgets = budgetService.findByUserId(userId);

        List<CategorySummary> catgeories = new ArrayList<>();

        for (Map.Entry<String, Double> entry : categorySumSpent.entrySet()) {

            if (entry.getValue() > maxi) {
                maxi = entry.getValue();
                maxCategory = entry.getKey();
            }

            CategorySummary category = new CategorySummary();

            category.setCategory(entry.getKey());
            category.setTotalSpent(entry.getValue());

            double budget = 0;

            for (int i = 0; i < budgets.size(); i++) {

                if (budgets.get(i).getCategory().equals(entry.getKey())) {
                    budget = budgets.get(i).getMonthlyBudget();
                    category.setTotalBudget(budget);
                    break;
                }
            }

            double categoryPercent = 0;

            if (budget > 0) {
                categoryPercent = Math.round((entry.getValue() * 100) / budget);
            }

            category.setPercentUtilization(categoryPercent);
            category.setRemaining(budget - entry.getValue());

            String status;

            if (categoryPercent < 50)
                status = "SAFE";
            else if (categoryPercent < 90)
                status = "WARNING";
            else if (categoryPercent <= 100)
                status = "CRITICAL";
            else
                status = "EXCEEDED";

            category.setStatus(status);

            catgeories.add(category);
        }

        response.setCategories(catgeories);

        response.setHighestSpendingCategory(maxCategory);

        return response;
    }

    // {
    // "month": "June",
    // "year": 2026,

    // "totalIncome": 0,
    // "totalExpense": 21500,
    // "totalTransactions": 32,

    // "highestSpendingCategory": "Food",
    // "highestExpense": {
    // "expenseName": "Dominos",
    // "amount": 1800
    // },

    // "averageExpense": 671.87,

    // "categorySummary": [
    // ...
    // ]
    // }

    public MonthlyReportResponse monthlyReport(String username, int month, int year) {
        MonthlyReportResponse response = new MonthlyReportResponse();

        Optional<UserEntity> userOpt = userService.findByUsername(username);

        if (!userOpt.isPresent()) {
            throw new ResourceNotFoundException("No such user found");
        }

        UserEntity user = userOpt.get();
        String userId = user.getUserId();

        List<UserExpenseEntity> list = userExpenseService.findByIdMonthAndYear(userId, month, year);

        response.setMonth(month);
        response.setYear(year);
        response.setTotalTransactions(list.size());
        int sum = 0;

        for (int i = 0; i < list.size(); i++) {
            UserExpenseEntity expense = list.get(i);
            sum += expense.getAmount();
        }

        double totalBudget = budgetService.calculate(userId);

        response.setTotalExpense(sum);

        double percent = 0;

        if (totalBudget > 0) {
            percent = Math.round((sum * 100) / totalBudget);
        }

        response.setPercentUtilization(percent);

        // ----------------------------------------------------------------------------------
        double maxi = 0;
        String maxCategory = "";

        Map<String, Double> categorySumSpent = new HashMap<>();

        for (int i = 0; i < list.size(); i++) {
            UserExpenseEntity expense = list.get(i);
            if (categorySumSpent.containsKey(expense.getCategory()))
                categorySumSpent.put(expense.getCategory(),
                        expense.getAmount() + categorySumSpent.get(expense.getCategory()));
            else
                categorySumSpent.put(expense.getCategory(), expense.getAmount());
        }

        List<BudgetEntity> budgets = budgetService.findByUserId(userId);

        List<CategorySummary> catgeories = new ArrayList<>();

        for (Map.Entry<String, Double> entry : categorySumSpent.entrySet()) {

            if (entry.getValue() > maxi) {
                maxi = entry.getValue();
                maxCategory = entry.getKey();
            }

            CategorySummary category = new CategorySummary();

            category.setCategory(entry.getKey());
            category.setTotalSpent(entry.getValue());

            double budget = 0;

            for (int i = 0; i < budgets.size(); i++) {

                if (budgets.get(i).getCategory().equals(entry.getKey())) {
                    budget = budgets.get(i).getMonthlyBudget();
                    category.setTotalBudget(budget);
                    break;
                }
            }

            double categoryPercent = 0;

            if (budget > 0) {
                categoryPercent = Math.round((entry.getValue() * 100) / budget);
            }

            category.setPercentUtilization(categoryPercent);
            category.setRemaining(budget - entry.getValue());

            String status;

            if (categoryPercent < 50)
                status = "SAFE";
            else if (categoryPercent < 90)
                status = "WARNING";
            else if (categoryPercent <= 100)
                status = "CRITICAL";
            else
                status = "EXCEEDED";

            category.setStatus(status);

            catgeories.add(category);
        }

        response.setHighestSpendingCategory(Pair.of(maxCategory, maxi));

        response.setCategories(catgeories);

        return response;
    }

    public YearlyResponse yearlyReport(String username, int year) {

        YearlyResponse response = new YearlyResponse();

        Optional<UserEntity> userOpt = userService.findByUsername(username);

        if (!userOpt.isPresent()) {
            throw new ResourceNotFoundException("No such user found");
        }

        UserEntity user = userOpt.get();
        String userId = user.getUserId();

        List<UserExpenseEntity> list = userExpenseService.findByIdYear(userId, year);

        response.setYear(year);

        // ---------------------------------------------------------
        // Total Expense

        double totalExpense = 0;

        for (UserExpenseEntity expense : list) {
            totalExpense += expense.getAmount();
        }

        response.setTotalExpense(totalExpense);

        // ---------------------------------------------------------
        // Month Wise Expense

        Map<Integer, Double> monthExpenseMap = new HashMap<>();

        for (UserExpenseEntity expense : list) {

            int month = expense.getDate().toLocalDate().getMonthValue();

            monthExpenseMap.put(
                    month,
                    monthExpenseMap.getOrDefault(month, 0.0) + expense.getAmount());
        }

        List<MonthExpense> monthWiseExpense = new ArrayList<>();

        double highestMonthExpense = 0;
        String highestMonth = "";

        for (Map.Entry<Integer, Double> entry : monthExpenseMap.entrySet()) {

            MonthExpense monthExpense = new MonthExpense();

            String monthName = java.time.Month.of(entry.getKey()).name();

            monthExpense.setMonth(
                    monthName.substring(0, 1) +
                            monthName.substring(1).toLowerCase());

            monthExpense.setAmount(entry.getValue());

            monthWiseExpense.add(monthExpense);

            if (entry.getValue() > highestMonthExpense) {
                highestMonthExpense = entry.getValue();
                highestMonth = monthExpense.getMonth();
            }
        }

        response.setMonthWiseExpense(monthWiseExpense);
        response.setHighestSpendingMonth(highestMonth);

        // ---------------------------------------------------------
        // Category Wise Expense

        Map<String, Double> categorySpent = new HashMap<>();

        for (UserExpenseEntity expense : list) {

            categorySpent.put(
                    expense.getCategory(),
                    categorySpent.getOrDefault(expense.getCategory(), 0.0)
                            + expense.getAmount());
        }

        List<BudgetEntity> budgets = budgetService.findByUserId(userId);

        List<CategorySummary> categories = new ArrayList<>();

        double highestCategoryExpense = 0;
        String highestCategory = "";

        for (Map.Entry<String, Double> entry : categorySpent.entrySet()) {

            if (entry.getValue() > highestCategoryExpense) {
                highestCategoryExpense = entry.getValue();
                highestCategory = entry.getKey();
            }

            CategorySummary category = new CategorySummary();

            category.setCategory(entry.getKey());
            category.setTotalSpent(entry.getValue());

            double budget = 0;

            for (BudgetEntity b : budgets) {

                if (b.getCategory().equals(entry.getKey())) {
                    budget = b.getMonthlyBudget() * 12;
                    break;
                }
            }

            category.setTotalBudget(budget);

            category.setRemaining(budget - entry.getValue());

            double percent = 0;

            if (budget > 0) {
                percent = Math.round((entry.getValue() * 100) / budget);
            }

            category.setPercentUtilization(percent);

            String status;

            if (percent < 50)
                status = "SAFE";
            else if (percent < 90)
                status = "WARNING";
            else if (percent <= 100)
                status = "CRITICAL";
            else
                status = "EXCEEDED";

            category.setStatus(status);

            categories.add(category);
        }

        response.setHighestCategory(highestCategory);
        response.setCategorySummary(categories);

        return response;
    }

    public InsightsResponse calculateInsights(String username) {

        InsightsResponse response = new InsightsResponse();

        Optional<UserEntity> userOpt = userService.findByUsername(username);

        if (!userOpt.isPresent()) {
            throw new ResourceNotFoundException("No such user found");
        }

        UserEntity user = userOpt.get();
        String userId = user.getUserId();

        LocalDate today = LocalDate.now();

        response.setMonth(today.getMonthValue());
        response.setYear(today.getYear());

        List<UserExpenseEntity> list = userExpenseService.findByIdMonthAndYear(
                userId,
                today.getMonthValue(),
                today.getYear());

        // -------------------------------------------------------------
        // Frequency of each category

        Map<String, Integer> freq = new HashMap<>();

        // Total spent in each category

        Map<String, Double> categorySpent = new HashMap<>();

        double maxExpense = 0;
        String highestExpenseName = "";
        String highestSpendingCategory = "";

        double sum = 0;

        for (UserExpenseEntity expense : list) {

            // Frequency
            freq.put(
                    expense.getCategory(),
                    freq.getOrDefault(expense.getCategory(), 0) + 1);

            // Category-wise expense
            categorySpent.put(
                    expense.getCategory(),
                    categorySpent.getOrDefault(expense.getCategory(), 0.0)
                            + expense.getAmount());

            sum += expense.getAmount();

            // Highest single expense

            if (expense.getAmount() > maxExpense) {

                maxExpense = expense.getAmount();

                highestExpenseName = expense.getExpenseName();

                highestSpendingCategory = expense.getCategory();
            }
        }

        // -------------------------------------------------------------
        // Most frequent category

        int highestFrequency = 0;
        String mostFrequentCategory = "";

        for (Map.Entry<String, Integer> entry : freq.entrySet()) {

            if (entry.getValue() > highestFrequency) {

                highestFrequency = entry.getValue();
                mostFrequentCategory = entry.getKey();
            }
        }

        response.setMostFrequentCategory(mostFrequentCategory);

        response.setHighestSpendingCategory(highestSpendingCategory);

        response.setHighestExpense(Pair.of(highestExpenseName, maxExpense));
        // -------------------------------------------------------------
        // Projected month-end expense

        double projectedMonthEndExpense = 0;

        if (today.getDayOfMonth() > 0) {

            projectedMonthEndExpense = (sum / (double) today.getDayOfMonth())
                    * today.lengthOfMonth();
        }

        response.setProjectedMonthEndExpense(
                Math.round(projectedMonthEndExpense));

        // -------------------------------------------------------------
        // Budget Risk

        List<BudgetEntity> budgets = budgetService.findByUserId(userId);

        int daysElapsed = today.getDayOfMonth();
        int totalDays = today.lengthOfMonth();

        List<String> projectedCategoryInsights = new ArrayList<>();

        for (Map.Entry<String, Double> entry : categorySpent.entrySet()) {

            double monthlyBudget = 0;

            for (BudgetEntity budget : budgets) {

                if (budget.getCategory().equalsIgnoreCase(entry.getKey())) {

                    monthlyBudget = budget.getMonthlyBudget();
                    break;
                }
            }

            double projectedExpense = (entry.getValue() / daysElapsed) * totalDays;

            if (monthlyBudget > 0) {

                double difference = projectedExpense - monthlyBudget;
                double percentDifference = (difference * 100.0) / monthlyBudget;

                if (projectedExpense > monthlyBudget) {

                    projectedCategoryInsights.add(
                            entry.getKey()
                                    + " is projected to exceed its budget by "
                                    + Math.round(percentDifference)
                                    + "% (₹"
                                    + Math.round(difference)
                                    + ").");

                } else {

                    projectedCategoryInsights.add(
                            entry.getKey()
                                    + " is projected to stay within budget with an estimated spend of ₹"
                                    + Math.round(projectedExpense)
                                    + ".");
                }

            } else {

                projectedCategoryInsights.add(
                        entry.getKey()
                                + " is projected to reach ₹"
                                + Math.round(projectedExpense)
                                + " this month.");
            }
        }

        response.setBudgetRisk(projectedCategoryInsights);

        return response;
    }
}
