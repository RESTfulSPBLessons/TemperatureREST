package club.model;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Энтити для хранения/логгирования информации о пополнении счета
 */
@Entity
@Table(name = "account_refil_log")
public class AccountRefillLog {


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Column
	private String runetId; //RUNET-ID - СТРОКА
	@Column
	private String ean; //EAN
	@Column
	private Long amount; //сумма перевода в копейках
	@Column
	private String orderId; //order ID
	@Column
	private java.sql.Timestamp transferDate; //дата/время (таймстэмп) совершения перевода (то есть когда пришел запрос от РИФа)
	@Column
	private java.sql.Timestamp approveDate; //дата/время подтверждения перевода от АБС
	@Column
	private String state; //Результат (в POST от Банка в поле "state" может быть APPROVED или DECLINED)
	@Column
	private String stateDescription; //Расшифровка результата (в POST от Банка в поле "stateDescription")
	@Column
	private String url; //url, который приходит от партнера и на который мы редеректим клиента

	public AccountRefillLog() {
	}

	public AccountRefillLog(String runetId, String ean, Long amount, java.sql.Timestamp approveDate, String orderId, String state,
	                        String stateDescription, String url) {

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		this.runetId = runetId;
		this.ean = ean;
		this.amount = amount;
		this.orderId = orderId;
		this.transferDate = timestamp;
		this.approveDate = approveDate;
		this.state = state;
		this.stateDescription = stateDescription;
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setOrderId(java.lang.String orderId) {
		this.orderId = orderId;
	}

	public void setApproveDate(java.sql.Timestamp approveDate) {
		this.approveDate = approveDate;
	}

	public void setState(java.lang.String state) {
		this.state = state;
	}

	public void setStateDescription(java.lang.String stateDescription) {
		this.stateDescription = stateDescription;
	}

	public Long getAmount() {
		return amount;
	}

	public String getEan() {
		return ean;
	}

	public String getOrderId() {
		return orderId;
	}

	public String getRunetId() {
		return runetId;
	}

	public String getState() {
		return state;
	}
}


