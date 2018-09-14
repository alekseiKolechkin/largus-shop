package ru.largusshop.internal_orders.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.largusshop.internal_orders.clients.EntityClient;
import ru.largusshop.internal_orders.model.Audit;
import ru.largusshop.internal_orders.model.CustomerOrder;
import ru.largusshop.internal_orders.model.Meta;
import ru.largusshop.internal_orders.model.Position;
import ru.largusshop.internal_orders.model.PositionDiff;
import ru.largusshop.internal_orders.model.Positions;
import ru.largusshop.internal_orders.model.State;
import ru.largusshop.internal_orders.utils.Credentials;
import ru.largusshop.internal_orders.utils.Mails;
import ru.largusshop.internal_orders.utils.exception.AppError;
import ru.largusshop.internal_orders.utils.exception.AppException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class CustomerOrderService {
    private final State PREDVARITELNYI_VNUTRENNIY = State.builder()
                                                         .meta(Meta.builder()
                                                                   .href("https://online.moysklad.ru/api/remap/1.1/entity/customerorder/metadata/states/f79b9eac-b295-11e8-9109-f8fc00115cd9")
                                                                   .metadataHref("https://online.moysklad.ru/api/remap/1.1/entity/customerorder/metadata")
                                                                   .type("state")
                                                                   .mediaType("application/json")
                                                                   .build())
                                                         .build();

    @Autowired
    private EntityClient entityClient;
    @Autowired
    private CustomerOrderService customerOrderService;
    @Autowired
    private AuditService auditService;
    @Autowired
    private ExcelService excelService;
    @Autowired
    private EmailService emailService;

    public CustomerOrder create(CustomerOrder customerOrder) {
        try {
            return entityClient.createEntity("customerOrder", CustomerOrder.class, customerOrder, Credentials.KRUTILIN);
        } catch (IOException e) {
            throw new AppException("Error while creating customer order, maybe connection error: " + e.toString(), AppError.APPLICATION_ERROR, e);
        }
    }

    public CustomerOrder getById(UUID id) {
        return getById(id, "");
    }

    public CustomerOrder getById(UUID id, String parameters) {
        try {
            return entityClient.getEntityById("customerOrder", CustomerOrder.class, id.toString(), Credentials.KRUTILIN, parameters);
        } catch (IOException e) {
            throw new AppException("Error while getting customer order with id: " + id + ", maybe connection error: " + e.toString(), AppError.APPLICATION_ERROR, e);
        }
    }

    public List<CustomerOrder> search(String searchString) throws IOException {
        return entityClient.findEntities("customerOrder", CustomerOrder.class, Credentials.KRUTILIN, "search=" + searchString);
    }

//    public List<CustomerOrder> filter(Pair<String, String>... parameterValueFilter) throws IOException {
//        String filter = "filter=" + Arrays.stream(parameterValueFilter).map(a -> a.getKey() + "=" + a.getValue()).collect(Collectors.joining(";"));
//        return entityClient.findEntities("customerOrder", CustomerOrder.class, Credentials.KRUTILIN, filter);
//    }

    public List<CustomerOrder> filter(String parameters) throws IOException {
//        if(nonNull(parameterValueFilter)) {
//             parameters = "filter=" + Arrays.stream(parameterValueFilter)
//                                            .map(a -> a.getKey() + "=" + a.getValue())
//                                            .collect(Collectors.joining(";")) + "&" + parameters;
//        }
        return entityClient.findEntities("customerOrder", CustomerOrder.class, Credentials.KRUTILIN, parameters);
    }

    public void update(UUID id, CustomerOrder customerOrder) throws IOException {
        entityClient.updateEntity("customerOrder", id.toString(), customerOrder, Credentials.KRUTILIN);
    }

    public boolean createCustomerOrderFromAudit(UUID customerOrderId) {
        CustomerOrder customerOrder = customerOrderService.getById(customerOrderId, "expand=owner.group");
        return createCustomerOrderFromAudit(customerOrder);
    }

    public boolean createCustomerOrderFromAudit(CustomerOrder customerOrder) {
        Integer sumOfOrder = customerOrder.getSum();
        List<Audit> audit = auditService.auditCustomerOrder(customerOrder.getId());
        audit = audit.stream().sorted(Comparator.comparing(Audit::getMoment).reversed()).collect(Collectors.toList());
        Audit lastChangeMadeByCustomer = audit.stream().filter(a -> a.getUid().equals(customerOrder.getOwner().getUid())).findFirst().orElse(null);
        List<Audit> changesToCreateOrderFrom = audit.subList(0, nonNull(lastChangeMadeByCustomer) ? audit.indexOf(lastChangeMadeByCustomer) : audit.size());
        customerOrder.setPositions(new Positions());
        setNewName(customerOrder);
        List<Position> positionsToAdd = new ArrayList<>();
        for (Audit changes : changesToCreateOrderFrom) {
            if (isNull(changes.getDiff()) || isNull(changes.getDiff().getPositions())) {
                continue;
            }
            List<PositionDiff> positions = changes.getDiff().getPositions();
            for (PositionDiff positionDiff : positions) {
                Position newValue = positionDiff.getNewValue();
                Position oldValue = positionDiff.getOldValue();
                if (isNull(oldValue) && isNull(newValue)) {
                    continue;
                }
                if (nonNull(newValue) && nonNull(oldValue)) {
                    float quantityDiff = oldValue.getQuantity() - newValue.getQuantity();
                    Position existingPosition = positionsToAdd.stream()
                                                              .filter(position -> position.getAssortment().getMeta().equals(newValue.getMeta()))
                                                              .findFirst().orElse(null);
                    if (nonNull(existingPosition)) {
                        existingPosition.setQuantity(existingPosition.getQuantity() + quantityDiff);
                    } else {
                        newValue.setQuantity(quantityDiff);
                        positionsToAdd.add(newValue);
                    }
                    continue;
                }
                if (nonNull(newValue)) {
                    Position existingPosition = positionsToAdd.stream()
                                                              .filter(position -> position.getAssortment().getMeta().equals(newValue.getMeta()))
                                                              .findFirst().orElse(null);
                    if (nonNull(existingPosition)) {
                        existingPosition.setQuantity(existingPosition.getQuantity() - newValue.getQuantity());
                    } else {
                        newValue.setQuantity(-newValue.getQuantity());
                        positionsToAdd.add(newValue);
                    }
                    continue;
                }
                Position existingPosition = positionsToAdd.stream()
                                                          .filter(position -> position.getAssortment().getMeta().equals(oldValue.getMeta()))
                                                          .findFirst().orElse(null);
                if (nonNull(existingPosition)) {
                    existingPosition.setQuantity(existingPosition.getQuantity() + oldValue.getQuantity());
                } else {
                    oldValue.setQuantity(oldValue.getQuantity());
                    positionsToAdd.add(oldValue);
                }
            }
        }
        positionsToAdd = positionsToAdd.stream().filter(position -> position.getQuantity() > 0).collect(Collectors.toList());
        positionsToAdd.forEach(position -> {
            position.setReserve(null);
            position.setPrice(position.getPrice()*100);
        });
        if (positionsToAdd.isEmpty()) {
            return false;
        }
        customerOrder.getPositions().setRows(positionsToAdd);
        customerOrder.setState(PREDVARITELNYI_VNUTRENNIY);
        CustomerOrder newOrder = customerOrderService.create(customerOrder);
        int wholeSum = sumOfOrder + newOrder.getSum();
        int percent = sumOfOrder / (wholeSum / 100);
        HSSFWorkbook excelFromCustomerOrder = excelService.getExcelFromCustomerOrder(customerOrder, percent);
        String fileName = "customerOrder-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".xls";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            excelFromCustomerOrder.write(baos);
        } catch (IOException e) {
            throw new AppException("Excel write failed.");
        }
        emailService.sendEmailsWithAttachment("Неотгруженные товары", Mails.getList(), baos, fileName);
        return true;
    }

    private void setNewName(CustomerOrder customerOrder) {
        String name = customerOrder.getName();
        String[] split = name.split("-");
        if (split.length >= 2) {
            String lastCharacter = split[split.length - 1];
            if (StringUtils.isNumeric(lastCharacter)) {
                customerOrder.setName(name.substring(0, name.lastIndexOf("-")) + "-" + (Integer.parseInt(lastCharacter) + 1));
            } else {
                customerOrder.setName(name + "-1");
            }
        } else {
            customerOrder.setName(name + "-1");
        }
    }
}
