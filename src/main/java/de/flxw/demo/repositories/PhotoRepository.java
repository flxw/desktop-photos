package de.flxw.demo.repositories;

import de.flxw.demo.data.GraphicsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface PhotoRepository extends JpaRepository<GraphicsData, Long> {
    @Query("DELETE from GraphicsData WHERE id IN :terminated")
    public void deleteInBatchById(@Param("terminated") Iterable<Long> toBeDeleted);

    @Query("SELECT id FROM GraphicsData")
    public Set<Long> getAllIds();
}
