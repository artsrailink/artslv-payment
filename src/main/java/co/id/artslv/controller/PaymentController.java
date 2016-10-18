package co.id.artslv.controller;

import co.id.artslv.lib.availability.AvailabilityData;
import co.id.artslv.lib.responses.MessageWrapper;
import co.id.artslv.lib.transactions.Transaction;
import co.id.artslv.lib.utility.CustomErrorResponse;
import co.id.artslv.lib.utility.CustomException;
import co.id.artslv.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by gemuruhgeo on 06/09/16.
 */
@RestController
@RequestMapping(value = "/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @RequestMapping(value = "/arts_payment/{rqid}",method = RequestMethod.POST)
    public ResponseEntity<?> getPayment(@RequestBody Transaction transaction,@PathVariable String rqid){
        MessageWrapper payments;
        try {
            payments = paymentService.insertPayment(transaction,rqid);
        } catch (CustomException e) {
            CustomErrorResponse customErrorResponse = (CustomErrorResponse) e.getCause();
            MessageWrapper<Object> error = new MessageWrapper<>(customErrorResponse);
            return new ResponseEntity<>(error, HttpStatus.OK);
        }
        return new ResponseEntity<>(payments,HttpStatus.OK);
    }

}
