package com.example.expense_tracker.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.expense_tracker.Entity.BudgetEntity;

@Repository
public interface BudgetRepo extends JpaRepository<BudgetEntity, String> {
        public List<BudgetEntity> findAll();

        public BudgetEntity saveAndFlush(BudgetEntity newBudget);

        @Query("""
                        SELECT b
                        FROM Budget b
                        WHERE LOWER(b.userId)=LOWER(:userId)
                        AND LOWER(b.category)=LOWER(:category)
                        """)
        public Optional<BudgetEntity> findByUserIdAndCategory(@Param("userId") String userId,
                        @Param("category") String category);

        public List<BudgetEntity> findByUserId(String userId);
}
