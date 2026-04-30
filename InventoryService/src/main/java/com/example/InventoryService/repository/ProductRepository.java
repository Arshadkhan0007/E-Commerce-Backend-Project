package com.example.InventoryService.repository;

import com.example.InventoryService.entity.Product;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    @Query(value = """
                SELECT * FROM (
                                SELECT p.*, ROWNUM rnum
                                FROM (SELECT * FROM e_com_product ORDER BY product_id) p
                                WHERE ROWNUM <= :endRow
                                )
                WHERE rnum > :startRow
            """, nativeQuery = true)
    List<Product> findAll(@Param("startRow") int startRow,
                          @Param("endRow") int endRow);

}
