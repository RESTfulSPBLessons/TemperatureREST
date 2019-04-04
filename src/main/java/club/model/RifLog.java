package club.model;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;


@Entity
@Table(name = "riflog")
public class RifLog {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column
	private String runetId; //RUNET-ID - СТРОКА
	@Column
	private String lastName; //Фамилия
	@Column
	private String firstName; //Имя
	@Column
	private String middleName; //Отчество
	@Column
	private Date birthDate; //день варенья
	@Column
	private String taxNumber; //ИНН
	@Column
	private String phoneNumber; //Номер тилипона
	@Column()
	private String passportSeria; //Серия пасспорта
	@Column
	private String passportNumber; //Номер пасспорта
	@Column
	private String photoRecognitionID; //ID распознавания фото
	@Column
	private String taxNumRecognitionID; //ID распознавания ИНН
	@Column
	private String clientID; //ID клиента
	@Column
	private String virtalCardID; //ID ВПК карты
	@Column
	private String plasticCardID; //ID пластиковой карты
	@Column
	private String accountNumber; //Номер счета

	@Column
	@Basic(fetch = FetchType.LAZY)
	@Lob
	private byte[] picture;


	public RifLog() {
	}

	public RifLog(String runetId, String lastName, String firstName) {
		this.runetId = runetId;
		this.lastName = lastName;
		this.firstName = firstName;
	}

	public RifLog(Map<String, String> modelData, byte[] compressByteArray) {

		//Спарсим ка мы дату заранее, чтобы проблем не было потом
		Date parsedBirthday = null;
		try{
			parsedBirthday=modelData.get("birthdate")==null ? null : Date.valueOf(LocalDate.parse(modelData.get("birthdate"), DateTimeFormatter.ofPattern("dd.MM.yyyy")));

		} catch (Exception e){
			//  Дату спарсить не удалось
		}

		this.runetId = modelData.get("runetId");
		this.lastName = modelData.get("surname");
		this.firstName = modelData.get("name");
		this.middleName = modelData.get("patronymic");
		this.birthDate = parsedBirthday;
		this.taxNumber = modelData.get("inn");
		this.phoneNumber = modelData.get("phoneNumber");
		this.passportSeria = modelData.get("series");
		this.passportNumber = modelData.get("number");
		this.photoRecognitionID = modelData.get("passportId");
		this.taxNumRecognitionID = modelData.get("inn_recognition_id");
		this.clientID = modelData.get("client_id");
		this.virtalCardID = modelData.get("vpk_number");
		this.plasticCardID = null;
		this.accountNumber = modelData.get("account_number");
		this.picture = compressByteArray;

	}

	public RifLog(Map<String, String> modelData) {

		modelData.remove("birthdate");
		modelData.put("birthdate", "fdvdfv");
		Date parsedBirthday = null;
		try{
			parsedBirthday = modelData.get("birthdate")==null ? null : Date.valueOf(LocalDate.parse(modelData.get("birthdate"), DateTimeFormatter.ofPattern("dd.MM.yyyy")));

		} catch (Exception e){
			//  Дату спарсить не удалось
		}

		this.runetId = "111";
		this.lastName = "Иван";
		this.firstName = "Иванович";
		this.middleName = "Тестов";
		this.birthDate = parsedBirthday;
		this.taxNumber = "test";
		this.phoneNumber = "test";
		this.passportSeria = modelData.get("series");
		this.passportNumber = modelData.get("number");
		this.photoRecognitionID = "2111";
		this.taxNumRecognitionID = null;
		this.clientID = "dfbvvv";
		this.virtalCardID = "sdffdv";
		this.plasticCardID = null;
		this.accountNumber = null;
		this.picture = null;


	}

	@Override
	public String toString() {
		return "RifLog{" +
				"id=" + id +
				", runetId='" + runetId + '\'' +
				'}';
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRunetId() {
		return runetId;
	}

	public void setRunetId(String runetId) {
		this.runetId = runetId;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getTaxNumber() {
		return taxNumber;
	}

	public void setTaxNumber(String taxNumber) {
		this.taxNumber = taxNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPassportSeria() {
		return passportSeria;
	}

	public void setPassportSeria(String passportSeria) {
		this.passportSeria = passportSeria;
	}

	public String getPassportNumber() {
		return passportNumber;
	}

	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}

	public String getPhotoRecognitionID() {
		return photoRecognitionID;
	}

	public void setPhotoRecognitionID(String photoRecognitionID) {
		this.photoRecognitionID = photoRecognitionID;
	}

	public String getTaxNumRecognitionID() {
		return taxNumRecognitionID;
	}

	public void setTaxNumRecognitionID(String taxNumRecognitionID) {
		this.taxNumRecognitionID = taxNumRecognitionID;
	}

	public String getClientID() {
		return clientID;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public String getVirtalCardID() {
		return virtalCardID;
	}

	public void setVirtalCardID(String virtalCardID) {
		this.virtalCardID = virtalCardID;
	}

	public String getPlasticCardID() {
		return plasticCardID;
	}

	public void setPlasticCardID(String plasticCardID) {
		this.plasticCardID = plasticCardID;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public byte[] getPicture() {
		return picture;
	}

	public void setPicture(byte[] picture) {
		this.picture = picture;
	}
}
