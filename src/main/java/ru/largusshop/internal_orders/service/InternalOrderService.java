package ru.largusshop.internal_orders.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.largusshop.internal_orders.model.Counterparty;
import ru.largusshop.internal_orders.model.CustomerOrder;
import ru.largusshop.internal_orders.model.Demand;
import ru.largusshop.internal_orders.model.Employee;
import ru.largusshop.internal_orders.model.Group;
import ru.largusshop.internal_orders.model.Meta;
import ru.largusshop.internal_orders.model.Organization;
import ru.largusshop.internal_orders.model.Position;
import ru.largusshop.internal_orders.model.State;
import ru.largusshop.internal_orders.model.StockReport;
import ru.largusshop.internal_orders.model.Store;
import ru.largusshop.internal_orders.model.Supply;
import ru.largusshop.internal_orders.utils.Mails;
import ru.largusshop.internal_orders.utils.exception.AppException;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Service
public class InternalOrderService {
    private final String INTERNAL_ORDER_STATE_ID = "3c707d19-9e08-11e8-9107-504800052a7a";
    private final Counterparty OOO_SUPPLIER = Counterparty.builder()
                                                          .meta(Meta.builder()
                                                                    .href("https://online.moysklad.ru/api/remap/1.1/entity/counterparty/cbf563a1-f362-11e4-7a40-e8970010f056")
                                                                    .metadataHref("https://online.moysklad.ru/api/remap/1.1/entity/counterparty/metadata")
                                                                    .type("counterparty")
                                                                    .mediaType("application/json")
                                                                    .build())
                                                          .build();
    private final State INTERNAL_ORDER_DEMANDED_STATE = State.builder()
                                                             .meta(Meta.builder()
                                                                       .href("https://online.moysklad.ru/api/remap/1.1/entity/customerorder/metadata/states/8891f30c-a558-11e8-9ff4-315000279879")
                                                                       .metadataHref("https://online.moysklad.ru/api/remap/1.1/entity/customerorder/metadata")
                                                                       .type("state")
                                                                       .mediaType("application/json")
                                                                       .build())
                                                             .build();
    private final Organization OOO_MAVIKO = Organization.builder()
                                                        .meta(Meta.builder()
                                                                  .href("https://online.moysklad.ru/api/remap/1.1/entity/organization/4769a1bb-2861-11e8-9107-5048000aa8cd")
                                                                  .metadataHref("https://online.moysklad.ru/api/remap/1.1/entity/organization/metadata")
                                                                  .type("organization")
                                                                  .mediaType("application/json")
                                                                  .build())
                                                        .build();

    private final Employee NIKOLAY = Employee.builder()
                                             .meta(Meta.builder()
                                                       .href("https://online.moysklad.ru/api/remap/1.1/entity/employee/cbeab98a-f362-11e4-7a40-e8970010f02e")
                                                       .metadataHref("https://online.moysklad.ru/api/remap/1.1/entity/employee/metadata")
                                                       .type("employee")
                                                       .mediaType("application/json")
                                                       .build())
                                             .build();
    private final Organization SIG = Organization.builder()
                                                 .meta(Meta.builder()
                                                           .href("https://online.moysklad.ru/api/remap/1.1/entity/organization/cbf3e8d8-f362-11e4-7a40-e8970010f053")
                                                           .metadataHref("https://online.moysklad.ru/api/remap/1.1/entity/organization/metadata")
                                                           .type("organization")
                                                           .mediaType("application/json")
                                                           .build())
                                                 .build();

    private final Group MAIN = Group.builder().meta(Meta.builder()
                                                        .href("https://online.moysklad.ru/api/remap/1.1/entity/group/cc193245-f362-11e4-90a2-8ecb0000a5a1")
                                                        .metadataHref("https://online.moysklad.ru/api/remap/1.1/entity/group/metadata")
                                                        .type("group")
                                                        .mediaType("application/json")
                                                        .build())
                                    .build();
    @Autowired
    private CustomerOrderService customerOrderService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private DemandService demandService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private SupplyService supplyService;
    @Autowired
    private ExcelService excelService;
    @Autowired
    private EmailService emailService;

    public String createInternalOrdersFormCustomerOrders() throws Exception {
        List<CustomerOrder> customerOrders = customerOrderService.filter("expand=owner,positions.assortment,agent.owner&state.id=" + INTERNAL_ORDER_STATE_ID);
        if (isNull(customerOrders) || customerOrders.isEmpty()) {
            return "Заказы со статусом \"Внутренний заказ\" не найдены.";
        }

        Thread.sleep(100L);
        List<Demand> createdDemands = new ArrayList<>();
        for (CustomerOrder customerOrder : customerOrders) {
            //Получить остатки
            StockReport stockReport = reportService.getStockReportByDocWithId(customerOrder.getId());
            Thread.sleep(100L);
            //Создать шаблон отгрузки
            Demand templateDemand = demandService.getTemplateBasedOnCustomerOrder(customerOrder);
            templateDemand.setName(customerOrder.getName());
            Thread.sleep(100L);
            //Отредактировать шаблон
            List<Store> stores = storeService.getAllStores();
            List<Organization> organizations = organizationService.getAllOrganizations();
            Employee orderOwner = customerOrder.getAgent().getOwner();
            templateDemand.setOwner(NIKOLAY);
            templateDemand.setOrganization(SIG);
            templateDemand.setGroup(MAIN);
            Store destinationStore = getDestinationStore(stores, orderOwner);
            Organization destinationOrganization = getDestinationOrganization(organizations, orderOwner);
            Thread.sleep(100L);
            //Задать стоимость позиций
            for (Position position : templateDemand.getPositions().getRows()) {
                Position stockPosition = getStockPosition(stockReport, position);
                Float overhead = position.getQuantity() - (stockPosition.getStock() < 0 ? 0 : stockPosition.getStock());
                setPositionPriceAndCost(position, stockPosition, overhead);
            }
            //Создать отгрузку
            Demand createdDemand = demandService.create(templateDemand);
            setCostForCreatedDemandPositions(templateDemand, createdDemand);
            createdDemands.add(createdDemand);
            //Создать приемку
            Supply supply = Supply.builder().agent(OOO_SUPPLIER)
                                  .organization(destinationOrganization)
                                  .store(destinationStore)
                                  .positions(templateDemand.getPositions())
                                  .applicable(false)
                                  .owner(orderOwner)
                                  .group(orderOwner.getGroup())
                                  .build();
            supplyService.create(supply);
            //создать новый заказ с удаленными позицями
            customerOrderService.createCustomerOrderFromAudit(customerOrder);
            Thread.sleep(100L);
            //Обновить статус заказа и снять проведение
            CustomerOrder orderWithNewStatus = CustomerOrder.builder()
                                                            .state(INTERNAL_ORDER_DEMANDED_STATE)
                                                            .applicable(false)
                                                            .build();
            customerOrderService.update(customerOrder.getId(), orderWithNewStatus);
            Thread.sleep(100L);

        }
        //Создать ексель и отправить письма
        HSSFWorkbook workbook = excelService.getExcelFromDemands(createdDemands);
        String fileName = "demands-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".xls";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        emailService.sendEmailsWithAttachment("Внутренние заказы обработаны.", Mails.getList(), baos, fileName);
        return "Внутренние заказы обработаны.";
    }

    private void setCostForCreatedDemandPositions(Demand templateDemand, Demand createdDemand) {
        createdDemand.getPositions()
                     .getRows()
                     .stream()
                     .forEach(position -> position.setCost(templateDemand.getPositions()
                                                                         .getRows()
                                                                         .stream()
                                                                         .filter(pos -> pos.getAssortment()
                                                                                           .getMeta()
                                                                                           .getHref()
                                                                                           .split("\\?")
                                                                                 [0]
                                                                                 .equals(position.getAssortment()
                                                                                                 .getMeta()
                                                                                                 .getHref()
                                                                                                 .split("\\?")
                                                                                                 [0]))
                                                                         .findAny()
                                                                         .orElseThrow(() -> new AppException("Couldn't create supply. Position cost undefined:" + position.getAssortment().getName()))
                                                                         .getCost()));
    }

    private void setPositionPriceAndCost(Position position, Position stockPosition, Float overhead) {
        Integer cost = stockPosition.getCost();
        if (overhead > 0) {
            Integer buyPrice = isNull(position.getAssortment().getBuyPrice()) ? position.getAssortment().getProduct().getBuyPrice().getValue()
                    : position.getAssortment().getBuyPrice().getValue();
            if (buyPrice == 0) {
                emailService.sendEmails("У товара с кодом: " + position.getAssortment().getCode() + " нет закупочной цены.", Mails.getList());
            }
            cost = cost + Math.round(overhead * buyPrice);
        }
        position.setCost(cost);
        position.setPrice(Math.round(cost / position.getQuantity()));
    }

    private Position getStockPosition(StockReport stockReport, Position position) {
        return stockReport.getRows()
                          .get(0)
                          .getPositions()
                          .stream()
                          .filter(p -> {
                              String href = position.getAssortment().getMeta().getHref();
                              href = href.split("\\?")[0];
                              return p.getMeta().getHref().equals(href);
                          })
                          .findFirst()
                          .orElseThrow(() -> new AppException("Position not found in stock report: " + position.getAssortment().getName()));
    }

    private Organization getDestinationOrganization(List<Organization> organizations, Employee orderOwner) {
        return organizations.stream()
                            .filter(organization -> organization.getOwner().getMeta().equals(orderOwner.getMeta()))
                            .findFirst()
                            .orElseThrow(() -> new AppException("Destination organization not found for owner: " + orderOwner.getMeta().getHref()));
    }

    private Store getDestinationStore(List<Store> stores, Employee orderOwner) {
        return stores.stream()
                     .filter(store -> store.getOwner().getMeta().equals(orderOwner.getMeta()))
                     .findFirst()
                     .orElseThrow(() -> new AppException("Destination store not found for owner:" + orderOwner.getMeta().getHref()));
    }
}
