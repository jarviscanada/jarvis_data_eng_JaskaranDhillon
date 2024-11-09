package ca.jrvs.apps.trading.repository;

import ca.jrvs.apps.trading.model.SecurityOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SecurityOrderDao extends JpaRepository<SecurityOrder, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM SecurityOrder so WHERE so.accountId = :accountId")
    void deleteByAccountId(@Param("accountId") Long accountId);
}
