package com.partner.repository;

import com.partner.model.entity.PartnerTool;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface PartnerToolRepository extends ReactiveCrudRepository<PartnerTool, Long> {
}
