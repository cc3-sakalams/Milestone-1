// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
// Guest and Admin as two main classes | Repository pattern with SQL | Scanner used | Full Java implementation

package repository;

import model.Ticket;
import java.util.List;

public interface TicketRepository extends Repository<Ticket, String> {
    List<Ticket> findAvailable();
}
