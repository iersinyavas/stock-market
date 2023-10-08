package com.artsoft.stock.controller;

import com.artsoft.stock.service.operation.ShareOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/share-order")
@CrossOrigin(origins = "*")
public class ShareOrderController {

    @Autowired
    private ShareOrderService shareOrderService;


    //PAgeable
    /*@GetMapping(value="/file-reconciliations-by-egm-object-code", produces = MediaType.APPLICATION_JSON)
    @Operation(description = "Get ftp files from egm ftp server by service")
    public ResponseEntity<BaseResponse<Page<LReconciliationOperationStatusDTO>>> findFileReconciliationByEgmObjectCode(@SpringQueryMap RequestEGMObjectType request, @PageableDefault(sort = {"createDate"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<LReconciliationOperationStatusDTO> lReconciliationOperationStatusDTOList = statecontributionfilereconciliationService.findReconciliationListByEgmObjectCode(request.getEgmObjectCode(), pageable);
        return  MafResponseEntity.ok(new BaseResponse<>(lReconciliationOperationStatusDTOList));
    }

    public Page<LReconciliationOperationStatusDTO> findByCriteriaWithPage(String egmObjectCode, Pageable pageable) {
        LReconciliationOperationStatusRepository.LReconciliationOperationStatusQuerySpec querySpec = new LReconciliationOperationStatusRepository.LReconciliationOperationStatusQuerySpec();
        querySpec.setEgmObjectType(egmObjectCode);
        Page<LReconciliationOperationStatus> pageData = lReconciliationOperationStatusRepository.findAll(querySpec, pageable);
        return pageData.map(item -> {
            LReconciliationOperationStatusDTO dto = new LReconciliationOperationStatusDTO();
            dto.setEgmObjectCode(item.getEgmObjectType().getName());
            dto.setFileName(item.getFileName().replaceAll("\\..*", ""));
            dto.setTotalAndTransferredCountAsStr(LReconciliationOperationStatusMapper.INSTANCE.getTotalAndTransferredCountAsStr(item));
            dto.setOperationCompletedCount(item.getOperationCompletedCount());
            dto.setControlStartDate(DateConverterUtil.convertStartAndEndDateLStatusOperation(item.getControlStartDate()));
            dto.setOperationOngoingCount(item.getOperationOngoingCount());
            dto.setControlEndDate(DateConverterUtil.convertStartAndEndDateLStatusOperation(item.getControlEndDate()));
            dto.setOperationStatusValue(item.getOperationStatusType().getName());
            dto.setOperationFailedCount(item.getOperationFailedCount());
            dto.setOperationStatusCode(item.getOperationStatusType().getCode());
            dto.setVersion(item.getVersion());
            dto.setAcquisitionOrder(item.getAcquisitionOrder());
            dto.setCreateDate(item.getCreateDate());
            dto.setCreateUser(item.getUpdateUser());
            dto.setDataPeriod(item.getDataPeriod());
            dto.setDeleteFlag(item.getDeleteFlag());
            dto.setObjectRecordCount(item.getObjectRecordCount());
            dto.setReconciliatiationOperationStatusId(item.getReconciliatiationOperationStatusId());
            dto.setTransferredRecordCount(item.getTransferredRecordCount());
            dto.setVersion(item.getVersion());
            dto.setUpdateDate(item.getUpdateDate());
            dto.setUpdateUser(item.getUpdateUser());
            return dto;
        });
    }*/

}
