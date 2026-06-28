package com.example.expense_tracker.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.expense_tracker.Entity.BudgetEntity;
import com.example.expense_tracker.Repository.BudgetRepo;

@Service
public class BudgetService {

    @Autowired
    BudgetRepo budgetRepo;

    public List<BudgetEntity> getAllBudgets() {
        return budgetRepo.findAll();
    }

    public BudgetEntity addBudget(BudgetEntity newBudget) {
        return budgetRepo.saveAndFlush(newBudget);
    }

    public Optional<BudgetEntity> findByUserIdAndCategory(String userId, String category) {
        return budgetRepo.findByUserIdAndCategory(userId, category);
    }

    public List<BudgetEntity> findByUserId(String userId) {
        return budgetRepo.findByUserId(userId);
    }

    public double calculate(String userId) {
        List<BudgetEntity> budgets = budgetRepo.findByUserId(userId);
        double sum = 0;
        for (int i = 0; i < budgets.size(); i++) {
            sum += budgets.get(i).getMonthlyBudget();
        }
        return sum;
    }
}
