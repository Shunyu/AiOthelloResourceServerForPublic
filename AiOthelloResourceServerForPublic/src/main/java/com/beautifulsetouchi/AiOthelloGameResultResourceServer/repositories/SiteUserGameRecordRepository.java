package com.beautifulsetouchi.AiOthelloGameResultResourceServer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.beautifulsetouchi.AiOthelloGameResultResourceServer.models.SiteUserGameRecord;

public interface SiteUserGameRecordRepository extends JpaRepository<SiteUserGameRecord, Long> {
	
	SiteUserGameRecord findByUserid(String userid);
	boolean existsByUserid(String userid);

    @Transactional
    @Modifying
	@Query("UPDATE SiteUserGameRecord u SET u.winnumber = :winnumber WHERE u.userid = :userid")
	Integer setFixedWinnumberFor(@Param("winnumber") Long winnumber, @Param("userid") String userid);

    @Transactional
    @Modifying
	@Query("UPDATE SiteUserGameRecord u SET u.losenumber = :losenumber WHERE u.userid = :userid")
	Integer setFixedLosenumberFor(@Param("losenumber") Long losenumber, @Param("userid") String userid);	

    @Transactional
    @Modifying
	@Query("UPDATE SiteUserGameRecord u SET u.drawnumber = :drawnumber WHERE u.userid = :userid")
	Integer setFixedDrawnumberFor(@Param("drawnumber") Long drawnumber, @Param("userid") String userid);
    
}
