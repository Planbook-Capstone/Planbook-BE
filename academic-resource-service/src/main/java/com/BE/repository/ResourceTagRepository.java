package com.BE.repository;

import com.BE.model.ResourceTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceTagRepository extends JpaRepository<ResourceTag, Long> {

    // Find by resource ID
    List<ResourceTag> findByResource_Id(Long resourceId);

    // Find by tag ID
    List<ResourceTag> findByTag_Id(Long tagId);

    // Delete by resource ID
    @Modifying
    @Query("DELETE FROM ResourceTag rt WHERE rt.resource.id = :resourceId")
    void deleteByResourceId(@Param("resourceId") Long resourceId);

    // Delete by tag ID
    @Modifying
    @Query("DELETE FROM ResourceTag rt WHERE rt.tag.id = :tagId")
    void deleteByTagId(@Param("tagId") Long tagId);

    // Delete specific resource-tag relationship
    @Modifying
    @Query("DELETE FROM ResourceTag rt WHERE rt.resource.id = :resourceId AND rt.tag.id = :tagId")
    void deleteByResourceIdAndTagId(@Param("resourceId") Long resourceId, @Param("tagId") Long tagId);

    // Check if resource-tag relationship exists
    boolean existsByResource_IdAndTag_Id(Long resourceId, Long tagId);

    // Count resources for a tag
    @Query("SELECT COUNT(rt) FROM ResourceTag rt WHERE rt.tag.id = :tagId")
    long countByTagId(@Param("tagId") Long tagId);

    // Count tags for a resource
    @Query("SELECT COUNT(rt) FROM ResourceTag rt WHERE rt.resource.id = :resourceId")
    long countByResourceId(@Param("resourceId") Long resourceId);
}
