package club.repository;

import club.model.RifLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Репозиторий, курирующий операции по открытию счета.
 *
 */
@Repository
public interface RifLogsRepository extends CrudRepository<RifLog, Long> {

	/**
	 * Метод проверки наличия в БД Runet-ID и EAN (это нужно при пополнеии карты).
	 *
	 * @param runetId
	 * @param ean
	 * @return
	 */
	@Query(value = "select count(l) from RifLog l WHERE l.runetId =?1 and l.virtalCardID =?2")
	Long validateP2P(String runetId, String ean);

	/**
	 * Получаем entry РифЛога по Runet-Id и EAN.
	 *
	 * @param runetId
	 * @param ean
	 * @return
	 */
	@Query(value = "select l from RifLog l WHERE l.runetId =?1 and l.virtalCardID =?2")
	List<RifLog> getRifLogEntryBuRunetAndEan(String runetId, String ean);

}

