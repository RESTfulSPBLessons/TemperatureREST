package ru.reso.calclogcompare.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

/**
 * Entity.
 */
@Entity
@Table(name = "WS_PREM_TEST")
public class Premium {

    /**
     * Primary key.
     */
    @Id
    @Column(name = "ID", nullable = false)
    @SequenceGenerator(name = "my_seq", sequenceName = "WS_PREM_TEST_ID_SEQ")
    @GeneratedValue(strategy =  GenerationType.SEQUENCE, generator = "my_seq")
    @Getter
    @Setter
    private Long id;


    /**
     * Name.
     */
    @Getter
    @Setter
    private String name;

    /**
     * Descripotion.
     */
    @Getter
    @Setter
    private String description;

    /**
     * Risk.
     */
    @Getter
    @Setter
    private String risk;

    /**
     * Конструктор для Премии.
     *
     * @param name - имя.
     */
    public Premium(final String name) {
        this.name = name;
        this.description = "description";
        this.risk = "risk";

    }

    /**
     * Дефолтный конструктор.
     */
    public Premium() {
    }

    @Override
    public final String toString() {
        return "Premium{" + "id=" + id + ", name='" + name + '\'' + '}';
    }

}
