package com.gr.dm.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.gr.dm.core.entity.TransactionDetail;

/**
 * 
 * @author Aleem Malik
 *
 */
public interface TransactionDetailRepository extends CrudRepository<TransactionDetail, Long> {

	List<TransactionDetail> findByTransactionId(String id);
	
	@Query("select td from TransactionDetail td where td.transactionId = :transactionId and td.productCategory = :productCategory")
	TransactionDetail getTransactionDetail(@Param("transactionId") String transactionId, @Param("productCategory") String productCategory);
	
	List<TransactionDetail> getTransactionDetailByTransactionId(String transactionId);
}