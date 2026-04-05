
package repository;

import model.Accommodation;
import java.util.List;

public interface AccommodationRepository extends Repository<Accommodation, String> {
    List<Accommodation> findAvailable();
}
