package club.repository;

import club.model.AccountRefillLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface AccountRefillRepository extends CrudRepository<AccountRefillLog, Long> {

	@Query(value = "select l from AccountRefillLog l WHERE l.orderId =:#{#param}")
	List<AccountRefillLog> getAllLogsByOrderId(@Param("param") String param);

	@Query(value = "select sum(l.amount) from AccountRefillLog l WHERE l.state='APPROVED'")
	Long getSumOfAllTransactions();

}

