package co.id.artslv.repository;

import co.id.artslv.lib.transactions.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TransactionRepository extends JpaRepository<Transaction,String>{
    Transaction findOneByBookcode(String bookcode);
    Transaction findOneByPaycode(String paytypecode);
}
