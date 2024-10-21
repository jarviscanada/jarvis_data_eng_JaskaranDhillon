package ca.jrvs.apps.trading.repository;

import ca.jrvs.apps.trading.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionDao extends JpaRepository<Position, Long> {
    Optional<Position> findByTicker(String ticker);
    @Query("SELECT p FROM Position p WHERE p.accountId = :accountId")
    List<Position> findAllByAccountId(@Param("accountId") Integer accountId);
}