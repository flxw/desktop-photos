package de.flxw.demo.repositories;

import de.flxw.demo.data.GraphicsData;
import de.flxw.demo.data.TimelineEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface PhotoRepository extends JpaRepository<GraphicsData, Long> {
    @Transactional
    @Modifying
    @Query("DELETE from GraphicsData WHERE id IN :terminated")
    void deleteInBatchById(@Param("terminated") Iterable<Long> toBeDeleted);

    @Query("SELECT id FROM GraphicsData")
    Set<Long> getAllIds();

    @Query("SELECT NEW de.flxw.demo.data.TimelineEntry(CAST(TIME_STAMP AS date) AS date, " +
            "ARRAY_AGG(CONCAT_WS(';', id, height, width))) " +
            "FROM GraphicsData " +
            "GROUP BY date " +
            "ORDER BY date DESC")
    List<TimelineEntry> getTimelineIds();

    @Query("SELECT thumbnailImage FROM GraphicsData WHERE id = :id")
    byte[] getThumbnailImageById(@Param("id") Long id);
}
