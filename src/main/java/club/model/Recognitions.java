package club.model;

import javax.persistence.*;

/**
 * Энтити для контроля кол-ва распознаваний.
 */
@Entity
@Table(name = "recognitions")
public class Recognitions {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column
	private String runetId;

	@Column
	private Integer count; //Кол-во распознаваний

	public Recognitions() {
	}

	public Recognitions(String runetId) {
		this.runetId = runetId;
		this.count = 1;
	}


	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
}
