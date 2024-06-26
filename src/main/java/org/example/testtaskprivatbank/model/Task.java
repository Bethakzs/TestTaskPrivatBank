package org.example.testtaskprivatbank.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Title cannot be null")
    @Column(name = "title", unique = true, nullable = false)
    private String title;

    @NotEmpty(message = "Description cannot be empty")
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(name = "deadline")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @NotNull(message = "Deadline cannot be null")
    private LocalDateTime deadline;

    @Column(name = "notified_one_hour")
    @JsonIgnore
    private boolean notifiedOneHour;

    @Column(name = "notified_ten_minutes")
    @JsonIgnore
    private boolean notifiedTenMinutes;

    @Column(name = "notified_deadline")
    @JsonIgnore
    private boolean notifiedDeadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status;
}