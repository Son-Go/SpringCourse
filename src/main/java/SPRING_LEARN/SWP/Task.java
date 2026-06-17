package SPRING_LEARN.SWP;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record Task (
        Long id,
        Long creatorId,
        Long assignedUserId,
        Status status,
        LocalDateTime createDateTime,
        LocalDate deadlineDate,
        Priority priority
) {
}
