package co.id.artslv.service;

import co.id.artslv.lib.boarding.Boarding;
import co.id.artslv.lib.payments.Paytype;
import co.id.artslv.lib.responses.MessageWrapper;
import co.id.artslv.lib.transactions.Transaction;
import co.id.artslv.lib.transactions.Transactiondet;
import co.id.artslv.lib.users.User;
import co.id.artslv.lib.utility.CustomErrorResponse;
import co.id.artslv.lib.utility.CustomException;
import co.id.artslv.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;


@Service
public class PaymentService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaytypeRepository paytypeRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactiondetRepository transactiondetRepository;

    @Autowired
    private BoardingRepository boardingRepository;

    @Transactional(rollbackFor = CustomException.class)
    public MessageWrapper insertPayment(Transaction transaction, String rqid) throws CustomException {
        Transaction trans = new Transaction();
        Paytype paytype;
        User user;
        if(rqid == null || rqid.trim().equals(""))
            throw new CustomException(new CustomErrorResponse("01","RQID is not valid"));
        user = userRepository.findOneByRqid(rqid);
        if(user == null)
            throw new CustomException(new CustomErrorResponse("01","RQID is not valid"));
        if((transaction.getBookcode() == null || transaction.getBookcode().trim().equals("")) && (transaction.getPaycode() == null || transaction.getPaycode().trim().equals(""))){
            throw new CustomException(new CustomErrorResponse("02","Paycode or Bookcode must be filled"));
        }else{
            if(!(transaction.getBookcode() == null || transaction.getBookcode().trim().equals("")) && transaction.getBookcode().length() != 7)
                throw new CustomException(new CustomErrorResponse("04","Transaction not found"));
            else if(!(transaction.getPaycode() == null || transaction.getPaycode().trim().equals("")) && transaction.getPaycode().length() != 13)
                throw new CustomException(new CustomErrorResponse("04","Transaction not found"));
            }
        if(transaction.getPaytypecode() == null || transaction.getPaytypecode().trim().equals("")){
            throw new CustomException(new CustomErrorResponse("03","Paytype Code must be filled"));
        }else {
            if (!(transaction.getPaycode() == null || transaction.getPaycode().trim().equals(""))) {
                trans = transactionRepository.findOneByPaycode(transaction.getPaycode());
            } else if (!(transaction.getBookcode() == null || transaction.getBookcode().trim().equals(""))) {
                trans = transactionRepository.findOneByBookcode(transaction.getBookcode());
            }
        }
        if(trans==null){
            throw new CustomException(new CustomErrorResponse("04","Transaction not found"));
        }

        paytype = paytypeRepository.findOneByCode(transaction.getPaytypecode());

        if(paytype==null){
            throw new CustomException(new CustomErrorResponse("05","Paytype Code not found"));
        }
        if(trans.getStatus().equals("0")) {
            throw new CustomException(new CustomErrorResponse("06", "Transaction nonaktif"));
        }
        else if(trans.getStatus().equals("2")) {
            throw new CustomException(new CustomErrorResponse("07", "Transaction has been paid"));
        }
        else if(trans.getStatus().equals("3")) {
            throw new CustomException(new CustomErrorResponse("08", "Transaction timeout"));
        }
        else if(trans.getStatus().equals("1")){
            int comp = trans.getNetamount().compareTo(transaction.getNetamount());
            if(comp != 0) {
                throw new CustomException(new CustomErrorResponse("09","Net Amount doesn't match"));
            }
            List<Transactiondet> transdetlist = transactiondetRepository.findByTransactionid(trans.getId());

            for (Transactiondet transdet : transdetlist) {
                //insert tabel arts_t_boarding
                Boarding boarding = new Boarding();
                boarding.setBookcode(trans.getBookcode());
                boarding.setTicketnum(transdet.getTicketnum());
                boarding.setTripdate(trans.getTripdate());
                boarding.setStasiunid(trans.getStasiunidorg());
                boarding.setStasiuncode(trans.getStasiuncodeorg());
                boarding.setUnitid(trans.getUnitidbook());
                boarding.setUnitcode(trans.getUnitcodebook());
                boarding.setStatus("2");
                boarding.setDomain(trans.getDomain());
                boarding.setCreatedby("userpos");
                boarding.setCreatedon(LocalDateTime.now());
                boarding.setModifiedby("userpos");
                boarding.setModifiedon(LocalDateTime.now());
                boarding.setTime(LocalDateTime.now());
                boardingRepository.save(boarding);

                //update tabel arts_t_transactiondet
                transdet.setStatus("2");
                transdet.setUseridpay("userpos");
                transdet.setUserfullnamepay("userpos");
                transdet.setUnitidpay("POS_MRI_01");
                transdet.setUnitcodepay("POS_MRI_01");
                transdet.setChannelidpay("CNL-POS");
                transdet.setChannelcodepay("POS");
                transdet.setModifiedby("userpos");
                transdet.setModifiedon(LocalDateTime.now());
                transactiondetRepository.save(transdet);
            }
            //update tabel arts_t_transaction
            trans.setStatus("2");
            trans.setPaytypeid(paytype.getId());
            trans.setPaytypecode(paytype.getCode());
            trans.setUseridpay("userpos");
            trans.setUserfullnamepay("userpos");
            trans.setUnitidpay("POS_MRI_01");
            trans.setUnitcodepay("POS_MRI_01");
            trans.setChannelidpay("CNL-POS");
            trans.setChannelcodepay("POS");
            trans.setModifiedby("userpos");
            trans.setModifiedon(LocalDateTime.now());
            transactionRepository.save(trans);

        }
        MessageWrapper resultWrapperPayment = new MessageWrapper<>("00","PAYMENT SUCCESS");
        return resultWrapperPayment;
    }
}
