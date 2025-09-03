package com.movieticketbooking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movieticketbooking.entity.PgTransaction;

@Repository
public interface PgTransactionDao extends JpaRepository<PgTransaction, Integer> {

	PgTransaction findByTypeAndOrderId(String value, String razorpayOrderId);

}
