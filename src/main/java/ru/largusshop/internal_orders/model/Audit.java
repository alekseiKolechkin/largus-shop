package ru.largusshop.internal_orders.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Audit {
    Diff diff;
    String uid;
    LocalDateTime moment;
}
