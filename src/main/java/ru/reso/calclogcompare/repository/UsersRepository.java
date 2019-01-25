package ru.reso.calclogcompare.repository;


import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.reso.calclogcompare.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface UsersRepository extends JpaRepository<Users, Integer>{
}
