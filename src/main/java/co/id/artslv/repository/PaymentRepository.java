package co.id.artslv.repository;

import co.id.artslv.lib.transactions.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by root on 26/09/16.
 */
public interface PaymentRepository extends JpaRepository<Transaction,String>{
}
