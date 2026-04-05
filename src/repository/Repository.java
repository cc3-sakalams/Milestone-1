// === THEME PARK + RESORT SYSTEM - CORRECT VERSION ===
// Guest and Admin as two main classes | Repository pattern with SQL | Scanner used | Full Java implementation

package repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic Repository — base CRUD contract for all repositories.
 *
 * @param <T>  Entity type
 * @param <ID> Primary key type
 */
public interface Repository<T, ID> {
    void        save(T entity);
    Optional<T> findById(ID id);
    List<T>     findAll();
    void        update(T entity);
    void        delete(ID id);
}
