package club.service;

import club.model.AccountRefillLog;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

public interface AccountRefillService {

	String getUrlByOrderId(String orderId);

	String getRunetIdByOrderId(String orderId) throws Exception;

	void add(AccountRefillLog log);

	void updateDBEntry(String orderId, String status, String status_description, Timestamp valueOf);

	String getEanByOrderId(String orderId) throws Exception;
}
