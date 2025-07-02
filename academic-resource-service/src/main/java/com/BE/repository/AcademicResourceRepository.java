package com.BE.repository;

import com.BE.model.AcademicResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AcademicResourceRepository extends JpaRepository<AcademicResource, Long> {

       // Search by name or description
       @Query("SELECT DISTINCT ar FROM AcademicResource ar " +
                     "WHERE (:keyword IS NULL OR :keyword = '' OR " +
                     "LOWER(ar.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                     "LOWER(ar.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
       Page<AcademicResource> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

       // Search by type
       Page<AcademicResource> findByTypeIgnoreCase(String type, Pageable pageable);

       // Complex search with multiple filters
       @Query("SELECT DISTINCT ar FROM AcademicResource ar " +
                     "LEFT JOIN ar.resourceTags rt " +
                     "LEFT JOIN rt.tag t " +
                     "WHERE (:keyword IS NULL OR :keyword = '' OR " +
                     "LOWER(ar.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                     "LOWER(ar.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                     "AND (:type IS NULL OR :type = '' OR LOWER(ar.type) = LOWER(:type)) " +
                     "AND (:tagIds IS NULL OR t.id IN :tagIds)")
       Page<AcademicResource> findByFilters(
                     @Param("keyword") String keyword,
                     @Param("type") String type,
                     @Param("tagIds") Set<Long> tagIds,
                     Pageable pageable);

       // Find resources by tag IDs
       @Query("SELECT DISTINCT ar FROM AcademicResource ar " +
                     "JOIN ar.resourceTags rt " +
                     "JOIN rt.tag t " +
                     "WHERE t.id IN :tagIds")
       Page<AcademicResource> findByTagIds(@Param("tagIds") Set<Long> tagIds, Pageable pageable);

       // Find resources by multiple tag IDs (must have ALL tags)
       @Query("SELECT ar FROM AcademicResource ar " +
                     "WHERE ar.id IN (" +
                     "SELECT rt.resource.id FROM ResourceTag rt " +
                     "WHERE rt.tag.id IN :tagIds " +
                     "GROUP BY rt.resource.id " +
                     "HAVING COUNT(DISTINCT rt.tag.id) = :tagCount)")
       Page<AcademicResource> findByAllTagIds(@Param("tagIds") Set<Long> tagIds,
                     @Param("tagCount") long tagCount,
                     Pageable pageable);

       // Find by ID with tags fetched
       @Query("SELECT ar FROM AcademicResource ar " +
                     "LEFT JOIN FETCH ar.resourceTags rt " +
                     "LEFT JOIN FETCH rt.tag " +
                     "WHERE ar.id = :id")
       Optional<AcademicResource> findByIdWithTags(@Param("id") Long id);

       // Count resources by type
       @Query("SELECT ar.type, COUNT(ar) FROM AcademicResource ar GROUP BY ar.type")
       List<Object[]> countByType();

       // Find recent resources
       @Query("SELECT ar FROM AcademicResource ar ORDER BY ar.createdAt DESC")
       Page<AcademicResource> findRecentResources(Pageable pageable);
}
