package com.example.repository;

import com.example.model.Task;
import com.example.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Task t WHERE t.status = :status ORDER BY t.createdAt ASC")
    Optional<Task> findFirstByStatusOrderByCreatedAtAsc(@Param("status") TaskStatus status);

    List<Task> findByStatus(TaskStatus status);
} 