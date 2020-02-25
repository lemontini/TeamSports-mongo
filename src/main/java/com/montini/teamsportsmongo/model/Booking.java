package com.montini.teamsportsmongo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor

public class Booking {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
