package club.repository;

import club.model.Recognitions;
import club.model.RifLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Репозиторий, курирующий контроль распознаваний на 1 РУНЕТ-ID
 *
 */
@Repository
public interface RecognitionsCountControlRepository extends CrudRepository<Recognitions, Long> {

	/**
	 * Метод проверки наличия в БД Runet-ID и EAN (это нужно при пополнеии карты).
	 *
	 * @param runetId
	 * @param ean
	 * @return
	 */
	/*@Query(value = "select count(l) from RifLog l WHERE l.runetId =?1 and l.virtalCardID =?2")
	Long validateP2P(String runetId, String ean);*/


	/**
	 * Получаем entry по Runet-Id.
	 *
 	 * @param runetId
	 *
	 * @return
	 */
	@Query(value = "select l from Recognitions l WHERE l.runetId =?1")
	List<Recognitions> getEntryByRunetId(String runetId);

}

