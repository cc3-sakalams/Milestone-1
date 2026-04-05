// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
// Guest and Admin as two main classes | Repository pattern with SQL | Scanner used | Full Java implementation

package repository;

import model.Accommodation;
import java.util.List;

public interface AccommodationRepository extends Repository<Accommodation, String> {
    List<Accommodation> findAvailable();
}
