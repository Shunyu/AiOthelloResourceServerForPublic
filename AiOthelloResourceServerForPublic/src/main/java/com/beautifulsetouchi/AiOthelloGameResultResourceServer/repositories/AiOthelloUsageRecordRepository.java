package com.beautifulsetouchi.AiOthelloGameResultResourceServer.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.beautifulsetouchi.AiOthelloGameResultResourceServer.models.AiOthelloUsageRecord;

public interface AiOthelloUsageRecordRepository extends JpaRepository<AiOthelloUsageRecord, Long> {
	
	List<AiOthelloUsageRecord> findByUserid(String userid);
	@Transactional
	void deleteByUserid(String userid);
	boolean existsByUserid(String userid);
    
}
