package com.example.expense_tracker.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.expense_tracker.Entity.BudgetEntity;
import com.example.expense_tracker.Entity.UserEntity;
import com.example.expense_tracker.dto.UpdateBudgetInput;
import com.example.expense_tracker.service.BudgetService;
import com.example.expense_tracker.service.UserService;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Budget APIs")
@SecurityRequirement(name = "bearerAuth")
@RestController
public class BudgetController {
    @Autowired
    private BudgetService budgetService;
    @Autowired
    private UserService userService;

    @GetMapping("view-budgets")
    public ResponseEntity<?> getAllBudgets(Authentication authentication) {
        String username = authentication.getName();
        Optional<UserEntity> userOpt = userService.findByUsername(username);
        if (!userOpt.isPresent()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        UserEntity user = userOpt.get();
        String userId = user.getUserId();

        System.out.println("VIEW BUDGETS HIT");

        return new ResponseEntity<>(
                budgetService.findByUserId(userId),
                HttpStatus.OK);
    }

    @PostMapping("add-budget")
    public ResponseEntity<?> createBudget(Authentication authentication,
            @Valid @RequestBody UpdateBudgetInput newBudget) {
        String username = authentication.getName();
        System.out.println(authentication);
        Optional<UserEntity> userOpt = userService.findByUsername(username);
        BudgetEntity budgetEntity = new BudgetEntity();
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            String userId = user.getUserId();
            budgetEntity.setUserId(userId);
            budgetEntity.setCategory(newBudget.getCategory());
            budgetEntity.setMonthlyBudget(newBudget.getMonthlyBudget());
        }
        return new ResponseEntity<>(budgetService.addBudget(budgetEntity), HttpStatus.CREATED);
    }

    @PutMapping("update-budget")
    public ResponseEntity<?> updateBudget(Authentication authentication,
            @Valid @RequestBody UpdateBudgetInput updatedBudget) {
        String username = authentication.getName();
        Optional<UserEntity> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            String userId = user.getUserId();

            Optional<BudgetEntity> budgetOpt = budgetService.findByUserIdAndCategory(userId,
                    updatedBudget.getCategory());
            if (budgetOpt.isPresent()) {
                BudgetEntity budget = budgetOpt.get();
                budget.setMonthlyBudget(updatedBudget.getMonthlyBudget());
                return new ResponseEntity<>(budgetService.addBudget(budget), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
