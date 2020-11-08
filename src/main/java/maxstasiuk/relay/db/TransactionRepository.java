package maxstasiuk.relay.db;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import maxstasiuk.relay.data.Transaction;

@Repository
@Transactional
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
