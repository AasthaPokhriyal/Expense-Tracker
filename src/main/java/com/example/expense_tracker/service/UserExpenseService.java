package com.example.expense_tracker.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.expense_tracker.Entity.BudgetEntity;
import com.example.expense_tracker.Entity.UserEntity;
import com.example.expense_tracker.Entity.UserExpenseEntity;
import com.example.expense_tracker.Repository.BudgetRepo;
import com.example.expense_tracker.Repository.UserExpenseRepo;
import com.example.expense_tracker.dto.ExpenseInput;
import com.example.expense_tracker.exceptions.ResourceNotFoundException;

@Service
public class UserExpenseService {
    @Autowired
    UserExpenseRepo userExpenseRepo;
    UserExpenseEntity userExpenseEntity;
    BudgetEntity budgetEntity;
    @Autowired
    BudgetRepo budgetRepo;

    @Autowired
    UserService userService;

    public List<UserExpenseEntity> getAllExpenses() {
        return userExpenseRepo.findAll();
    }

    public UserExpenseEntity addExpense(String expenseId, ExpenseInput updatedInput) {
        Optional<UserExpenseEntity> expenseOpt = userExpenseRepo.findById(expenseId);
        if (!expenseOpt.isPresent()) {
            return null;
        }

        UserExpenseEntity expense = expenseOpt.get();

        expense.setUserId(expense.getUserId());
        expense.setExpenseName(updatedInput.getExpenseName());
        expense.setAmount(updatedInput.getAmount());
        expense.setCategory(updatedInput.getCategory());
        expense.setDate(updatedInput.getDate());
        expense.setDescription(updatedInput.getDescription());

        return userExpenseRepo.saveAndFlush(expense);
    }

    public UserExpenseEntity addNewExpense(
            Authentication authentication,
            ExpenseInput input) {

        String username = authentication.getName();

        Optional<UserEntity> userOpt = userService
                .findByUsername(username);
        if (!userOpt.isPresent())
            throw new ResourceNotFoundException("No such user found");

        UserEntity user = userOpt.get();
        String userId = user.getUserId();

        Optional<BudgetEntity> budgetOpt = budgetRepo.findByUserIdAndCategory(
                userId,
                input.getCategory());

        if (budgetOpt.isPresent()) {

            BudgetEntity budget = budgetOpt.get();

            List<UserExpenseEntity> userExpenses = userExpenseRepo.findByUserId(userId);

            double totalSpentInCategory = 0;

            for (UserExpenseEntity expense : userExpenses) {

                if (expense.getCategory() != null &&
                        expense.getCategory()
                                .equalsIgnoreCase(input.getCategory())) {

                    totalSpentInCategory += expense.getAmount();
                }
            }

            if (totalSpentInCategory + input.getAmount() > budget.getMonthlyBudget()) {
                throw new IllegalArgumentException(
                        "Expense exceeds monthly budget");
            }
        }

        UserExpenseEntity expense = new UserExpenseEntity();

        expense.setUserId(userId);
        expense.setExpenseName(input.getExpenseName());
        expense.setAmount(input.getAmount());
        expense.setCategory(input.getCategory());
        expense.setDate(input.getDate());
        expense.setDescription(input.getDescription());

        return userExpenseRepo.saveAndFlush(expense);
    }

    public List<UserExpenseEntity> findByUserId(String userId) {
        return userExpenseRepo.findByUserId(userId);
    }

    public Optional<UserExpenseEntity> findById(String expenseId) {
        return userExpenseRepo.findById(expenseId);
    }

    public Optional<UserExpenseEntity> findByExpenseIdAndUserId(String expenseId, String userId) {
        return userExpenseRepo.findByExpenseIdAndUserId(expenseId, userId);
    }

    public List<UserExpenseEntity> findByIdMonthAndYear(String userId, int month, int year) {
        return userExpenseRepo.findByIdMonthAndYear(userId, month, year);
    }

    public List<UserExpenseEntity> findByIdYear(String userId, int year) {
        return userExpenseRepo.findByUserIdYear(userId, year);
    }

    public void deleteExpense(String expenseId) {
        if (!userExpenseRepo.findById(expenseId).isPresent())
            throw new ResourceNotFoundException("Expense not found with id: " + expenseId);
        userExpenseRepo.deleteById(expenseId);
    }

    public double calculate(String userId) {
        List<UserExpenseEntity> list = userExpenseRepo.findByUserId(userId);
        int sum = 0;

        for (int i = 0; i < list.size(); i++) {
            UserExpenseEntity expense = list.get(i);
            sum += expense.getAmount();
        }

        return sum;
    }

    public Map<String, Double> categoryWiseSum(String user_id) {
        List<UserExpenseEntity> list = userExpenseRepo.findByUserId(user_id);
        if (list.isEmpty()) {
            throw new ResourceNotFoundException("No user found for id: " + user_id);
        }
        Map<String, Double> mpp = new HashMap<>();

        for (int i = 0; i < list.size(); i++) {
            UserExpenseEntity user = list.get(i);
            if (mpp.containsKey(user.getCategory()))
                mpp.put(user.getCategory(), user.getAmount() + mpp.get(user.getCategory()));
            else
                mpp.put(user.getCategory(), user.getAmount());
        }

        return mpp;
    }
}
