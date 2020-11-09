package maxstasiuk.relay.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import maxstasiuk.relay.data.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
