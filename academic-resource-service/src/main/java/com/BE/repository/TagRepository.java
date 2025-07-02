package com.BE.repository;

import com.BE.model.Tag;
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
public interface TagRepository extends JpaRepository<Tag, Long> {
    
    // Find by name (case insensitive)
    Optional<Tag> findByNameIgnoreCase(String name);
    
    // Check if tag exists by name
    boolean existsByNameIgnoreCase(String name);
    
    // Search tags by name or description
    @Query("SELECT t FROM Tag t WHERE " +
           "LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Tag> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // Find tags by IDs
    List<Tag> findByIdIn(Set<Long> ids);
    
    // Find popular tags (tags with most resources)
    @Query("SELECT t, COUNT(rt) as resourceCount FROM Tag t " +
           "LEFT JOIN t.resourceTags rt " +
           "GROUP BY t " +
           "ORDER BY resourceCount DESC")
    Page<Object[]> findPopularTags(Pageable pageable);
    
    // Find tags used by a specific resource
    @Query("SELECT t FROM Tag t " +
           "JOIN t.resourceTags rt " +
           "WHERE rt.resource.id = :resourceId")
    List<Tag> findByResourceId(@Param("resourceId") Long resourceId);
    
    // Find unused tags (tags with no resources)
    @Query("SELECT t FROM Tag t " +
           "WHERE t.id NOT IN (SELECT DISTINCT rt.tag.id FROM ResourceTag rt)")
    List<Tag> findUnusedTags();
}
