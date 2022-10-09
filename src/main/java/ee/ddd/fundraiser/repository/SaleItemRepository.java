package ee.ddd.fundraiser.repository;

import ee.ddd.fundraiser.domain.SaleItem;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SaleItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {}
