package co.id.artslv.service;

import co.id.artslv.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by root on 26/09/16.
 */
@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;


}
