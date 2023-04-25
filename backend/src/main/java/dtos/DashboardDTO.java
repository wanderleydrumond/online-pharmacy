package dtos;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement
@Getter
@Setter
public class DashboardDTO {
	private Short totalClients, totalProducts, totalCarts, totalSignIns;
	private Float totalValueConcludedOrders, totalValueConcludedOrdersCurrentMonth, totalValueConcludedOrdersLastMonth;
	private List<UserDTO> visitorsDTO;
}