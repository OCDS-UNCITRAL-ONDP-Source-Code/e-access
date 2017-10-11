package com.ocds.tender.service;

import com.ocds.tender.model.dto.tender.Tender;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public interface TenderService {

    void insertData(String ocId, Date addedDate, Tender data);

    void updateData(String ocId, Date addedDate, Tender data);

}
