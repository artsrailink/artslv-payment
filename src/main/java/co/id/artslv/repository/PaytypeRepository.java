package co.id.artslv.repository;

import co.id.artslv.lib.payments.Paytype;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PaytypeRepository extends JpaRepository<Paytype,String>{
    Paytype findOneByCode(String paytypecode);
}
