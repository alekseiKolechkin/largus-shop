package ru.largusshop.internal_orders.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.largusshop.internal_orders.model.Counterparty;
import ru.largusshop.internal_orders.model.CustomerOrder;
import ru.largusshop.internal_orders.model.Demand;
import ru.largusshop.internal_orders.model.Employee;
import ru.largusshop.internal_orders.model.Meta;
import ru.largusshop.internal_orders.model.Organization;
import ru.largusshop.internal_orders.model.Position;
import ru.largusshop.internal_orders.model.State;
import ru.largusshop.internal_orders.model.StockReport;
import ru.largusshop.internal_orders.model.Store;
import ru.largusshop.internal_orders.model.Supply;
import ru.largusshop.internal_orders.utils.Mails;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
        List<CustomerOrder> customerOrders = customerOrderService.filter("expand=positions.assortment,agent&state.id=" + INTERNAL_ORDER_STATE_ID);
        if (isNull(customerOrders) || customerOrders.isEmpty()) {
            return "Заказы со статусом \"Внутренний заказ\" не найдены.";
        }

        Thread.sleep(100L);
        List<Demand> createdDemands = new ArrayList<>();
        for (CustomerOrder customerOrder : customerOrders) {
            StockReport stockReport = reportService.getStockReportByDocWithId(customerOrder.getId());
            Thread.sleep(100L);
            Demand template = demandService.getTemplateBasedOnCustomerOrder(customerOrder);
            Thread.sleep(100L);
            List<Store> stores = storeService.getAllStores();
            List<Organization> organizations = organizationService.getAllOrganizations();
            Employee owner = customerOrder.getAgent().getOwner();
            Store destinationStore = stores.stream()
                                           .filter(store -> store.getOwner().equals(owner))
                                           .findFirst()
                                           .get();
            Organization destinationOrganization = organizations.stream()
                                                                .filter(organization -> organization.getOwner().equals(owner))
                                                                .findFirst()
                                                                .get();
            Thread.sleep(100L);
            for (Position position : template.getPositions().getRows()) {
                Position stockPosition = stockReport.getRows()
                                                    .get(0)
                                                    .getPositions()
                                                    .stream()
                                                    .filter(p -> p.getMeta().getHref().equals(position.getAssortment().getMeta().getHref()))
                                                    .findFirst()
                                                    .get();
                int overhead = position.getQuantity() - stockPosition.getStock();
                Integer cost = stockPosition.getCost();
                if (overhead > 0) {
                    Integer buyPrice = position.getAssortment().getBuyPrice().getValue();
                    if (buyPrice == 0) {
                        emailService.sendEmails("У товара с кодом: " + position.getAssortment().getCode() + " нет закупочной цены.", Mails.getList());
                    }
                    cost = cost + overhead * buyPrice;
                }
                position.setCost(cost);
                position.setPrice(cost / position.getQuantity());
            }
            Demand createdDemand = demandService.create(template);
            createdDemand.getPositions()
                         .getRows()
                         .stream()
                         .forEach(position -> position.setCost(template.getPositions()
                                                                       .getRows()
                                                                       .stream()
                                                                       .filter(pos -> pos.getAssortment()
                                                                                         .getMeta()
                                                                                         .getHref()
                                                                                         .equals(position.getAssortment()
                                                                                                         .getMeta()
                                                                                                         .getHref()))
                                                                       .findAny()
                                                                       .get()
                                                                       .getCost()));
            Supply supply = Supply.builder().agent(OOO_SUPPLIER)
                                  .organization(destinationOrganization)
                                  .store(destinationStore)
                                  .positions(template.getPositions())
                                  .build();
            supplyService.create(supply);
            createdDemands.add(createdDemand);
            CustomerOrder orderWithNewStatus = CustomerOrder.builder()
                    .state(INTERNAL_ORDER_DEMANDED_STATE)
                    .applicable(false)
                    .build();
            customerOrderService.update(customerOrder.getId(), orderWithNewStatus);
            Thread.sleep(100L);
        }
        HSSFWorkbook workbook = excelService.getExcelFromDemands(createdDemands);
        String fileName = "demands-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".xls";
//        try (FileOutputStream out = new FileOutputStream(new File(fileName))) {
//            workbook.write(out);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        emailService.sendEmailsWithAttachment("Внутренние заказы обработаны.", Mails.getList(), baos, fileName);
        return "Внутренние заказы обработаны.";
    }
}
