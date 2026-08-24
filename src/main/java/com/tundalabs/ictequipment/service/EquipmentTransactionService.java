package com.tundalabs.ictequipment.service;

import com.tundalabs.ictequipment.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EquipmentTransactionService {

    TransactionResponseDto createTransaction(CreateTransactionRequestDto request);

    TransactionResponseDto getTransactionById(Long id);

    TransactionResponseDto getTransactionByCode(String transactionCode);

    Page<TransactionResponseDto> getTransactions(TransactionFilterParams filters, Pageable pageable);

    TransactionResponseDto submitSignatures(Long transactionId, SubmitSignatureRequestDto request);

    TransactionResponseDto cancelTransaction(Long transactionId);

    byte[] generateTransactionPdf(Long transactionId);
}
