package dtos;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement
@Getter
@Setter
public class DashboardDTO {
	Short totalClients, totalProducts, totalOngoingOrders;
	Float totalValueConcludedOrders, totalValueConcludedOrdersCurrentMonth, totalValueConcludedOrdersLastMonth;
	List<UserDTO> usersDTO;
}